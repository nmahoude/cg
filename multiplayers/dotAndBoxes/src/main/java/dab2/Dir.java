package dab2;

public enum Dir {
  TOP (State.TOP_MASK, false, 'T'),
  RIGHT (State.RIGHT_MASK, true, 'R'),
  BOTTOM (State.BOTTOM_MASK,false, 'B'),
  LEFT (State.LEFT_MASK, true, 'L');
  
  public final int mask;
  public final boolean vertical;
  public final char letter;
  
  private Dir(int mask, boolean vertical, char letter) {
    this.mask = mask;
    this.vertical = vertical;
    this.letter = letter;
  }
  
  public static Dir missingDir(int mask) {
    for (Dir dir : Dir.values()) {
      if ((mask & dir.mask) == 0) return dir;
    }
    return null;
  }

  char dir() {
    return letter;
  }
}
