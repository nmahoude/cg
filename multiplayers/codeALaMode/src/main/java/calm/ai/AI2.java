package calm.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import calm.Desert;
import calm.Item;
import calm.ItemMask;
import calm.P;
import calm.Player;
import calm.State;

public class AI2 {

  private State state;

  public Order think(State state) {
    this.state = state;
    
    
    int myDistToOven = Player.map.distanceFromTo(state.me.pos, Player.map.ovenAsEquipment.pos, state.him.pos);
    int hisdistToOven = Player.map.distanceFromTo(state.him.pos, Player.map.ovenAsEquipment.pos, state.me.pos);
    System.err.println("my dist to oven ="+myDistToOven);
    System.err.println("his dist to oven ="+hisdistToOven);
    
    // update stats on desert ingredients
    int neededChoppedStrawberriesCount = 0;
    int neededCroissantCount = 0;
    int neededBlueberriesTartsCount = 0;
    // TODO get next desert too ?
    for (int i=0;i<3;i++) {
      Desert d = state.deserts[i];
      if (d.item.hasChoppedStrawberries()) neededChoppedStrawberriesCount++;
      if (d.item.hasBlueBerriesTart()) neededBlueberriesTartsCount++;
      if (d.item.hasCroissant()) neededCroissantCount++;
    }
    
    int choppedStrawberriesCount = 0;
    int croissantCount = 0;
    int blueberriesTartsCount = 0;
    for (int i=0;i<state.itemsFE;i++) {
      if (state.items[i].hasBlueBerriesTart() || state.items[i].hasRawTart() || state.items[i].hasChoppedDough()) blueberriesTartsCount++;
      if (state.items[i].hasCroissant() || state.items[i].hasDough()) croissantCount++;
      if (state.items[i].hasStrawberries()) choppedStrawberriesCount++;
    }
    if (state.me.hands.hasBlueBerriesTart() || state.me.hands.hasRawTart() || state.me.hands.hasChoppedDough()) blueberriesTartsCount++;
    if (state.me.hands.hasCroissant() || state.me.hands.hasDough()) croissantCount++;
    if (state.me.hands.hasStrawberries()) choppedStrawberriesCount++;
    if (state.him.hands.hasBlueBerriesTart() || state.him.hands.hasRawTart() || state.him.hands.hasChoppedDough()) blueberriesTartsCount++;
    if (state.him.hands.hasCroissant() || state.him.hands.hasDough()) croissantCount++;
    if (state.him.hands.hasStrawberries()) choppedStrawberriesCount++;
    
    if (state.ovenContents.hasDough() || state.ovenContents.hasCroissant()) croissantCount++;
    if (state.ovenContents.hasRawTart() || state.ovenContents.hasBlueBerriesTart()) blueberriesTartsCount++;
    
    System.err.println("Statistics (needed / current) :");
    System.err.println("Chopped st: "+neededChoppedStrawberriesCount+" / "  +choppedStrawberriesCount);
    System.err.println("Croissant : "+neededCroissantCount+" / "+croissantCount);
    System.err.println("BlueTart  : "+neededBlueberriesTartsCount+" / "+blueberriesTartsCount);
    
    
    List<Desert> mayBeDoing = desertHeMayBeDoing(state);
    Desert desert = chooseDesert(state, mayBeDoing);
    if (desert == null) {
      desert = chooseDesert(state, Collections.emptyList());
    }
    System.err.println("*** preparing "+desert);
    System.err.println("*** my hands "+state.me.hands);
    Order order = null;
    
    if (desert == null) {
      System.err.println("Can't prepare anything, dropping");
      return state.me.getRidOff();
    }

    // get in oven !
    if (state.ovenContents.hasBlueBerriesTart() || state.ovenContents.hasCroissant()) {
      System.err.println("Oven is waiting ...");
      boolean shouldIGo = false;
      if (state.me.hands.isEmpty() && state.him.hands.isEmpty()) {
        shouldIGo = myDistToOven <= hisdistToOven;
      } else if (state.him.hands.isEmpty()) {
        shouldIGo = false;
      } else {
        shouldIGo = myDistToOven <= hisdistToOven;
      }

      if (shouldIGo) {
        System.err.println("Save the oven !");
        if (state.me.hands.isEmpty()) {
          System.err.println("my hands are empty, going to oven");
          order = state.me.getOven();
        } else {
          System.err.println("need to get rid off what i'm holding");
          order = state.me.getRidOff();
        }
      }
    }
    
//    if (!state.me.hands.isEmpty()) {
//      
//      // check if there is a better dish than the one I transport to fill th desert
//      int bestValue = state.me.hands.getValue();
//      boolean shouldDrop = false;
//      Item best = null;
//      for (int i=0;i<state.itemsFE;i++) {
//        Item item = state.items[i];
//        if (item.isCompatibleWith(desert.item)) {
////          if (item.mask != 0 && (item.mask & state.me.hands.mask) == 0 && ((item.mask | state.me.hands.mask) & ~desert.item.mask) == 0 && (item.hasPlate() || state.me.hands.hasPlate())) {
////            // ok i transport something useful for the plate
////            System.err.println("I'm holding an interstring ingredient for desert, gettin the rest");
////            return Order.use(item);
////          }
//          
//          
//          if (item.getValue() > bestValue) {
//            shouldDrop = true;
//            bestValue = item.getValue();
//            best = item;
//          }
//        } 
//      }
//      if (shouldDrop) {
//        System.err.println("*** I'm holding "+state.me.hands);
//        System.err.println("*** But there is a better item "+best+" on table ... so get rid off");
//        return state.me.getRidOff();
//      }
//    }
    
    // build tier 2
    if (order == null) {
      order = getBlueberriesTart(desert);
    }
    if (order == null) {
      order = getCroissant(desert);
    }
    if (order == null) {
      order = getChoppedStrawberries(desert);
    }
    
    // get ingredients for desert
    if (order == null) {// || (state.me.hands.isEmpty() && order.tag == OrderTag.WAIT)) {
      order = state.me.grab(desert);
    }
    
    if (order == null) {
      order = Order.use(Player.map.bell);
    }
    
    if (order == null) {
      order = Order.WAIT;
    }
    return order;
  }

