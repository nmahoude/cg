package lcm.cards;

public enum CardType {
  CREATURE(0),
  ITEM_GREEN(1),
  ITEM_RED(2),
  ITEM_BLUE(3);
  
  private int index;
  CardType(int index) {
    this.index = index;
  }
  public int intValue() {
    return index;
  }
  static CardType fromValue(int value) {
    for (CardType ct : CardType.values()) {
      if (ct.index == value) {
        return ct;
      }
    }
    return null;
  }
}
