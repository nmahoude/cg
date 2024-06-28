package calm.actions;

import calm.state.Agent;
import calm.state.State;

public class Wait extends Action {

  int time;

  public Wait(int time) {
    super(null);
    this.time = time;
  }

  @Override
  public boolean prerequisites(State state, Agent agent) {
    return true;
  }

  @Override
  public void privateApplyEffect(State state, Agent agent) {
    // do nothing
  }

  @Override
  public String describe() {
    return "Wait for "+time+" turns";
  }

  @Override
  public void execute(State state, Agent agent) {
    System.out.println("WAIT");
  }
}