  private Order getBlueberriesTart(Desert desert) {
    System.err.println("getTart?");
    if (!desert.item.hasBlueBerriesTart()) {
      System.err.println("  no need of tart");
      return null;
    }
    if (state.me.hands.hasBlueBerriesTart()) {
      System.err.println("  already got one");
      return null;
    }
    
    if (state.me.hands.hasRawTart() || state.me.hands.hasChoppedDough()) {
      return state.me.buildBlueberryTart();
    }
    
    if (state.ovenContents.hasRawTart()) {
      System.err.println("Oven is cooking one (for us ?)");
      return null;
    }
    if (state.ovenContents.hasBlueBerriesTart()) {
      System.err.print("oven has cooked BPie ..");
      if (state.me.hands.isEmpty()) {
        System.err.println(" get it (is it for us ?)");
        return Order.use(Player.map.ovenAsEquipment);
      } else {
        System.err.println(" get ridoff to get it after");
        return state.me.getRidOff();
      }
    }
    
    List<Item> items = state.getItemsCompatibleWith(desert);
    for (Item item : items) {
      if (item.hasBlueBerriesTart() && (item.mask & state.me.hands.mask) == 0) {
        System.err.println("   we will get them on the tables");
        return null;
      }
    }
    
    return state.me.buildBlueberryTart();
  }

