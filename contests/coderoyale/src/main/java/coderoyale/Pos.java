package coderoyale;

public class Pos {
  public int x;
  public int y;

  public Pos() {
  }

  public Pos(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  @Override
  public String toString() {
    return "" + x + " "+y;
  }
  public double dist(Pos pos) {
    return Math.sqrt(dist2(pos));
  }
  
  double dist2(Pos pos) {
    return (pos.x-x)*(pos.x-x) + (pos.y-y)*(pos.y-y);
  }
}
