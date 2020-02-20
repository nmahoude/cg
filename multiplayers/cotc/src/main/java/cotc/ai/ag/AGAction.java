package cotc.ai.ag;

import cotc.entities.Action;
import cotc.utils.Coord;

public class AGAction {
  public Action action;
  public Coord target; //only for FIRE

  public AGAction() {
  }
  
  public AGAction(Action action, Coord target) {
    this.action = action;
    this.target = target;
  }

  @Override
  public String toString() {
    if (action == Action.FIRE) {
      return "FIRE "+target.x+" "+target.y;
    }  else if (action == Action.MINE) {
      return "MINE";
    }
    return action.toString();
  }

  public void copyFrom(AGAction from) {
    this.action = from.action;
    this.target = from.target;
  }
}
