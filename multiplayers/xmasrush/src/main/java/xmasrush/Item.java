package xmasrush;

public class Item {
  public final static String[] names = new String[] { "ARROW", "BOOK", "CANE", "CANDY", "DIAMOND", "FISH", "MASK", "KEY", "POTION", "SCROLL", "SHIELD", "SWORD"};

  public static int getItem(char[] itemName) {
    int itemIndex = -1;
    if (itemName[0] == 'A') itemIndex = 0;
    else if (itemName[0] == 'B') itemIndex = 1;
    else if (itemName[0] == 'C') {
      if (itemName[3] == 'E') {
        itemIndex = 2;
      } else {
        itemIndex = 3;
      }
    }
    else if (itemName[0] == 'D') itemIndex = 4;
    else if (itemName[0] == 'F') itemIndex = 5;
    else if (itemName[0] == 'M') itemIndex = 6;
    else if (itemName[0] == 'K') itemIndex = 7;
    else if (itemName[0] == 'P') itemIndex = 8;
    else if (itemName[0] == 'S') {
      if (itemName[1] == 'C') {
        itemIndex = 9;
      } else if (itemName[1] == 'H') { 
        itemIndex = 10;
      } else {
        itemIndex = 11;
      }
    }
    return itemIndex;
  }
}
