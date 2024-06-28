package planner.actionplanner;

import planner.actions.Action;
import planner.state.State;

public class Node {
  Node parent = null;
  State state = new State();
  int totalCost = 0;
  int turn;
  public Action action;
  
  @Override
  public String toString() {
    Node c = this;
    String actions = "";
    while (c.action != null) {
      actions = c.action.toString()+"("+c.turn+", "+c.totalCost+") => "+actions;
      c = c.parent;
    }
    return "(t="+turn+", tc="+totalCost+")"+actions;
  }
}
