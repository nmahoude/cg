package ww2;

public class Point {
  public final int x, y;
  public final long mask;
  
  static Point unknown = new Point(-1, -1);
  
  private static Point points[] = new Point[64];
  static {
    for (int x=0;x<8;x++) {
      for (int y=0;y<8;y++) {
        points[x+8*y] = new Point(x,y);
      }
    }
  }
  
  private Point(int x, int y) {
    this.x = x;
    this.y = y;
    this.mask = toBitMask(x, y);
  }
  
  public static Point get(int x, int y) {
    if (x == -1) return unknown;
    return points[x+8*y];
  }
  
  public static final long toBitMask(int x, int y) {
    if (x == -1) return 0L;
    return 0b1L << (x + 8L * y);
  }

}
