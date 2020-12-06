package calm.aifsm;

import calm.Agent;
import calm.Item;
import calm.Player;
import calm.State;
import calm.ai.Order;

public abstract class Executor {
  final State state;
  final Agent me;
  
  public Executor(State state) {
    this.state = state;
    this.me = state.me;
  }
  
  public int closer(Item item1, Item item2) {
    return Integer.compare(Player.map.distanceFromTo(me.pos, item1.pos, Player.state.him.pos), Player.map.distanceFromTo(me.pos, item2.pos, Player.state.him.pos));
  }

  public abstract Order execute();

  public abstract int eta();
}
