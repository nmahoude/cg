package calm.aifsm;

import calm.Item;
import calm.ItemMask;
import calm.Player;
import calm.State;
import calm.ai.Order;

public class PrepareCroissant extends Executor {
  
  public PrepareCroissant(State state) {
    super(state);
  }

  @Override
  public int eta() {
    if (me.hands.hasCroissant()) return 0;
    else return 1; // TODO calculate ETA !
  }
  
  @Override
  public Order execute() {
    if (me.hands.hasCroissant()) {
      return null;
    }

    System.err.println("Prepare croissant ...");
    if (!me.hands.isEmpty() && !me.hands.hasCroissant() && !me.hands.hasDough()) {
      return me.getRidOff();
    }
    if (me.hands.hasDough()) {
      return Order.use(Player.map.ovenAsEquipment);
    } else if (state.ovenIsMine && state.ovenContents.hasDough()) {
      return Order.WAIT;
    } else if (state.ovenContents.hasCroissant()) {
      return Order.use(Player.map.ovenAsEquipment);
    } else {
      Item item = state.getAll(ItemMask.DOUGH).stream().sorted(this::closer).findFirst().get();
      return Order.use(item);
    }
  }
}
