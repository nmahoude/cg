package cotc.ai.ag;

import cotc.entities.Action;
import cotc.utils.Coord;

public class AGAction {
  public Action action;
  public Coord target; //only for FIRE
  
  public AGAction(Action action, Coord target) {
    this.action = action;
    this.target = target;
  }

  @Override
  public String toString() {
    if (action == Action.FIRE) {
      return "FIRE "+target.toString();
    } 
    return action.toString();
  }
}
