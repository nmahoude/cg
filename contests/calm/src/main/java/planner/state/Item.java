package planner.state;

public class Item {
  public static final int EMPTY_TABLE = 0b0;
  public static final int NOTHING = 0b0;
  
  public static final int EQUIPMENT_DISH = 0b1;
  public static final int EQUIPMENT_OVEN = 0b10;
  public static final int EQUIPMENT_ICE_CREAM = 0b100;
  public static final int EQUIPMENT_STRAWBERRY = 0b1000;
  public static final int EQUIPMENT_BLUEBERRIES = 0b10000;
  public static final int EQUIPMENT_DOUGH = 0b100000;
  public static final int EQUIPMENT_BELL = 0b1000000;
  public static final int EQUIPMENT_CHOPPING_BOARD = 0b10000000;
  
  public static final int ICE_CREAM = 0b100000000;
  public static final int BLUEBERRIES = 0b1000000000;
  public static final int STRAWBERRIES = 0b10000000000;
  public static final int CHOPPED_STRAWBERRIES = 0b100000000000;
  public static final int CROISSANT = 0b1000000000000;
  public static final int DOUGH = 0b10000000000000;
  public static final int BLUEBERRIES_TART = 0b100000000000000;
  public static final int DISH = 0b1000000000000000;
  public static final int CHOPPED_DOUGH = 0b10000000000000000;
  public static final int RAW_TART = 0b100000000000000000;

  public static final int CLEAN_MASK = 0b11111111;

  private Item() {
  }
  
  public static int getFromString(String input) {
    int value = 0;
    // TODO perf ?
    String values[] = input.split("-");
    for (String str : values) {
      if (str.equals("DISH")) {
        value |= DISH;
      }
      if (str.equals("ICE_CREAM")) {
        value |= ICE_CREAM;
      }
      if (str.equals("BLUEBERRIES")) {
        value |= BLUEBERRIES;
      }
      if (str.equals("STRAWBERRIES")) {
        value |= STRAWBERRIES;
      }
      if (str.equals("CHOPPED_STRAWBERRIES")) {
        value |= CHOPPED_STRAWBERRIES;
      }
      if (str.equals("CROISSANT")) {
        value |= CROISSANT;
      }
      if (str.equals("DOUGH")) {
        value |= DOUGH;
      }
      if (str.equals("TART")) {
        value |= BLUEBERRIES_TART;
      }
      if (str.equals("CHOPPED_DOUGH")) {
        value |= CHOPPED_DOUGH;
      }
      if (str.equals("RAW_TART")) {
        value |= RAW_TART;
      }
    }
    return value;
  }

  public static boolean has(int item, int stuff) {
    return (item & stuff) != 0;
  }

  public static String toString(int preparing) {
    if (preparing == 0b100000000)
      return "ICE_CREAM";
    if (preparing == 0b1000000000)
      return "BLUEBERRIES";
    if (preparing == 0b10000000000)
      return "STRAWBERRY";
    if (preparing == 0b100000000000)
      return "CHOPPED_STRAWBERRIES";
    if (preparing == 0b1000000000000)
      return "CROISSANT";
    if (preparing == 0b10000000000000)
      return "DOUGH";
    if (preparing == 0b100000000000000)
      return "BLUEBERRIES_TART";
    if (preparing == 0b1000000000000000)
      return "DISH";
    if (preparing == 0b10000000000000000)
      return "CHOPPED_DOUGH";
    if (preparing == 0b100000000000000000)
      return "RAW_TART";

    if (preparing == 0) {
      return "Nothing";
    } 
    
    return "Unknown :"+Integer.toBinaryString(preparing);
  }

  public static boolean canPutOnDish(int item) {
    return item != DOUGH && item != STRAWBERRIES && item != CHOPPED_DOUGH && item != RAW_TART;
  }

  public static boolean isWarmable(int item) {
    return item == Item.DOUGH || item == Item.RAW_TART;
  }
  
  public static boolean canBeUseWithAgent(int item, State state, Agent agent) {
    if ((item & Item.DISH) != 0) {
      return agent.items == Item.NOTHING;
    } else {
      switch (item) {
      case Item.ICE_CREAM:
      case Item.BLUEBERRIES:
      case Item.CHOPPED_STRAWBERRIES:
      case Item.CROISSANT:
      case Item.BLUEBERRIES_TART:
      case Item.EQUIPMENT_ICE_CREAM:
      case Item.EQUIPMENT_BLUEBERRIES:
        return canAddTier2Ingredient(item, agent);
      case Item.EQUIPMENT_DOUGH:
      case Item.EQUIPMENT_STRAWBERRY:
      case Item.STRAWBERRIES:
      case Item.DOUGH:
      case Item.CHOPPED_DOUGH:
      case Item.RAW_TART:
        return canAddTier1Ingredient(agent);
      case Item.EQUIPMENT_DISH:
        return true; // TODO plus d'assiette ?
      case Item.DISH:
        return Item.canPutOnDish(agent.items);
      case Item.EQUIPMENT_OVEN:
        return canUseOven(state, agent);
      case Item.EQUIPMENT_BELL:
        return agent.items != 0;
      case Item.EQUIPMENT_CHOPPING_BOARD:
        return agent.items == Item.STRAWBERRIES || agent.items == Item.DOUGH;
      default: 
        throw new RuntimeException("Unknown item : "+ Integer.toBinaryString(item));
      }
    }
  }
  
  private static boolean canUseOven(State state, Agent agent) {
    if (state.ovenContents == 0) {
      return Item.isWarmable(agent.items);
    } else if (Item.isWarmable(state.ovenContents)) {
      return false;
    } else {
      return agent.items == 0 
          || (
              ((agent.items & Item.DISH) != 0) 
              && Integer.bitCount(agent.items) < 5);
    }
  }

  private static boolean canAddTier1Ingredient(Agent agent) {
    return agent.items == 0;
  }

  private static boolean canAddTier2Ingredient(int item, Agent agent) {
    return agent.items == 0 
        || ((agent.items & Item.DISH) != 0          // on a une assiette 
              && (agent.items & item)==0)          // on a pas deja l'item
              && Integer.bitCount(agent.items) < 5  // elle est pas pleine
              ;
  }      
}
