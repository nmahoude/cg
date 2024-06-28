package calm.actions;

import calm.state.Agent;
import calm.state.State;
import calm.state.Table;
import calmBronze.Item;

public class UseEquipmentIngredient extends Action  {
  private int typeIngredient;

  public UseEquipmentIngredient(Table table, int type) {
    super(table);
    this.typeIngredient = type;
  }

  @Override
  public boolean prerequisites(State currentState, Agent agent) {
    if ((agent.items & typeIngredient) != 0) return false; // pas de doublon
    if ((agent.items & Item.DISH) != 0) {
      if (typeIngredient == Item.EQUIPMENT_DOUGH || typeIngredient == Item.EQUIPMENT_STRAWBERRY) {
        return false;
      }
      return Integer.bitCount(agent.items) < 5; // 1 for dish & max 4 for the ingredient
    } else {
      // si on a pas d'assiette
      if (agent.items == Item.CHOPPED_DOUGH && typeIngredient == Item.BLUEBERRIES) {
        return true; // cas special
      } else {
        return agent.items == 0; // on ne peut porter qu'un ingredient
      }
    }
  }


  @Override
  public void privateApplyEffect(State state, Agent agent) {
    if (agent.items == Item.CHOPPED_DOUGH && typeIngredient == Item.BLUEBERRIES) {
      agent.items = Item.RAW_TART;
    } else {
      agent.items |=typeIngredient;
    }
  }
  
  @Override
  public String describe() {
    return "get static ingredient "+Item.toString(typeIngredient);
  }
}
