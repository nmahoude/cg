package planner.actions;

import planner.state.Item;
import planner.state.State;

public class UseDishWasher extends Action {

  public UseDishWasher() {
    super(State.equipmentDishWasher);
  }

  @Override
  public boolean prerequisites(State state) {
    if (state.agent1.items == Item.DISH
        || !Item.canPutOnDish(state.agent1.items)) {
      return false;
    } else {
      return true;
    }
  }

  @Override
  public void privateApplyEffect(State currentState) {
    // TODO check if there is already 3 dish in circulation !
    if ((currentState.agent1.items & Item.DISH) != 0) {
      currentState.agent1.items = Item.DISH;
    } else {
      currentState.agent1.items |= Item.DISH;
    }
  }

  @Override
  public String describe() {
    return "Use the dishWasher";
  }

}
