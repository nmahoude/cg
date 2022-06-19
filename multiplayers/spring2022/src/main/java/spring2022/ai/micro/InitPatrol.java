package spring2022.ai.micro;

import spring2022.Action;
import spring2022.Hero;
import spring2022.Pos;
import spring2022.State;
import spring2022.ai.MicroAI;

public class InitPatrol implements MicroAI {

  static Pos[] positions = new Pos[] { new Pos(4252, 8491), new Pos(8374, 457), new Pos(8500, 8000) };
  
  
  
  public Action think(State state, Hero hero) {
    
    return Action.doMove(positions[hero.index()]);
    
  }
}
