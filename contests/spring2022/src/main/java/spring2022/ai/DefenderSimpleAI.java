package spring2022.ai;

import spring2022.Action;
import spring2022.Hero;
import spring2022.State;
import spring2022.Unit;

public class DefenderSimpleAI {
  private static State state;
  private static Hero hero;

  
  static boolean enemyHasAttacker = false;
  
  
  public static void updateTurn(State state) {
    DefenderSimpleAI.state = state;
    detectEnemyAttacker();
  }
  
  
  public static Action think(State state, Hero hero) {
    DefenderSimpleAI.state = state;
    DefenderSimpleAI.hero = hero;

    Action action;
    if ((action = shouldReturnToBase()) != Action.WAIT) return action;
    if ((action = shouldPanicWind()) != Action.WAIT) return action;
    
    if ((action = shouldRegularWind()) != Action.WAIT) return action;
    
    if ((action = attackClosestToBase()) != Action.WAIT) return action;
    
    if ((action = shouldPatrol()) != Action.WAIT) return action;
    return Action.WAIT;
  }
  
  
  private static Action shouldRegularWind() {
    if (state.mana[0] < 10) return Action.WAIT;
    
    int monsterInWindRange = 0;
    for (int u= 0;u<state.unitsFE;u++) {
      Unit monster = state.fastUnits[u];

      if (monster.isDead()) continue;

      int dist2Base = State.myBase.dist2(monster.pos);
      int dist2ToHero = hero.pos.dist2(monster.pos);
      if (dist2ToHero < State.WIND_RANGE2 
          && dist2Base < 5000*5000
          && monster.shieldLife <= 0
          ) {
        monsterInWindRange++;
      }
    }
    
    if (monsterInWindRange > 0) {
      // TODO do better angle ...
      return Action.doWind(State.oppBase);
    }
    
    
    return Action.WAIT;
  }


  private static void detectEnemyAttacker() {
    if (enemyHasAttacker) {
      return;
    }
    
    for (Hero hero : state.oppHeroes) {
      if (hero.isInRange(State.myBase, 8000)) {
        enemyHasAttacker = true;
      }
    }
  }


  private static Action shouldPatrol() {
    Action action = new Action();
    int decal = hero.id >= 3 ? (hero.id - 3) + 1 : hero.id + 1;
    int distX = (int) (6000  * Math.cos(decal * Math.PI * 25 / 180));
    int distY = (int) (6000 * Math.sin(decal * Math.PI * 25 / 180));
    action.moveTo(distX, distY);
    return action;
  }


  private static Action shouldReturnToBase() {
    if (hero.pos.dist(State.myBase) > 10_000) {
      return Action.doMove(State.myBase);
    }
    return Action.WAIT;
  }


  private static Action shouldPanicWind() {
    if (state.mana[0] < 10) return Action.WAIT;
    
    int monsterInWindRange = 0;
    for (int u= 0;u<state.unitsFE;u++) {
      Unit monster = state.fastUnits[u];

      if (monster.isDead()) continue;

      int dist2Base = State.myBase.dist2(monster.pos);
      int dist2ToHero = hero.pos.dist2(monster.pos);
      if (dist2ToHero < State.WIND_RANGE2 
          && dist2Base < 1000*1000
          && monster.shieldLife <= 0 ) {
        monsterInWindRange++;
      }
    }
    
    if (monsterInWindRange > 0) {
      // TODO do better angle ...
      return Action.doWind(State.oppBase);
    }
    
    
    return Action.WAIT;
  }

  private static Action attackClosestToBase() {
    int bestDist = Integer.MAX_VALUE;
    Unit bestUnit = null;

    boolean isUnderThreat = hasOppHeroNearBase();
    
    
    for (int u= 0;u<state.unitsFE;u++) {
      Unit monster = state.fastUnits[u];


      if (monster.isDead()) continue;
      
      int dist2 = State.myBase.dist2(monster.pos);

      if (bestDist > dist2) {
        bestDist = dist2;
        bestUnit = monster;
      }
    }

    if (bestUnit != null) {
      
      if (!bestUnit.isInRange(State.myBase, State.BASE_TARGET_DIST + 2000)
          && bestUnit.health > 20) {
          System.err.println("Don't attack closest unit when too much health and out of base zone + 2000");
            return Action.WAIT;
      }
        
      // TODO find a better position ?
      return Action.doMove(bestUnit.pos);
      
    } else {
      return Action.WAIT;
    }

  }

  private static boolean hasOppHeroNearBase() {
    for (int h=0;h<3;h++) {
      if (state.oppHeroes[h].isInFog()) continue;
      if (state.oppHeroes[h].isInRange(State.myBase, 6000)) {
        return true;
      }
    }
    
    return false;
  }


}
