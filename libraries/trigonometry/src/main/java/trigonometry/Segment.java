package trigonometry;

import java.util.Iterator;
import java.util.List;

public class Segment extends Line {
  private double length;

  public Segment(Point p1, Point p2) {
    super(p1, p2);
    length = p2.sub(p1).length();
  }

  public Segment(Point p, Vector v) {
    super(p, v);
    length = p2.sub(p1).length();
  }
  
  @Override
  public List<Point> intersection(Circle circle) {
    List<Point> intersectingPoints = super.intersection(circle);
    
    for (Iterator<Point> ite = intersectingPoints.iterator(); ite.hasNext();) {
      Point point = (Point) ite.next();
      if (!isInside(point)) {
        ite.remove();
      }
    }
    return intersectingPoints;
  }

  /**
   * Only works if you know the point is ON the line
   */
  public boolean isInside(Point point) {
    Vector segment = segment();
    return point.sub(p1).dot(segment) > 0
        && p2.sub(point).dot(segment) > 0;
  }
  
  private Vector segment() {
    return p2.sub(p1);
  }
  
  public double distanceTo(Point p) {
    Point a = p1;
    Vector n = p2.sub(p1).normalize();
    
    Vector pa = p.sub(a);
    double projDist = pa.dot(n);
    if (projDist < 0) {
      return pa.length();
    }
    if (projDist > length) {
      return p.sub(p2).length();
    }
    
    Vector proj = n.dot(projDist);
    Vector h = pa.sub(proj);
    return h.length();
  }
}
