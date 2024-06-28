package planner.actions;

import calmBronze.Table;
import planner.state.State;

public class DropItem extends Action {

  public DropItem() {
    super(null);
  }

  @Override
  public boolean prerequisites(State state) {
    if (state.agent1.items == 0) return false; // can't drop if nothing in hands
    
    // TODO check empty table
    return true;
  }

  @Override
  protected void privateApplyEffect(State state) {
    // TODO OH MY GOD, but that may work
    state.tables.put(new Table(state.agent1.pos.x, state.agent1.pos.y), state.agent1.items);
    state.agent1.items = 0;
  }

  @Override
  public String describe() {
    return "Drop stuff";
  }

  @Override
  public void execute(State state) {
    Table best = null;
    int bestDist = Integer.MAX_VALUE;
    for (Table t : State.fixedTables) {
      
      if (state.tables.containsKey(t)) continue; // something on it
      
      int dist = t.pos.manhattanDistance(state.agent1.pos);
      if (dist < bestDist) {
        bestDist = dist;
        best = t;
      }

      if (t.pos.neighborDistance(state.agent1.pos) > 1) continue;

      {
        System.out.println("USE "+t.pos.x+" "+t.pos.y);
        return;
      }
    }
    // oups no empty table nearby, go get somewhere else
    if (best == null) {
      System.out.println("WAIT no empty tables");
    } else {
      System.out.println("MOVE "+best.pos.x+" "+best.pos.y);
    }
  }
}
