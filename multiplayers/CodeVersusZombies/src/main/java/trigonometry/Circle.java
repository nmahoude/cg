package trigonometry;

public class Circle {
  static final double PRECISION = 0.001;
  
  final Point center;
  final double radius;
  
  Circle(Point center, double radius) {
    this.center = center;
    this.radius = radius;
  }
  
  boolean isIn(Point p) {
    return center.distTo(p) <= radius;
  }
  boolean isOn(Point p) {
    return Math.abs(center.distTo(p) - radius) < PRECISION;
  }
}
