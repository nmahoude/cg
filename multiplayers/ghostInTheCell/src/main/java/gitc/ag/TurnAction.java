package gitc.ag;

import java.util.ArrayList;
import java.util.List;

import gitc.simulation.actions.Action;

public class TurnAction {
  public List<Action> actions = new ArrayList<>();
  
  public void clear() {
    actions.clear();
  }
}
