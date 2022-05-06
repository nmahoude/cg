package spring2022;

import spring2022.Action;

public class TriAction {
  public Action[] actions = new Action[3];
  
  public TriAction() {
    actions[0] = new Action();
    actions[1] = new Action();
    actions[2] = new Action();
  }

  public void copyFrom(TriAction model) {
    actions[0].copyFrom(model.actions[0]);
    actions[1].copyFrom(model.actions[1]);
    actions[2].copyFrom(model.actions[2]);
  }
  
  
  public void output() {
    for (int i = 0; i < 3; i++) {
      System.out.println(actions[i] + " #"+i);
    }
  }

  public void reset() {
    for (int i = 0; i < 3; i++) {
      actions[i].type = Action.TYPE_WAIT;
    }
  }
  
}
