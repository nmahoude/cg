package calm;

import java.util.List;
import java.util.Scanner;

import calm.ai.Order;

public class Agent {
  public State state;
  public P pos;
  public Item hands = new Item(P.INVALID);
  public int desert;
  
  public Agent(State state) {
    this.state = state;
  }
  
  public void read(Scanner in) {
    pos = P.get(in.nextInt(),in.nextInt());
    hands.reset(ItemMask.fromString(in.next()));
  }

  public Order buildChoppedStrawberries() {
    System.err.println("build Chopped strawberries");

    if (!hands.isEmpty() && !hands.hasStrawberries()) {
      return getRidOff();
    } else if (!hands.hasStrawberries()){
      return Order.use(Player.map.strawberries);
    } else {
      return Order.use(Player.map.chopper);
    }
  }

  public Order buildCroissant() {
    System.err.println("build croissant ...");

    if (!hands.isEmpty() && !hands.hasCroissant() && !hands.hasDough()) {
      return getRidOff();
    } 
    
    

    
    if (hands.hasDough()) {
      return Order.use(Player.map.ovenAsEquipment);
    } else if (state.ovenIsMine && state.ovenContents.hasDough()) {
      return Order.WAIT;
    } else if (state.ovenContents.hasCroissant()) {
      return Order.use(Player.map.ovenAsEquipment);
    } else {
      Item item;
      // TODO best location ?
      if ((item = state.get(ItemMask.DOUGH)) != null) {
        return Order.use(item);
      } else {
        return Order.use(Player.map.dough);
      }
    }
  }

  public Order buildBlueberryTart() {
    System.err.println("build tart ...");
    System.err.println("oven timer : "+state.ovenTimer);
    
    if (state.ovenTimer > 0 && state.me.isCloseTo(Player.map.ovenAsEquipment) && hands.hasRawTart()) {
      System.err.println("don't wait near oven ...");
      return getRidOff();
    }

    
    if (!hands.isEmpty() && !hands.hasBlueBerries() && !hands.hasDough() && !hands.hasChoppedDough() && !hands.hasRawTart()) {
      return getRidOff();
    }
    
    if (hands.isEmpty()) {
      Item item;
      if ((item = state.get(ItemMask.RAW_TART)) != null) {
        return Order.use(item);
      } else {
        System.err.println(" no raw tart found");
      }
      
      if ((item = state.get(ItemMask.CHOPPED_DOUGH)) != null) {
        return Order.use(item);
      }
    }
    
    if (hands.hasDough()) {
      return Order.use(Player.map.chopper);
    } else if (hands.hasChoppedDough()) {
      return Order.use(Player.map.blueberries);
    } else if (hands.hasRawTart()) {
        return Order.use(Player.map.ovenAsEquipment);
    } else if (state.ovenIsMine && state.ovenContents.hasRawTart()) {
      return Order.WAIT;
    } else if (state.ovenContents.hasBlueBerriesTart()) {
      return Order.use(Player.map.ovenAsEquipment);
    } else {
      return Order.use(Player.map.dough);
    }
  }

  public Order getOven() {
    System.err.println("Get in oven");
    // TODO check I don't have 4 ingredients already
    if (hands.isEmpty() 
        || (hands.hasPlate() && (hands.mask & Player.map.ovenAsEquipment.mask) == 0)) {
      return Order.use(Player.map.ovenAsEquipment);
    } else {
      return getRidOff();
    }
  }

  
  
  public Order getRidOff() {
    // TODO find best place to get rid off ...
    System.err.println("Get rid off");
    return Order.use(state.findClosestEmptyTable(state));
  }

  public Order grab(Desert desert) {
    System.err.println("grab desert");

    boolean needPlate = false;
    
    
    
    List<Item> items = state.getDishCompatibleWithDesertWhenHolding(desert, hands);
    Item best = null;
    double bestV = Double.NEGATIVE_INFINITY;
    for (Item item : items) {
      if (state.me.hands.mask != 0 && !state.me.hands.hasPlate() && !item.hasPlate()) continue; // can't grab 2 ingredient without a plate
      System.err.println("testing "+item+ " @"+item.pos+" with dist "+Player.map.distanceFromTo(state.me.pos, item.pos, state.him.pos));
      double value = item.getValue() - 0.1 * Player.map.distanceFromTo(state.me.pos, item.pos, state.him.pos);
      if (best == null || value > bestV) {
        bestV = value;
        best = item;
      }
    }
    if (best != null) {
      System.err.println("Get ingredients on table @"+best.pos);
      return Order.use(best);
    }
    
    if ((hands.mask & ~desert.item.mask) != 0) {
      return getRidOff();
    }
    if (!hands.isEmpty() && !hands.hasPlate()) {
      return Order.use(Player.map.dishwasher);
    } else if (!hands.hasCroissant() && desert.item.hasCroissant()) {
      return getCroissant();
    } else if (!hands.hasChoppedStrawberries() && desert.item.hasChoppedStrawberries()) {
      return getChoppedStrawberries();
    } else if (!hands.hasIceCream() && desert.item.hasIceCream()) {
      return getIceCream();
    } else if (!hands.hasBlueBerries() && desert.item.hasBlueBerries()) {
      return getBlueBerries();
    } else {
      System.err.println("  nothing left");
      return null;
    }
  }

  private Order getBlueBerries() {
    return Order.use(Player.map.blueberries);
  }

  private Order getChoppedStrawberries() {
    if (state.hasChoppedStrawberries() && !hands.hasStrawberries()) {
      System.err.println("grab chopped strawberries");
      Item item = state.getChoppedStrawberries(this);
      return Order.use(item);
    }
    return buildChoppedStrawberries();
  }

  private Order getCroissant() {
    if (state.hasCroissant() && !hands.hasDough()) {
      System.err.println("get croissant ...");
      Item croissant = state.getCroissant(this);
      return Order.use(croissant);
    }
    return buildCroissant();
  }

  private Order getIceCream() {
    return Order.use(Player.map.icecream);
  }

  public boolean isCloseTo(Item item) {
    return pos.neighbor(item.pos) <= 1;
  }

}
