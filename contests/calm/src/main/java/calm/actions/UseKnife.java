package calm.actions;

import calm.state.Agent;
import calm.state.State;
import calmBronze.Item;

public class UseKnife extends Action {

  public UseKnife() {
    super(State.equipmentChoppingBoard);
  }

  @Override
  public boolean prerequisites(State state, Agent agent) {
    return agent.items == Item.DOUGH || agent.items == Item.STRAWBERRIES;
  }

  @Override
  public void privateApplyEffect(State state, Agent agent) {
    if (agent.items == Item.DOUGH) {
      agent.items = Item.CHOPPED_DOUGH;
    } else if (agent.items == Item.STRAWBERRIES) {
      agent.items = Item.CHOPPED_STRAWBERRIES;
    } else {
      throw new RuntimeException("Chopp unknown item "+ Item.toString(agent.items));
    }
  }

  @Override
  public String describe() {
    return "Use knife ";
  }

}
