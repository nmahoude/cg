package xmashrush2;

public class Item {
  public final static String[] names = new String[] { "ARROW", "BOOK", "CANE", "CANDY", "DIAMOND", "FISH", "MASK", "KEY", "POTION", "SCROLL", "SHIELD", "SWORD"};
  public final static String[] letters = new String[] { "A", "B", "C", "Y", "D", "F", "M", "K", "P", "S", "H", "W"};

  public static int indexFromName(char[] itemName) {
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
  
  public static String name(int index) {
	  if (index <0) return "";
	  
	  return names[index % 100];
  }
  public static String letter(int index) {
	  if (index <0) return " ";
	  String letter = letters[index % 100];
	  if (index > 100) return letter.toLowerCase(); else return letter;
  }

	public static boolean p0Item(int itemIndex) {
		return itemIndex >=0 && itemIndex < 100;
	}
}
