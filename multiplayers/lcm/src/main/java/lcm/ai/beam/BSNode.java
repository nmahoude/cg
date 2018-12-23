package lcm.ai.beam;

import java.util.ArrayList;
import java.util.List;

import lcm.State;
import lcm.sim.Action;

public class BSNode {
  public State state = new State();
  public Action action;
  BSNode parent;
  double score;
  public boolean isTerminal;
  public BSNode minimaxParent;
  
  public static int comparator(BSNode t, BSNode o) {
    return Double.compare( o.score, t.score); // higher is better
  }

  public List<Action> getActions() {
    List<Action> actions = new ArrayList<>();
    BSNode best = this;
    while (best != null) {
      if (best.action != null && best.action != Action.pass()) {
        actions.add(0, best.action);
      }
      best = best.parent;
    }
    return actions;
  }
  
  @Override
  public String toString() {
    String output = "";
    for (Action action : getActions()) {
      output+=action.toString(state)+", ";
    }
    return ""+output+" => Score : "+score;
  }

  public void debugActions() {
    for (Action action : getActions()) {
      action.print(state, System.err);
      System.err.print("; ");
    }
  }
}
