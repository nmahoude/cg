package calm.actions;

import calm.state.Agent;
import calm.state.State;
import calmBronze.Item;
public class UseOven extends Action {

  public UseOven() {
    super(State.equipmentOven);
  }

  @Override
  public boolean prerequisites(State state, Agent agent) {
    if (state.board.ovenContents == Item.DOUGH) return false; // not bake yet
    if (state.board.ovenContents == Item.RAW_TART) return false; // not bake yet
    
    if (agent.items == Item.NOTHING && state.board.ovenContents == Item.CROISSANT) return true;
    if ((agent.items == Item.NOTHING) && state.board.ovenContents == Item.BLUEBERRIES_TART) return true;
    if (agent.items == Item.DOUGH && state.board.ovenContents == Item.NOTHING) return true;
    if (agent.items == Item.RAW_TART && state.board.ovenContents == Item.NOTHING) return true;
    
    return false;
  }

  @Override
  public void privateApplyEffect(State state, Agent agent) {
    if (state.board.ovenContents != Item.NOTHING) {
      agent.items = state.board.ovenContents;
      state.board.ovenTimer = 0;
      state.board.ovenContents = Item.NOTHING;
    } else if (agent.items != Item.NOTHING) {
      state.board.ovenContents = agent.items;
      state.board.ovenTimer = state.turn + 10;
      agent.items = Item.NOTHING;
    } else {
      // burned !
    }
  }

  @Override
  public String describe() {
    return "Use oven";
  }
}
