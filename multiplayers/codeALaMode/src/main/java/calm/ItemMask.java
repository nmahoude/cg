package calm;

public class ItemMask {
  public static final int NOTHING = 0b0;
  public static final int EMPTY = NOTHING;
  
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
  public static final int FLOOR = 0b1000000000000000000;
  public static final int LAST = FLOOR;

  
  public static int fromString(String input) {
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


  public static int fromLetter(char value) {
    switch (value) {
    case 'D': return EQUIPMENT_DISH;
    case 'W': return EQUIPMENT_BELL;
    case 'B': return EQUIPMENT_BLUEBERRIES;
    case 'I': return EQUIPMENT_ICE_CREAM;
    case 'S': return EQUIPMENT_STRAWBERRY;
    case 'C': return EQUIPMENT_CHOPPING_BOARD;
    case 'H': return EQUIPMENT_DOUGH;
    case 'O': return EQUIPMENT_OVEN;
    }
    return 0;
  }


  public static String output(int preparing) {
      if (Integer.bitCount(preparing)>1) {
        String str="";
        int mask =1;
        for (int i=0;i<30;i++) {
          if ((mask & preparing) != 0) {
            str +=ItemMask.output(mask & preparing)+"-";
          }
          mask = mask << 1;
        }
        return str;
      }
      
      if (preparing == DISH)
        return "DISH";
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
}
