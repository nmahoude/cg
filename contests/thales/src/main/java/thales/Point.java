package thales;

public class Point {
  double x,y;

  public void update(int x, int y) {
    if (Player.DEBUG_OUTPUT) {
      System.err.println(String.format("%d %d", x, y));
    }
    this.x = x;
    this.y = y;
  }
}
