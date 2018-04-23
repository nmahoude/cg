package trigonometry;

import java.util.ArrayList;
import java.util.List;

public class Circle {
  static final double PRECISION = 0.001;
  
  final public Point center;
  final public double radius;
  
  public Circle(Point center, double radius) {
    this.center = center;
    this.radius = radius;
  }
  
  @Override
  public String toString() {
    return "Circle center= "+center+" R= "+radius;
  }
  
  public boolean isIn(Point p) {
    return center.distTo(p) <= radius;
  }
  public boolean isOn(Point p) {
    return Math.abs(center.distTo(p) - radius) < PRECISION;
  }

  public List<Point> getIntersectingPoints(Point p, Vector v) {
    return getIntersectingPoints(p, p.add(v));
  }
  
  public List<Point> getIntersectingPoints(Point p1, Point p2) {
    double x1 = p1.x;
    double y1 = p1.y;
    double x2 = p2.x;
    double y2 = p2.y;
    
    double a = (y2-y1) / (x2-x1);
    double b = y1 - x1 * ((y2-y1) / (x2-x1));
    List<Point> intersectingPoints = getIntersectingPoints(a, b);
    
    return intersectingPoints;
  }
  
  public List<Point> getIntersectingPoints(double a, double b) {
    List<Point> lst = new ArrayList<>();
    double cx = center.x;
    double cy = center.y;

    double A = 1 + a * a;
    double B = 2 * (-cx + a * b - a * cy);
    double C = cx * cx + cy * cy + b * b - 2 * b * cy - radius * radius;
    double delta = B * B - 4 * A * C;

    if (delta > 0) {
      double sqrtDelta = Math.sqrt(delta);
      double x = (-B - sqrtDelta) / (2 * A);
      double y = a * x + b;
      lst.add(new Point(x, y));

      x = (-B + sqrtDelta) / (2 * A);
      y = a * x + b;
      lst.add(new Point(x, y));
    } else if (delta == 0) {
      double x = -B / (2 * A);
      double y = a * x + b;
      lst.add(new Point(x, y));
    } else {
      // lst will remain empty, no solution
    }
    return lst;
  }
}