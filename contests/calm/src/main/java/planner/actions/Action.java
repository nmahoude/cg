package planner.actions;

import calmBronze.Table;
import planner.state.State;

public abstract class Action {
  protected final Table table;
  
  public Action(Table table) {
    this.table = table;
  }
  public abstract boolean prerequisites(State state);
  protected abstract void privateApplyEffect(State state);
  
  protected int evaluateTurns(State state) {
    if (this instanceof Wait) {
      return ((Wait)this).time;
    } else if (this instanceof DropItem) {
      return 1; // TODO assuming we will always drop ASAP
    } else {
      if (table.pos.neighborDistance(state.agent1.pos) <= 1) {
        return 1;//cost 1 for the use
      } else {
        int dist = table.pos.manhattanDistance(state.agent1.pos);
        return Math.max(1, dist / 4) + 1; // deplacement + use
      }
    }
  }
  
  public void applyEffect(State state) {
    state.addTurns(evaluateTurns(state));
    if (! (this instanceof Wait) && !(this instanceof DropItem)) {
      state.agent1.pos = table.pos;
    }
    privateApplyEffect(state);
  }

  public void execute(State state) {
    int neighborDistance = table.pos.neighborDistance(state.agent1.pos);
    if (neighborDistance <= 1) {
      System.out.println("USE " + table.pos.x + " " + table.pos.y);
    } else {
      System.out.println("MOVE " + table.pos.x + " " + table.pos.y);
    }
  }
  
  public abstract String describe();
  @Override
  public String toString() {
    return "Action: "+describe();
  }
}
