package coif_old;

public class Pos {
  public static final Pos INVALID = new Pos(-1, -1);
  static Pos positions[] = new Pos[12*12];
  public final int x;
  public final int y;
  public final int index;
  
  
  static {
    for (int y=0;y<12;y++) {
      for (int x=0;x<12;x++) {
        positions[x + y * 12] = new Pos(x,y);
      }
    }
  }
  
  private Pos(int x, int y) {
    this.x = x;
    this.y = y;
    this.index = x + 12 * y;
  }
  
  @Override
  public String toString() {
    return String.format("(%d %d)", x, y);
  }
  
  public static Pos get(int x,int y) {
    if (x <0 || x>=12 || y<0 || y>=12) {
      return INVALID;
    }
    return positions[x + y * 12];
  }

  public Pos move(Dir dir) {
    return get(this.x + dir.dx, this.y + dir.dy);
  }

  public boolean isValid() {
    return x != -1;
  }

  public int manhattan(Pos next) {
    // TODO CACHE ?
    return Math.abs(this.x-next.x) + Math.abs(this.y-next.y);
  }
}
