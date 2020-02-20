package tge;

public class Point {
  public final static Point unknown = new Point(-1,-1);
  private static Point cache[] = new Point[81];

  static {
    for (int x=0;x<9;x++) {
      for (int y=0;y<9;y++) {
        cache[x+9*y] = new Point(x,y);
      }
    }
  }
  public final int x;
  public final int y;

  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public static Point get(int x2, int y2) {
    return cache[x2+9*y2];
  }

  public Point translate(int dx, int dy) {
    return Point.get(x+dx,  y+dy);
  }
  
  @Override
  public String toString() {
    return "("+x+","+y+")";
  }

  public double manathan(Point target) {
    return Math.abs(target.x-x)+Math.abs(target.y-y);
  }
}
