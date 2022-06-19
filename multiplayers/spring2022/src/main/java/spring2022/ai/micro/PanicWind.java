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
      if (monster.hasShield()) continue;
      
      int distBase = State.myBase.fastDist(monster.pos);
      int distToHero = hero.pos.fastDist(monster.pos);
      
      if (distToHero < State.WIND_RANGE 
          && distBase < 1500
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

}
