package calm.aifsm;

import calm.Desert;
import calm.ItemMask;
import calm.State;
import calm.ai.Order;

public class PrepareDesert extends Executor {
  Desert desert = new Desert();
  Executor currentExecutor = null;
  
  public PrepareDesert(State state,Desert desert) {
    super(state);
    this.desert.award = desert.award;
    this.desert.item.mask = desert.item.mask;
  }

  @Override
  public int eta() {
    return 1; // TODO calculate ETA !
  }
  
  @Override
  public Order execute() {
    // TODO interrupt ?
    
    if (currentExecutor != null) {
      Order order = currentExecutor.execute();
      if (order != null) {
        return order;
      } else {
        currentExecutor = null;
      }
    }
    
    currentExecutor = chooseNewIngredient();
    if (currentExecutor != null) {
      Order order = currentExecutor.execute();
      if (order != null) {
        return order;
      } else {
        currentExecutor = null;
      }
    }
    
    if (currentExecutor == null) {
      currentExecutor = new IngredientsGrabber(state, desert);
      Order order = currentExecutor.execute();
      if (order != null) {
        return order;
      } else {
        currentExecutor = null;
      }
    }

    return me.getRidOff();
  }

  private Executor chooseNewIngredient() {
    if (needBlueberriesTart()) {
      return new PrepareBlueberriesTart(state);
    } else if (needCroissant()) {
      return new PrepareCroissant(state);
    } else if (needChoppedStrawberries()) {
      return new PrepareChoppedStrawberries(state);
    } else {
      return null;
    }
  }

  private boolean needBlueberriesTart() {
    if (!desert.item.hasBlueBerriesTart()) return false;
    
    if (!state.getAll(ItemMask.BLUEBERRIES_TART).isEmpty() || me.hands.hasBlueBerriesTart()) return false;
    return true;
  }

  private boolean needCroissant() {
    if (!desert.item.hasCroissant()) return false;
    
    if (!state.getAll(ItemMask.CROISSANT).isEmpty() || me.hands.hasCroissant()) return false;
    return true;
  }

  private boolean needChoppedStrawberries() {
    if (!desert.item.hasChoppedStrawberries()) return false;
    
    if (!state.getAll(ItemMask.CHOPPED_STRAWBERRIES).isEmpty() || me.hands.hasChoppedStrawberries()) return false;
    return true;
  }
}
