package calm.actions;

import calm.state.Agent;
import calm.state.State;
import calm.state.Table;

public class DropItem extends Action {

  public DropItem() {
    super(null);
  }

  @Override
  public boolean prerequisites(State state, Agent agent) {
    if (agent.items == 0) return false; // can't drop if nothing in hands
    
    // TODO check empty table
    return true;
  }

  @Override
  protected void privateApplyEffect(State state, Agent agent) {
    // TODO OH MY GOD, but that may work
    state.board.tables.put(new Table(agent.pos.x, agent.pos.y), agent.items);
    agent.items = 0;
  }

  @Override
  public String describe() {
    return "Drop stuff";
  }

  @Override
  public void execute(State state, Agent agent) {
    Table best = null;
    int bestDist = Integer.MAX_VALUE;
    for (Table t : State.fixedTables) {
      
      if (state.board.tables.containsKey(t)) continue; // something on it
      
      int dist = t.pos.manhattanDistance(agent.pos);
      if (dist < bestDist) {
        bestDist = dist;
        best = t;
      }

      if (t.pos.neighborDistance(agent.pos) > 1) continue;

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
