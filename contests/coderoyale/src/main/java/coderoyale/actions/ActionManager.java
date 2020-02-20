package coderoyale.actions;

import coderoyale.units.Queen;

public class ActionManager {
  ActionGroup main = new ActionGroup();
  
  public void doAction(Queen me) {
    main.doAction(me);
  }
  
  public void addAction(Action action) {
    main.addAction(action);
  }
  public void resetActions() {
    main.reset();
  }
}
