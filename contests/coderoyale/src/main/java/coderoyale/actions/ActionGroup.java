package coderoyale.actions;

import java.util.ArrayList;
import java.util.List;

import coderoyale.units.Queen;

public class ActionGroup extends Action {
  List<Action> actions = new ArrayList<>();

  @Override
  public void doAction(Queen me) {
    for (Action action : actions) {
      if (action.isFinished()) continue;
      action.doAction(me);
      if (!action.isFinished()) return;
    }
    isFinished = true;
  }

  public void addAction(Action action) {
    actions.add(action);
  }

  public void reset() {
    actions.clear();
  }
  
  public int actionsQueue() {
    return actions.size();
  }
  
  public ActionGroup then(Action action) {
    actions.add(action);
    return this;
  }
}
