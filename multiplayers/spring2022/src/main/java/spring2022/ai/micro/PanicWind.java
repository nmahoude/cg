package spring2022.ai.micro;

import spring2022.Action;
import spring2022.Hero;
import spring2022.State;
import spring2022.Unit;
import spring2022.ai.MicroAI;

public class PanicWind implements MicroAI {

  @Override
  public Action think(State state, Hero hero) {

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

}
