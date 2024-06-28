package planner.actions;

import planner.state.Item;
import planner.state.State;

public class UseKnife extends Action {

  public UseKnife() {
    super(State.equipmentChoppingBoard);
  }

  @Override
  public boolean prerequisites(State state) {
    return state.agent1.items == Item.DOUGH || state.agent1.items == Item.STRAWBERRIES;
  }

  @Override
  public void privateApplyEffect(State state) {
    if (state.agent1.items == Item.DOUGH) {
      state.agent1.items = Item.CHOPPED_DOUGH;
    } else if (state.agent1.items == Item.STRAWBERRIES) {
      state.agent1.items = Item.CHOPPED_STRAWBERRIES;
    } else {
      throw new RuntimeException("Chopp unknown item "+ Item.toString(state.agent1.items));
    }
  }

  @Override
  public String describe() {
    return "Use knife ";
  }

}
