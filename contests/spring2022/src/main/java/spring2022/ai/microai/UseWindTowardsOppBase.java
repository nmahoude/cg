package spring2022.ai.microai;

import java.util.concurrent.ThreadLocalRandom;

import spring2022.Action;
import spring2022.Hero;
import spring2022.Pos;
import spring2022.State;
import spring2022.Unit;
import spring2022.ai.MicroAI;

public class UseWindTowardsOppBase implements MicroAI {
  public static final UseWindTowardsOppBase i = new UseWindTowardsOppBase();
  
  private static final ThreadLocalRandom random = ThreadLocalRandom.current();
  
  private static final int MIN_DISTANCE_TO_BASE_TO_WIND = State.BASE_TARGET_DIST + 1500;
  private static final int MIN_MOB_HEALTH_TO_WIND = 10;

  
  private static final Pos[] TargetDirection = new Pos[] { new Pos(17630, 5000), new Pos(13630, 9000), State.oppBase };

  
  @Override
  public Action think(State state, Hero hero) {
    if (state.mana[0] < 10) {
      return Action.WAIT;
    }
      
    int monsterCount = 0;
    Unit bestMonster = null;
    for (int u= 0;u<state.unitsFE;u++) {
      Unit monster = state.fastUnits[u];

      if (monster.isDead()) continue;
      if (monster.health < MIN_MOB_HEALTH_TO_WIND) continue;
      if (monster.hasShield()) continue;
      
      if (monster.isInRange(hero.pos, State.WIND_RANGE)
          && monster.isInRange(State.oppBase, MIN_DISTANCE_TO_BASE_TO_WIND)
          ) {
        bestMonster = monster;
        monsterCount++;
      }
    }

    if (monsterCount > 0) {
      Pos target = state.getBestQuadrantToThrow();
      
      return Action.doWind(target);
    }    
    return Action.WAIT;
  }

}
