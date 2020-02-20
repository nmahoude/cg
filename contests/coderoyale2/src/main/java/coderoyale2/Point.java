package coderoyale2;

public class Point {
  public double x, y;

  public Point() {
    this.x = 0;
    this.y = 0;
  }
  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public double distanceTo(Point p) {
    return Math.sqrt((p.x-x)*(p.x-x) + (p.y-y)*(p.y-y));
  }
  public double distance2To(Point p) {
    return (p.x-x)*(p.x-x) + (p.y-y)*(p.y-y);
  }
  
  @Override
  public String toString() {
    return "("+(int)x+","+(int)y+")";
  }
  public void clampWithin(double xMin, double xMax, double yMin, double yMax) {
    if (x < xMin) x = xMin;
    if (x > xMax) x = xMax;
    if (y < yMin) y = yMin;
    if (y > yMax) y = yMax;
  }

}
