package god.utils;

public class Point {
  public final int x;
  public final int y;
 
  public Point(int xx, int yy) {
      x = xx;
      y = yy;
  }

  public double distance(Point p) {
    return Math.sqrt((x-p.x)*(x-p.x) + (y-p.y)*(y-p.y));
  }

  public int dist2(Point p) {
    return (x-p.x)*(x-p.x) + (y-p.y)*(y-p.y);
  }
}