  private Order getCroissant(Desert desert) {
    System.err.println("getCroissant?");
    if (!desert.item.hasCroissant()) {
      System.err.println("  no need of croissant");
      return null;
    }
    if (state.me.hands.hasCroissant()) {
      System.err.println("  already got one");
      return null;
    }

    if (state.me.hands.hasDough()) {
      return state.me.buildCroissant();
    }

    if (state.ovenContents.hasDough()) {
      System.err.println("Oven is cooking one (for us ?)");
      return null;
    }
    
    if (state.ovenContents.hasCroissant()) {
      System.err.print("Oven has cooked a croissant, ");
      if (state.me.hands.isEmpty()) {
        System.err.println("get it (is it for us ?)");
        return Order.use(Player.map.ovenAsEquipment);
      } else {
        System.err.println("get rif off before getting it");
        return state.me.getRidOff();
      }
    }

    List<Item> items = state.getItemsCompatibleWith(desert);
    for (Item item : items) {
      if (item.hasCroissant() && (item.mask & state.me.hands.mask) == 0) {
        System.err.println("   we will get them on the tables");
        return null;
      }
    }

    return state.me.buildCroissant();
  }

  private Order getChoppedStrawberries(Desert desert) {
    System.err.println("getChoppedStrawberries?");
    if (!desert.item.hasChoppedStrawberries()) {
      System.err.println("   no need of strawberries");
      return null;
    }

    if (state.me.hands.hasChoppedStrawberries()) {
      System.err.println("   already got them :)");
      return null; 
    }
    
    if (state.me.hands.hasStrawberries()) {
      System.err.println("   building them");
      return state.me.buildChoppedStrawberries();
    }
    
    List<Item> items = state.getItemsCompatibleWith(desert);
    for (Item item : items) {
      if (item.hasChoppedStrawberries()  && (item.mask & state.me.hands.mask) == 0) {
        System.err.println("   we will get them on the tables");
        return null;
      }
    }
    
    return state.me.buildChoppedStrawberries();
  }

  List<Desert> desertHeMayBeDoing(State state) {
    Item clean = new Item(P.INVALID);
    List<Desert> deserts = new ArrayList<>();
    if (!state.him.hands.isEmpty()) {
      int mayBeDoing = 0;
      for (int i=0;i<3;i++) {
        Desert desert = state.deserts[i];
        
        // TODO do better to know what we are preparing
        clean.reset(state.him.hands.mask);
        if (clean.hasDough()) {
          clean.mask =ItemMask.CROISSANT;
        }
        if (clean.hasChoppedDough() || clean.hasRawTart()) {
          clean.mask =ItemMask.BLUEBERRIES_TART;
        }
        if (clean.hasStrawberries()) {
          clean.mask =ItemMask.CHOPPED_STRAWBERRIES;
        }
      
        
        if (clean.mask != 0 && (clean.mask & ~desert.item.mask) == 0 ) {
          System.err.println("he may be doing "+desert);
          deserts.add(desert);
        }
      }
    }
    return deserts;
  }
  
  private Desert chooseDesert(State state, List<Desert> oppDoing) {
    // TODO check if other is doing a desert
    Desert best = null;
    Item clean = new Item(P.INVALID);
    
    for (int i=0;i<3;i++) {
      Desert desert = state.deserts[i];
      if (oppDoing.contains(desert)) continue; // do not try to do the same desert
      
      // TODO do better to know what we are preparing
      clean.reset(state.me.hands.mask);
      clean.mask = (clean.mask & ~ItemMask.DOUGH);
      clean.mask = (clean.mask & ~ItemMask.CHOPPED_DOUGH);
      clean.mask = (clean.mask & ~ItemMask.RAW_TART);
      clean.mask = (clean.mask & ~ItemMask.STRAWBERRIES);
    
      if ((clean.mask & ~desert.item.mask) == 0 ) {
        if (Player.turnsRemaining > 0) {
          if (best == null || desert.award > best.award) {
            best = desert;
          }
        } else {
          if (best == null || desert.award < best.award) {
            best = desert;
          }
        }
      }
    }
    
    return best;
  }
}
