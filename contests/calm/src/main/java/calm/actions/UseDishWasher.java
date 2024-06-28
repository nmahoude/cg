package calm.actions;

import calm.state.Agent;
import calm.state.State;
import calmBronze.Item;
public class UseDishWasher extends Action {

  public UseDishWasher() {
    super(State.equipmentDishWasher);
  }

  @Override
  public boolean prerequisites(State state, Agent agent) {
    if (agent.items == Item.DISH
        || !Item.canPutOnDish(agent.items)) {
      return false;
    } else {
      return true;
    }
  }

  @Override
  public void privateApplyEffect(State currentState, Agent agent) {
    // TODO check if there is already 3 dish in circulation !
    if ((agent.items & Item.DISH) != 0) {
      agent.items = Item.DISH;
    } else {
      agent.items |= Item.DISH;
    }
  }

  @Override
  public String describe() {
    return "Use the dishWasher";
  }

}
