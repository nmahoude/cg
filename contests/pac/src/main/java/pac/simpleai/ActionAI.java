package pac.simpleai;

import pac.map.Pos;
import pac.sim.Action;

public class ActionAI {
  public double score;
  public Action action;
  public Pos target[] = new Pos[2];

  public void reset(Pos initialPos) {
    action = Action.WAIT;
    score = 0;
    target[0] = initialPos;
    target[1] = initialPos;
  }

  public void toOrder(Order order) {
    if (action == Action.SPEED) {
      order.speed();
    } else if (action == Action.MOVE) {
      order.move(target[1]);
    } else if (action == Action.SWITCH_PAPER) {
      order.doSwitch(action);
    } else if (action == Action.SWITCH_ROCK) {
      order.doSwitch(action);
    } else if (action == Action.SWITCH_SCISSOR) {
      order.doSwitch(action);
    } else if (action == Action.WAIT) {
      order.move(target[1]);
    }
  }
  
  @Override
  public String toString() {
    if (action == Action.MOVE) {
      return ""+action+" "+target[0]+" -> "+target[1]+" score="+score;
    } else {
      return ""+action+" score="+score;
    }
  }
  
}
