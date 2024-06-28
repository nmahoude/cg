package planner.actions;

import planner.state.State;

public class Wait extends Action {

  int time;

  public Wait(int time) {
    super(null);
    this.time = time;
  }

  @Override
  public boolean prerequisites(State state) {
    return true;
  }

  @Override
  public void privateApplyEffect(State state) {
    // do nothing
  }

  @Override
  public String describe() {
    return "Wait for "+time+" turns";
  }

  @Override
  public void execute(State state) {
    System.out.println("WAIT");
  }
}
