package tron.common;

public class Point {
  public static int MAX_X;
  public static int MAX_Y;
  
  public final static Point unknown = new Point(-1,-1);
  private static Point cache[];

  public static void init(int maxX, int maxY) {
    MAX_X = maxX;
    MAX_Y = maxY;
    cache = new Point[MAX_X*MAX_Y];
    for (int x=0;x<MAX_X;x++) {
      for (int y=0;y<MAX_Y;y++) {
        cache[x+MAX_X*y] = new Point(x,y);
      }
    }
  }
  
  public final int x;
  public final int y;

  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public static Point get(int x, int y) {
    return cache[x+MAX_X*y];
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