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
}
