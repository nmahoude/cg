package spring2022.ag;

import spring2022.Action;
import spring2022.Hero;
import spring2022.Pos;
import spring2022.State;
import spring2022.Unit;
import spring2022.ai.TriAction;

public class Simulator {

  public static final int MAX_BOTS = 2;
  public static final boolean ALLOW_WIND = true;
  public static final boolean ALLOW_CONTROL = true;
  private static final boolean ALLOW_SHIELD = true;

  public static void apply(LightState state, int d, TriAction actions) {
    apply(state, d, actions, null);
  }

  
  public static void apply(LightState state, int atDepth, TriAction myActions, TriAction oppActions) {
    Action[] actions = myActions.actions;
    
    resetAll(state);
    
    // check controls :/
    if (atDepth == 0) {
      for (int h = 0; h < MAX_BOTS; h++) {
        Hero hero = state.hero[h];
        if (hero.isControlled) {
          myActions.actions[h].type = Action.TYPE_WAIT;
        }
      }
    }
    
    // do controls
    if (ALLOW_CONTROL) {
      for (int h = 0; h < MAX_BOTS; h++) {
        if (actions[h].type == Action.TYPE_CONTROL) {
          if (state.mana >= 10) {
            state.mana-=10;
            doControl(state, state.hero[h], actions[h].targetEntity, actions[h].target);
            state.extraBonus = 1_000_000; // for the control, je ne sais pas comment le faire autrement ....
          } else {
            actions[h].type = Action.TYPE_WAIT;
          }
        }
      }
    }
    
    if (ALLOW_SHIELD) {
      for (int h = 0; h < MAX_BOTS; h++) {
        if (actions[h].type == Action.TYPE_SHIELD) {
          if (state.mana >= 10) {
            state.mana-=10;
            state.hero[h].shieldLife = State.SHIELD_LIFE;
          } else {
            actions[h].type = Action.TYPE_WAIT;
          }
        }
      }
    }
    
    // move heroes
    for (int h = 0; h < MAX_BOTS; h++) {
      if (actions[h].type == Action.TYPE_MOVE) {
        if (state.hero[h].dx == 0 && state.hero[h].dy == 0) {
          state.hero[h].pos.copyFrom(actions[h].target);
          state.hero[h].pos.addAndSnap(0, 0);
        }
      }
    }

    // damage
    for (int u = 0; u < state.unitsFE; u++) {
      Unit unit = state.units[u];

      for (int h = 0; h < MAX_BOTS; h++) {
        Hero hero = state.hero[h];
        checkDamage(state, unit, hero);
      }
      
      if (unit.health <= 0 ) {
        state.kill(u);
        u--; // hack need to update the replaced unit
      }
    }

    // do wind
    if (ALLOW_WIND) {
      for (int h = 0; h < MAX_BOTS; h++) {
        if (actions[h].type == Action.TYPE_WIND) {
          if (state.mana >= 10) {
            state.mana -= 10;
            doWind(state, 0, state.hero[h].pos, actions[h].target);
          } else {
            actions[h].type = Action.TYPE_WAIT;
          }
        }
      }
      
      if (atDepth == 0 && oppActions != null) {
        for (int h = 0; h < 3; h++) {
           if (oppActions.actions[h].type == Action.TYPE_WIND) {
            doWind(state, 1, state.oppHeroes[h].pos, oppActions.actions[h].target);
          }
        }
      }
    }
    
   
    // move units
    for (int u = 0; u < state.unitsFE; u++) {
      Unit unit = state.units[u];
      
      if (unit.isPushed) {
        unit.pos.addAndSnap((int)unit.dx, (int)unit.dy);
      } else if (unit.controlTargetCount > 0) {
        double dx = unit.controlTarget.x / unit.controlTargetCount - unit.pos.x;
        double dy = unit.controlTarget.y / unit.controlTargetCount - unit.pos.y;
        double dist = (int) Math.sqrt(dx * dx + dy * dy);
        dx = dx * State.MOB_MOVE/ dist;
        dy = dy * State.MOB_MOVE / dist;
        unit.pos.add(dx, dy);
        unit.speed.set((int)dx, (int)dy);
      } else {
        // normal move
        // TODO check if out of map
        unit.pos.add(unit.speed);
      }
      

      if (unit.isInRange(State.myBase, State.BASE_KILL_RADIUS)) {
        state.health--; // do damage to base
        
        unit.health = 0;
        state.kill(u);
        u--;
        continue;
      } else if (unit.isInRange(State.oppBase, State.BASE_KILL_RADIUS)) {
        unit.health = 0;
        state.kill(u);
        u--;
        continue;
      } else if (unit.isInRange(State.myBase, State.BASE_TARGET_DIST)) {
        unit.pos.addAndSnap(0, 0); // reput in map
        unit.speed.alignTo(unit.pos, State.myBase, State.MOB_MOVE);
      } else if (unit.isInRange(State.oppBase, State.BASE_TARGET_DIST)) {
        unit.pos.addAndSnap(0, 0); // reput in map
        unit.speed.alignTo(unit.pos, State.oppBase, State.MOB_MOVE);
      } else {
        if (!unit.pos.insideOfMap()) {
          unit.health = 0;
          state.kill(u);
          u--;
          continue;
        }
      }

      // reset nextTarget !
      unit.controlTargetCount = unit.controlNextTargetCount; 
      unit.controlTarget.copyFrom(unit.controlNextTarget);
      unit.controlNextTargetCount = 0;
      unit.controlNextTarget.set(0,0);
      
    }
    
    
    // TODO les heroes move avant l'application des wind , difficile de considérer que les ennemis ne vont pas bouger ... (sauf si il control/shield/wind)
    // Compromis : on ne fait le push qu'a t = 0
    if (atDepth == 0) {
      // move heroes (wind), on part du principe que les wind adv ne peuvent arriver qu'au tour 0
      // c'est tellemnt aléatoire après (déjà que la ....)
      for (int i = 0; i < MAX_BOTS; i++) {
        Hero h = state.hero[i];
        h.pos.addAndSnap((int)h.dx, (int)h.dy);

      }

      
      Hero[] oppHeroes = state.oppHeroes;
      for (int i = 0; i < oppHeroes.length; i++) {
        Hero h = oppHeroes[i];
        h.pos.addAndSnap((int)h.dx, (int)h.dy);
      }
    }
    
  }


