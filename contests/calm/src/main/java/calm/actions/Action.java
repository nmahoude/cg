package calm.actions;

import calm.state.Agent;
import calm.state.State;
import calm.state.Table;

public abstract class Action {
  protected final Table table;
  
  public Action(Table table) {
    this.table = table;
  }
  public abstract boolean prerequisites(State state, Agent agent);
  protected abstract void privateApplyEffect(State state, Agent agent);
  
  public void applyEffect(State state, Agent agent) {
    state.addTurns(1);
    privateApplyEffect(state,agent);
  }

  public void execute(State state, Agent agent) {
    System.out.println("USE " + table.pos.x + " " + table.pos.y);
  }
  
  public abstract String describe();
  @Override
  public String toString() {
    return "Action: "+describe();
  }
}
