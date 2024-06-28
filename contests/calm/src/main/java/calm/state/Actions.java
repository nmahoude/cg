package calm.state;

import calm.actions.Action;

public class Actions {
  public int actionsFE = 0;
  public Action actions[] = new Action[8];
  
  public void addAction(Action action) {
    actions[actionsFE++] = action;
  }
}
