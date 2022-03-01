package botg;

public class Pos {
  public static final Pos VOID = new Pos(-1, -1);
  public final int x;
  public final int y;
  
  private Pos(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  @Override
  public String toString() {
    return String.format("(%d, %d)", x, y);
  }
  
  public static Pos from(int x, int y) {
    return new Pos(x,y);
  }
  
  public int sqDist(Pos from) {
    return (x-from.x)*(x-from.x) + (y-from.y)*(y-from.y);
  }

  public int dist(Pos from) {
    return (int) (Math.sqrt(sqDist(from)));
  }
}
