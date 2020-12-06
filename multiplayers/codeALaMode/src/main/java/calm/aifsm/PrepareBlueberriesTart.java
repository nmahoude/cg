package calm.aifsm;

import calm.Item;
import calm.ItemMask;
import calm.Player;
import calm.State;
import calm.ai.Order;

public class PrepareBlueberriesTart extends Executor {
  
  public PrepareBlueberriesTart(State state) {
    super(state);
  }

  @Override
  public int eta() {
    if (me.hands.hasBlueBerriesTart()) return 0;
    else return 1; // TODO calculate ETA !
  }
  
  @Override
  public Order execute() {
    if (me.hands.hasBlueBerriesTart()) {
      return null;
    }

    System.err.println("Prepare blueberries tart ...");
    if (!me.hands.isEmpty() && !me.hands.hasDough() && !me.hands.hasChoppedDough() && !me.hands.hasRawTart()) {
      return me.getRidOff();
    }
    
    if (state.ovenContents.hasRawTart() || state.ovenContents.hasBlueBerriesTart()) {
      return Order.use(Player.map.ovenAsEquipment);
    }
    if (!me.hands.isEmpty()) {
      if (me.hands.hasDough()) {
        return Order.use(Player.map.chopper);
      } else if (me.hands.hasChoppedDough()) {
        return Order.use(Player.map.blueberries);
      } else if (me.hands.hasRawTart()) {
        return Order.use(Player.map.ovenAsEquipment);
      } else if (state.ovenIsMine && state.ovenContents.hasRawTart()) {
        return Order.WAIT;
      } else if (state.ovenContents.hasBlueBerriesTart()) {
        return Order.use(Player.map.ovenAsEquipment);
      }  else {
        return me.getRidOff();
      }
    } else {
      Item item;
      item = state.getAll(ItemMask.RAW_TART).stream().sorted(this::closer).findFirst().orElse(null);
      if (item != null) {
        return Order.use(item);
      } else {
        System.err.println(" no raw tart found");
      }
      
      item = state.getAll(ItemMask.CHOPPED_DOUGH).stream().sorted(this::closer).findFirst().orElse(null);
      if (item != null) {
        return Order.use(item);
      } else {
        System.err.println("no chopped dough");
      }
      
      item = state.getAll(ItemMask.DOUGH).stream().sorted(this::closer).findFirst().orElse(null);
      return Order.use(item);
    }

  }
}
