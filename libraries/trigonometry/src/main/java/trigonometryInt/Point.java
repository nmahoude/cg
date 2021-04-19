package trigonometryInt;

public class Point {
  public static final Point Invalid = new Point(-1, -1);
  private static Point[] points = null;
  private static int width,height;
  
  /**
   * Mandatory init (for performance)
   * Make sure to call it only once.
   * 
   * @param width width of the cache of points (MAX_X)
   * @param height height of the cache of points (MAX_Y)
   */
  public static void init(int width, int height) {
    if (points != null) {
      return;
    }
    Point.width = width;
    Point.height = height;
    points = new Point[Point.width*Point.height];
    for (int y=0;y<height;y++) {
      for (int x=0;x<width;x++) {
        points[ x + y * width] = new Point(x,y);
      }
    }
  }
  
  
  public final int x;
  public final int y;
  public final int offset;
  
  private Point(int x, int y) {
    this.x = x;
    this.y = y;
    this.offset = x + y*width;
  }

  @Override
  public String toString() {
    return String.format("(%d, %d / %d)", x, y, offset);
  }
  
  public static Point get(int x, int y) {
    return points[x+y*width];
  }
  public static Point getSecured(int x, int y) {
    if (x<0 || x>=width) return Point.Invalid;
    if (y<0 || y>=height) return Point.Invalid;
    return points[x+y*width];
  }
  
  /* manhattan distance */
  public int distance(Point other) {
    return Math.abs(x-other.x)+Math.abs(y-other.y);
  }
  
  public int distance2(Point other) {
    return (x-other.x)*(x-other.x) + (y-other.y)*(y-other.y);
  }

  public boolean inRange(int radius, Point other) {
    return distance(other) <= radius;
  }
  
  public double euclidianDist(Point other) {
    return Math.sqrt((x-other.x)*(x-other.x) + (y-other.y)*(y-other.y)); 
  }

  @Override
  public int hashCode() {
    return offset;
  }
  @Override
  public boolean equals(Object obj) {
    return ((Point)obj).offset == this.offset;
  }

  public static Point getFromOffset(int offset) {
    return points[offset];
  }
}
