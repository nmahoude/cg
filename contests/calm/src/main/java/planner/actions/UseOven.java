package planner.actions;

import planner.state.Item;
import planner.state.State;

public class UseOven extends Action {

  public UseOven() {
    super(State.equipmentOven);
  }

  @Override
  public boolean prerequisites(State state) {
    if (state.ovenContents == Item.DOUGH) return false; // not bake yet
    if (state.ovenContents == Item.RAW_TART) return false; // not bake yet
    
    if (state.agent1.items == Item.NOTHING && state.ovenContents == Item.CROISSANT) return true;
    if ((state.agent1.items == Item.NOTHING) && state.ovenContents == Item.BLUEBERRIES_TART) return true;
    if (state.agent1.items == Item.DOUGH && state.ovenContents == Item.NOTHING) return true;
    if (state.agent1.items == Item.RAW_TART && state.ovenContents == Item.NOTHING) return true;
    
    return false;
  }

  @Override
  public void privateApplyEffect(State state) {
    if (state.ovenContents != Item.NOTHING) {
      state.agent1.items = state.ovenContents;
      state.ovenTimer = 0;
      state.ovenContents = Item.NOTHING;
    } else if (state.agent1.items != Item.NOTHING) {
      state.ovenContents = state.agent1.items;
      state.ovenTimer = state.turn + 10;
      state.agent1.items = Item.NOTHING;
    } else {
      // burned !
    }
  }

  @Override
  public String describe() {
    return "Use oven";
  }
}
