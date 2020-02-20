package greatescape;

public class Point {
  public int x;
  public int y;

  public Point() {
  }
  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public static Point get(int x, int y) {
    return new Point(x, y);
  }
}
