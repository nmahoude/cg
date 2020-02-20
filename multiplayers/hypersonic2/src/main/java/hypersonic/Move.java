package hypersonic;


public enum Move {
  UP(" ↑", 0, -1, false),
  LEFT(" ←", -1, 0, false),
  RIGHT(" →", 1, 0, false),
  DOWN(" ↓", 0, 1, false),
  STAY(" •", 0, 0, false),
  UP_BOMB("☢↑", 0 ,-1, true),
  LEFT_BOMB("☢←", -1, 0, true),
  RIGHT_BOMB("☢→", 1, 0, true),
  DOWN_BOMB("☢↓", 0, 1, true),
  STAY_BOMB("☢•", 0, 0, true);
  
  public final String name;
  public final int dx;
  public final int dy;
  public final boolean dropBomb;
  Move(final String name, int dx, int dy, boolean dropBomb) {
    this.name= name;
    this.dx = dx;
    this.dy = dy;
    this.dropBomb = dropBomb;
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
