package hypersonic;


public enum Move {
  UP(" ↑"),
  LEFT(" ←"),
  RIGHT(" →"),
  DOWN(" ↓"),
  STAY(" •"),
  UP_BOMB("☢↑"),
  LEFT_BOMB("☢←"),
  RIGHT_BOMB("☢→"),
  DOWN_BOMB("☢↓"),
  STAY_BOMB("☢•");
  
  String name;
  Move(final String name) {
    this.name= name;
  }
  @Override
  public String toString() {
    return name;
  }
  
  public static Move fromStr(String in) {
    for (Move m : Move.values()) {
      if (m.name.trim().equals(in.trim())) {
        return m;
      }
    }
    throw new RuntimeException("Unknown move : "+in);
  }
}
