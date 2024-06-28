package calm.actions;
import calm.state.Agent;
import calm.state.State;
import calm.state.Table;
import calmBronze.Item;

public class UseItemOnTable extends Action {
  private int item;

  public UseItemOnTable(Table table, Integer item) {
    super(table);
    this.item = item;
  }

  @Override
  public boolean prerequisites(State state, Agent agent) {
    int myItem = agent.items;

    if ((myItem | Item.DISH) != 0) { // une assiette
      if (Integer.bitCount(myItem) == 5) 
        return false; // DISH + 4 ingredients, can't get more
      else if (!Item.canPutOnDish(item)) 
        return false; // can put smee ingredients in a dish
      else 
        return true;
    } else {
      // pas d'assiete
      if (myItem == Item.CHOPPED_DOUGH && item == Item.BLUEBERRIES) {
        return true;
      } else if (myItem != 0) 
        return false;
      else
        return true;
    }
  }

  @Override
  public void privateApplyEffect(State state, Agent agent) {
    state.board.tables.remove(table);
    if (agent.items == Item.CHOPPED_DOUGH && item == Item.BLUEBERRIES) {
      agent.items = Item.RAW_TART;
    } else {
      agent.items |=item;
    }
  }

  @Override
  public String describe() {
    return "Use table ["+table.pos +"-"+Item.toString(item)+"]";
  }

}
