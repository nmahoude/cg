package trigonometry;

import java.util.Iterator;
import java.util.List;

public class Segment extends Line {
  public Segment(Point p1, Point p2) {
    super(p1, p2);
  }

  public Segment(Point p, Vector v) {
    super(p, v);
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
}
