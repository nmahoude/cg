package spring2022.ai;

import spring2022.Action;
import spring2022.Hero;
import spring2022.Player;
import spring2022.Pos;
import spring2022.State;
import spring2022.Unit;

public class AttackerSimpleAI {
  public static int BASE_ATTRACTION_RADIUS = 7000;
  private static final int SHIELD_ATTRACTION = State.BASE_TARGET_DIST;
  
  
  private static State state;
  private static Hero hero;

  public static void updateTurn(State state) {
    AttackerSimpleAI.state = state;
  }

  
  public static Action attack(State state, Hero hero) {
    AttackerSimpleAI.state = state;
    AttackerSimpleAI.hero = hero;
    
    Action action;

    if (Player.turn < 100) {
      if ((action = doPatrolAndKill()) != Action.WAIT) return action;
    }
    
    
    // if ((action = shouldReturnToEnnemyBase()) != Action.WAIT) return action;
    // if ((action = shouldShieldMob()) != Action.WAIT) return action;
    
    if ((action = doWind()) != Action.WAIT) return action;
    if ((action = doMoveForWind()) != Action.WAIT) return action;
    if ((action = doControl()) != Action.WAIT) return action;
    
    
    // if ((action = doAttackNotHealthyUnit()) != Action.WAIT) return action;
    if ((action = doPatrol()) != Action.WAIT) return action;
    
    return action;
  }

  private static Action doControl() {
    if (! hero.isInRange(State.oppBase, 8000)) {
      return Action.WAIT;
    }
    
    for (int u= 0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];

      if (unit.isDead()) continue;
      if (unit.hasShield()) continue;
      if (unit.health < 20) continue;
      if (unit.isControlled > 0) continue;
      
      if (!unit.isInRange(hero.pos, State.CONTROL_RANGE)) continue;
      
      return Action.doControl(unit.id, state.getBestQuadrantToThrow());
      
    }
    return Action.WAIT;
  }


  private static Action doPatrolAndKill() {
    
    Unit unit = state.getClosestUnit(hero.pos);
    if (unit != null) {
      return Action.doMove(unit.pos);
    } else {
      return Action.doMove(10000, 1500);
    }
  }


  private static Action doAttackNotHealthyUnit() {

    for (int u= 0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];

      if (!unit.isInRange(hero.pos, 1000)) continue;
      
      if (unit.health <= 13) {
        System.err.println("ATTACKER : attack not healthy unit "+unit.id +" with health "+unit.health);
        return Action.doMove(unit.pos);
      }
    }
    
    return Action.WAIT;
  }

  private static Action shouldShieldMob() {
    if (state.mana[0] < 10) return Action.WAIT;
    
    // only shield if a lot of enemies into base zone
    int unitInZoneCount = 0;
    for (int u= 0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];

      if (!unit.isInRange(State.oppBase, State.BASE_TARGET_DIST)) continue;
      unitInZoneCount++;
    }    
    
    if (unitInZoneCount < 3) return Action.WAIT;
    
    for (int u= 0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];

      if (unit.hasShield()) continue;
      if (!unit.isInRange(hero.pos, State.SHIELD_RANGE)) continue;
      
      if (unit.isInRange(State.oppBase, SHIELD_ATTRACTION)
          && unit.health > 10) {
        return Action.doShield(unit.id);
      }
    }
    
    return Action.WAIT;
  }

  private static Action shouldReturnToEnnemyBase() {
    if (!hero.isInRange(State.oppBase, BASE_ATTRACTION_RADIUS)) {
      return Action.doMove(State.oppBase);
    } 
    
    return Action.WAIT;
  }

  private static Action doMoveForWind() {
    
    // check unit I can see but are too far to wind them up, go closer to do it !
    for (int u= 0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];

      
      if (unit.isInRange(hero.pos, State.HERO_VIEW_RADIUS) 
          && unit.isInRange(State.oppBase, 6500)
          && unit.health > 13
          && !unit.isInRange(State.oppBase, State.BASE_TARGET_DIST)) {

        return Action.doMove(unit.pos);
      }
    }
    
    return Action.WAIT;
  }

  private static Action doPatrol() {
    Action action = new Action();
    action.moveTo(13000,5000);
    return action;
  }

  private static Action doWind() {
    if (state.mana[0] < 10) {
      return Action.WAIT;
    }
      
    int monsterCount = 0;
    for (int u= 0;u<state.unitsFE;u++) {
      Unit monster = state.fastUnits[u];

      if (monster.isDead()) continue;
      if (monster.health < 14) continue;
      
      if (monster.isInRange(hero.pos, State.WIND_RANGE)
          && monster.isInRange(State.oppBase, 6500)
          ) {
        monsterCount++;
      }
    }

    if (monsterCount > 0) {
      
      Pos pos = state.getBestQuadrantToThrow();
      return Action.doWind(pos);
    }    
    return Action.WAIT;
  }

}
