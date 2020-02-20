package lcm2;

public enum Location {
  GRAVEYARD(-100), 
  MY_HAND(0),
  MY_BOARD(1),
  HIS_BOARD(-1), 
  HIS_HAND(-2);
  
  static {
    GRAVEYARD.mirror = GRAVEYARD;
    MY_HAND.mirror = HIS_HAND;
    MY_BOARD.mirror = HIS_BOARD;
    HIS_HAND.mirror = MY_HAND;
    HIS_BOARD.mirror = MY_BOARD;
  }
  int value;
  public Location mirror;
  Location(int value) {
    this.value = value;
  }
  public int intValue() {
    return value;
  }
  public static Location fromValue(int i) {
    switch(i) {
    case 0: return MY_HAND;
    case 1: return MY_BOARD;
    case -1: return HIS_BOARD;
    case -2: return HIS_HAND;
    default:
      return GRAVEYARD;
    }
  }
}
