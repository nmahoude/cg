package hypersonic;


public enum Move {
  UP(" ↑"),
  UP_BOMB("☢↑"),
  LEFT(" ←"),
  LEFT_BOMB("☢←"),
  RIGHT(" →"),
  RIGHT_BOMB("☢→"),
  DOWN(" ↓"),
  DOWN_BOMB("☢↓"),
  STAY(" •"),
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
