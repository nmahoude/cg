package spring2022.ai.microai;

import spring2022.Action;
import spring2022.Hero;
import spring2022.State;
import spring2022.Unit;
import spring2022.ai.MicroAI;

public class FollowMobIntoBse implements MicroAI {

  public static final FollowMobIntoBse i = new FollowMobIntoBse();

  @Override
  public Action think(State state, Hero hero) {

    Unit intoBase = null;
    int bestDist = Integer.MAX_VALUE;
    
    for (int u= 0;u<state.unitsFE;u++) {
      Unit monster = state.fastUnits[u];

      if (monster.isDead()) continue;
      if (monster.health < 10) continue;
      if (monster.hasShield()) continue;
      
      int distToBase = monster.pos.fastDist(State.oppBase);
      int distToMe = monster.pos.fastDist(hero.pos);
      if (distToBase < State.BASE_TARGET_DIST
          ) {
        if (distToMe < bestDist & distToMe > 800) {
          bestDist = distToMe;
          intoBase = monster;
        }
      }
    }

    if (intoBase != null) {
      return Action.doMove(intoBase.pos);
    }
    
    
    return Action.WAIT;
  }

}
