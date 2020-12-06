package calm.aifsm;

import calm.Item;
import calm.ItemMask;
import calm.Player;
import calm.State;
import calm.ai.Order;

public class PrepareChoppedStrawberries extends Executor {
  
  public PrepareChoppedStrawberries(State state) {
    super(state);
  }

  public Order execute() {
    if (me.hands.hasChoppedStrawberries()) {
      return null;
    }
    
    System.err.println("Prepare chopped strawberries...");
    if (!me.hands.isEmpty() && !me.hands.hasStrawberries()) {
      return me.getRidOff();
    } else if (!me.hands.hasStrawberries()) {
      Item item = Player.state.getAll(ItemMask.STRAWBERRIES).stream().sorted(this::closer).findFirst().get();
      return Order.use(item);
    } else {
      return Order.use(Player.map.chopper);
    }
  }

  @Override
  public int eta() {
    return 0; // TODO estimate eta
  }
}