  private static void checkDamage(LightState state, Unit unit, Hero hero) {
    if (unit.isInHitRange(hero)) {
      unit.health -= 2;
      state.mana += 2;
      if (!unit.isInRange(State.myBase, State.BASE_TARGET_DIST)) {
        state.wildMana += 2; // MIAM
      }
    }
  }

  private static void resetAll(LightState state) {
    state.extraBonus = 0;
    
    for (int u = 0; u < state.unitsFE; u++) {
      Unit unit = state.units[u];
      unit.isPushed = false;
      unit.dx = 0;
      unit.dy = 0;
    }
    
    for (int i = 0; i < MAX_BOTS; i++) {
      Hero h = state.hero[i];
      h.dx = 0;
      h.dy = 0;
      h.isControlled = false;
    }
    
    for (int i = 0; i < 3; i++) {
      Hero h = state.oppHeroes[i];
      h.dx = 0;
      h.dy = 0;
      h.isControlled = false;
    }
  }

  private static void doControl(LightState state, Hero hero, int id, Pos target) {
    
    // on heroes
    Hero opp = null;
    Hero[] oppHeroes = state.oppHeroes;
    for (int i = 0; i < oppHeroes.length; i++) {
      Hero h = oppHeroes[i];
      if (h.id != id) continue;
      if (!h.hasShield() && h.isInRange(hero, State.CONTROL_RANGE) ) {
        opp = h;
      }
      break; // break in any case if id is found
    }
    if (opp != null) {
      double dx = target.x - opp.pos.x;
      double dy = target.y - opp.pos.y;
      double dist = (int) Math.sqrt(dx * dx + dy * dy);
      dx = dx * State.HERO_MAX_MOVE / dist;
      dy = dy * State.HERO_MAX_MOVE / dist;
      opp.pos.addAndSnap(dx, dy);
      return;
    }
    
    // on units
    Unit mob = null;
    for (int u = 0; u < state.unitsFE; u++) {
      Unit unit = state.units[u];
      if (unit.id != id) continue;
      if (!unit.hasShield() && unit.isInRange(hero.pos, State.CONTROL_RANGE) ) {
        mob = unit;
      }
      break; // break in any case if id is found
    }
    if (mob != null) {
      mob.controlNextTargetCount++;
      mob.controlNextTarget.add(target.x, target.y);
    }
  }

  private static void doWind(LightState state, int who, Pos pos, Pos target) {
    double dx = target.x - pos.x;
    double dy = target.y - pos.y;
    double dist = (int) Math.sqrt(dx * dx + dy * dy);
    dx = (int)(dx * State.WIND_PUSH_DISTANCE / dist);
    dy = (int)(dy * State.WIND_PUSH_DISTANCE / dist);

    for (int u = 0; u < state.unitsFE; u++) {
      Unit unit = state.units[u];

      if (unit.isInFog()) continue;
      if (unit.hasShield()) continue;

      if (unit.isInRange(pos, State.WIND_RANGE)) {
        unit.dx += dx;
        unit.dy += dy;
        unit.isPushed = true;
      }
    }

    if (who == 0) {
      // move him
      for (int i=0;i<3;i++) {
        if (state.oppHeroes[i].pos == Pos.VOID) continue;
        
        if (!state.oppHeroes[i].hasShield() && state.oppHeroes[i].isInRange(pos, State.WIND_RANGE)) {
          state.oppHeroes[i].dx +=dx ;
          state.oppHeroes[i].dy +=dy ;
        }
      }
    } else {
      // move me
      for (int i=0;i<MAX_BOTS;i++) {
        
        if (!state.hero[i].hasShield() && state.hero[i].isInRange(pos, State.WIND_RANGE)) {
          state.hero[i].dx +=dx ;
          state.hero[i].dy +=dy ;
        }
      }
    }

  }

}
