package ww;

public enum Dir {
  
  N(0, -1), NE(1, -1), E(1, 0), SE(1, 1), S(0,1), SW(-1, 1), W(-1, 0), NW(-1, -1);
  
  public final int dx;
  public final int dy;

  Dir(int dx, int dy) {
    this.dx = dx;
    this.dy = dy;
  }
}
