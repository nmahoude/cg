package planner.actions;

import calmBronze.Table;
import planner.state.Item;
import planner.state.State;

public class UseEquipmentIngredient extends Action  {
  private int typeIngredient;

  public UseEquipmentIngredient(Table table, int type) {
    super(table);
    this.typeIngredient = type;
  }

  @Override
  public boolean prerequisites(State currentState) {
    if ((currentState.agent1.items & typeIngredient) != 0) return false; // pas de doublon
    if ((currentState.agent1.items & Item.DISH) != 0) {
      if (typeIngredient == Item.EQUIPMENT_DOUGH || typeIngredient == Item.EQUIPMENT_STRAWBERRY) {
        return false;
      }
      return Integer.bitCount(currentState.agent1.items) < 5; // 1 for dish & max 4 for the ingredient
    } else {
      // si on a pas d'assiette
      if (currentState.agent1.items == Item.CHOPPED_DOUGH && typeIngredient == Item.BLUEBERRIES) {
        return true; // cas special
      } else {
        return currentState.agent1.items == 0; // on ne peut porter qu'un ingredient
      }
    }
  }


  @Override
  public void privateApplyEffect(State state) {
    if (state.agent1.items == Item.CHOPPED_DOUGH && typeIngredient == Item.BLUEBERRIES) {
      state.agent1.items = Item.RAW_TART;
    } else {
      state.agent1.items |=typeIngredient;
    }
  }
  
  @Override
  public String describe() {
    return "get static ingredient "+Item.toString(typeIngredient);
  }
}
