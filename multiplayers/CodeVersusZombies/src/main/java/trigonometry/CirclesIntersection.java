package trigonometry;

import java.util.ArrayList;
import java.util.List;

public class CirclesIntersection {
  static List<Point> getPotentialPoints(Circle origin, List<Circle> circles) {
    List<Point> points = new ArrayList<>();
    boolean result;
    for (int i=0;i<360;i++) {
      Point lastGood = null;
      double rad = i*2*Math.PI/360;
      double cos = Math.cos(rad);
      double sin = Math.sin(rad);
      double base = 1.0;
      double range = 0.5;
      do {
        Point pOnCircle = new Point(origin.center.x + origin.radius*cos*base,
          origin.center.x + origin.radius*sin*base);
        boolean isSafe = checkPointToCircle(pOnCircle, circles);
        if (isSafe) {
          lastGood = pOnCircle;
        }
        if (base != 1) {
          if (isSafe) {
            base = base + range;
          } else {
            base = base - range;
          }
          range /= 2;
        } else {
          base = 0.5;
          range = 0.25;
        }
      } while(range > 0.001);
      points.add(lastGood);
    }
    return points;
  }

  static private boolean checkPointToCircle(Point p, List<Circle> circles) {
    for (Circle c : circles) {
      if (p.squareDistance(c.center) < c.radius*c.radius) {
        return false;
      }
    }
    return true;
  }
}
