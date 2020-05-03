package calm.ai;

import calm.Desert;
import calm.Item;
import calm.ItemMask;
import calm.P;
import calm.Player;
import calm.State;

public class AI {

  private State state;

  public Order think(State state) {
    this.state = state;
    
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
    for (int i=0;i<77;i++) {
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
    
    Desert desert = chooseDesert(state);
    System.err.println("*** preparing "+desert);
    System.err.println("*** my hands "+state.me.hands);
    Order order = null;

    if (desert == null) {
      if (state.me.hands.hasPlate()) {
        order = state.me.getRidOff();
      } else {
        desert = state.deserts[0];
      }
    } else {
      
    }
    
    
    // get in oven !
    if (state.ovenContents.hasBlueBerriesTart() || state.ovenContents.hasCroissant()) {
      System.err.println("Oven is waiting ...");
      // TODO is he closer ? 
      // TODO is it his desert ?
      if (state.me.hands.isEmpty()) {
        System.err.println("my hands are empty, going to oven");
        order = state.me.getOven();
      } else {
        System.err.println("need to get rid off what i'm holding");
        order = state.me.getRidOff();
      }
    }
    
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
    if (order == null ) {
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
    System.err.println("getTart");
    if (!desert.item.hasBlueBerriesTart()) {
      System.err.println("  no need of tart");
      return null;
    }
    if (state.me.hands.hasBlueBerriesTart()) {
      System.err.println("  already got one");
      return null;
    }
    if (state.getItem(ItemMask.BLUEBERRIES_TART) != null) {
      System.err.println("   we will get them on the tables");
      return null;
    }
    return state.me.buildCroissant();
  }

  private Order getCroissant(Desert desert) {
    System.err.println("getCroissant");
    if (!desert.item.hasCroissant()) {
      System.err.println("  no need of croissant");
      return null;
    }
    if (state.me.hands.hasCroissant()) {
      System.err.println("  already got one");
      return null;
    }
    if (state.getCroissant(null) != null) {
      System.err.println("   we will get them on the tables");
      return null;
    }
    return state.me.buildCroissant();
  }

  private Order getChoppedStrawberries(Desert desert) {
    System.err.println("getChoppedStrawberries");
    if (!desert.item.hasChoppedStrawberries()) {
      System.err.println("   no need of strawberries");
      return null;
    }

    if (state.me.hands.hasChoppedStrawberries()) {
      System.err.println("   already got them :)");
      return null; 
    }
    if (state.getChoppedStrawberries(state.me) != null) {
      System.err.println("   we will get them on the tables");
      return null; 
    }
    
    return state.me.buildChoppedStrawberries();
  }

  private Desert chooseDesert(State state) {
    // TODO check if other is doing a desert
    Desert best = null;
    Item clean = new Item(P.INVALID);
    
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
          state.him.desert = desert.item.mask;
          mayBeDoing++;
        }
      }
      if (mayBeDoing > 1) {
        System.err.println("Too much choices, reseting");
        state.him.desert = 0;
      }
    }
    
    // check if other old desert it still here
    if (state.him.desert != 0) {
      int old = state.him.desert;
      state.him.desert = 0;
      for (int i=0;i<3;i++) {
        Desert desert = state.deserts[i];
        if (old == desert.item.mask) {
          state.him.desert = old;
          break;
        }
      }
    }
    
    for (int i=0;i<3;i++) {
      Desert desert = state.deserts[i];
      if (desert.item.mask == state.him.desert) continue; // do not try to do the same desert
      
      // TODO do better to know what we are preparing
      clean.reset(state.me.hands.mask);
      clean.mask = (clean.mask & ~ItemMask.DOUGH);
      clean.mask = (clean.mask & ~ItemMask.CHOPPED_DOUGH);
      clean.mask = (clean.mask & ~ItemMask.RAW_TART);
      clean.mask = (clean.mask & ~ItemMask.STRAWBERRIES);
    
      if ((clean.mask & ~desert.item.mask) == 0 ) {
        if (best == null || desert.award < best.award) {
          best = desert;
        }
      }
    }
    
    return best;
  }
}
