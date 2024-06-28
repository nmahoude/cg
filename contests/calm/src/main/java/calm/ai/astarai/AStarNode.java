package calm.ai.astarai;

import calm.actions.Action;
import calm.state.State;

public class AStarNode {
  public AStarNode parent;
  public Action action; // action from parent
  public State state = new State();
  public boolean player1ToPlay = true;
  
  public int provenCost;
  public int totalCost;
 
  @Override
  public String toString() {
    AStarNode c = this;
    String actions = "";
    while (c.action != null) {
      actions = c.action.toString()+"("+c.provenCost+", "+c.totalCost+") => "+actions;
      c = c.parent;
    }
    return "(t="+provenCost+", tc="+totalCost+")"+actions;
  }
}
