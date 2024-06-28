package planner.actions;

import calmBronze.Table;
import planner.state.Item;
import planner.state.State;

public class UseItemOnTable extends Action {
  private int item;

  public UseItemOnTable(Table table, Integer item) {
    super(table);
    this.item = item;
  }

  @Override
  public boolean prerequisites(State state) {
    int myItem = state.agent1.items;

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
  public void privateApplyEffect(State state) {
    state.tables.remove(table);
    if (state.agent1.items == Item.CHOPPED_DOUGH && item == Item.BLUEBERRIES) {
      state.agent1.items = Item.RAW_TART;
    } else {
      state.agent1.items |=item;
    }
  }

  @Override
  public String describe() {
    return "Use table ["+table.pos +"-"+Item.toString(item)+"]";
  }

}
