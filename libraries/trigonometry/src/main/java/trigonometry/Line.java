package trigonometry;

import java.util.List;

public class Line {
  public final Point p1;
  public final Point p2;
  
  public Line(Point p1, Point p2) {
    this.p1 = p1;
    this.p2 = p2;
  }
  
  public Line(Point p1, Vector v1) {
    this.p1 = p1;
    this.p2 = p1.add(v1);
  }
  
  public List<Point> intersection(Circle circle) {
    return circle.getIntersectingPoints(p1, p2);
  }
}
