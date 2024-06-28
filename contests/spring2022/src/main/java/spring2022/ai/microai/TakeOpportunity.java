package spring2022.ai.microai;

import spring2022.Action;
import spring2022.Hero;
import spring2022.Pos;
import spring2022.State;
import spring2022.Unit;
import spring2022.ai.MicroAI;

/**
 * check if there is an excellent opportunity to take regardless of everything
 * 
 * 1. wind unit into opp base when we are so close it will be disastrous
 * 
 * 
 * @author nmahoude
 *
 */
public class TakeOpportunity implements MicroAI {

  public static final TakeOpportunity i = new TakeOpportunity();
  
  
  public boolean allin;
  public boolean lastSeenOutside[] = new boolean[3]; // did we saw them far outside of base
  PatrolOppBaseNeighborood patrolOppBaseNeighborood = new PatrolOppBaseNeighborood();
  
  @Override
  public Action think(State state, Hero hero) {
    if (state.mana[0] < 10) return Action.WAIT;

    Action action;
    
    
    //if ((action = allIn(state, hero)) != Action.WAIT) return action;
    
    if ((action = doBigWind(state, hero)) != Action.WAIT) return action;
    
    
    
    return Action.WAIT;
  }

  // see all heroes outside of map, mobcontrol and wind
  private Action allIn(State state, Hero hero) {
    for (int i=0;i<3;i++) {
      if (state.oppHeroes[i].isInFog()) continue;
      
      if (state.oppHeroes[i].isInRange(State.oppBase, State.BASE_TARGET_DIST + 1000)) {
        // reset !
        lastSeenOutside[i] = false;
        // trop proche encore
      } else {
        lastSeenOutside[i] = true;
      }
    }

    if (lastSeenOutside[0] && lastSeenOutside[1] && lastSeenOutside[2]) {
      // balancer tous les mobs vers la base !!!
      allin = true;
      System.err.println("ALL IN ");
      Action action;
      if ((action = forcedWind(state, hero)) != Action.WAIT) return action;
      if ((action = forcedControl(state, hero)) != Action.WAIT) return action;
      return patrolOppBaseNeighborood.think(state, hero);
      
    } else {
      allin = false;
    }
    
    return Action.WAIT;
  }

  private Action forcedControl(State state, Hero hero) {
    for (int u= 0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];

      if (unit.isDead()) continue;
      
      if (!unit.isInRange(hero, State.CONTROL_RANGE)) continue;
      if (unit.hasShield()) continue; // shield
      if (unit.nearBase || unit.threatFor == 2) continue; // pas besoin si elle va déjà dans la direction
      
      Pos target = State.oppBase;
      return Action.doControl(unit.id, target);
    }
    return Action.WAIT;
  }

  private Action forcedWind(State state, Hero hero) {
    int monsterCount = 0;
    for (int u= 0;u<state.unitsFE;u++) {
      Unit monster = state.fastUnits[u];

      if (monster.isDead()) continue;
      if (monster.hasShield()) continue;
      
      if (monster.isInRange(hero.pos, State.WIND_RANGE)) {
        monsterCount++;
      }
    }

    if (monsterCount > 0) {
      return Action.doWind(State.oppBase);
    } else {
      return Action.WAIT;
    }
  }

  private Action doBigWind(State state, Hero hero) {
    for (int u= 0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];

      if (unit.isDead()) continue;
      
      if (unit.hasShield()) continue;
      if (!unit.isInRange(hero, State.WIND_RANGE)) continue;
      
      int dist = unit.pos.dist(State.oppBase);
      int stepsToBase = dist / State.MOB_MOVE;
      
      if (dist < 2200 && unit.health > 2) {
        // direct hit mais il faut qu'il reste un hit 
        return Action.doWind(State.oppBase);
      }
      
      if (dist < 3500) {
        // TODO not the center ?

        // il lui faut un peu de vie quand meme
        if (unit.health >= 2 * (stepsToBase-7)) {
          return Action.doWind(State.oppBase);
        }
      }
    }
    
    
    return Action.WAIT;
  }

}
