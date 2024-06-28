package spring2022.ai.microai;

import spring2022.Action;
import spring2022.Hero;
import spring2022.Pos;
import spring2022.State;
import spring2022.Unit;
import spring2022.ai.MicroAI;

public class InitPatrol implements MicroAI {

  static Pos[] positions = new Pos[] { new Pos(4252, 8491), new Pos(8374, 457), new Pos(5966, 7344) };
  
  
  
  public Action think(State state, Hero hero) {
    int id = hero.id >= 3 ? hero.id - 3 : hero.id;
    
    for (int u= 0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];

      if (unit.isInViewRange(hero)) {
        return Action.WAIT;
      }
    }
    
    return Action.doMove(positions[id]);
    
  }
}
