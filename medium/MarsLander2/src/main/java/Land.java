import java.util.ArrayList;
import java.util.List;

import trigonometry.Point;
import trigonometry.Vector;

public class Land {

  public Point[] points;
  public int platformIndex = 0; // to +1
  public Point target = null;
  
  public void findPlatform() {
    for (int i = 1;i<points.length;i++) {
      if (points[i-1].y == points[i].y) {
        platformIndex = i-1;
        target = new Point(0.5*points[i].x+0.5*points[i-1].x, points[i].y);
      }
    }
  }

  List<Point> getTrajectory(Point cPos) {
    List<Point> trajPoints = new ArrayList<>();
    trajPoints.add(cPos);
    int currentIndex = getIndex(cPos);
    Point currentPos = cPos;
    for (int i=currentIndex; i<platformIndex;i++) {
      Point testP = points[i];
      if (!lineIsAboveAll(currentPos, testP, currentIndex, i-1)) {
        trajPoints.add(points[i-1]);
        currentPos = points[i-1];
      }
    }
    trajPoints.add(new Point(target.x, target.y+100));
    trajPoints.add(target);
    return trajPoints;
  }
  
  private boolean lineIsAboveAll(Point currentPos, Point testP, int currentIndex, int lastCheck) {
    for (int i =currentIndex;i<lastCheck;i++) {
      if (points[i].isAbove(currentPos, testP)) {
        return false;
      }
    }
    return true;
  }

  private int getIndex(Point cPos) {
    for (int i=0;i<points.length;i++) {
      if (points[i+1].x >cPos.x && points[i].x < cPos.x) {
        return i+1;
      }
    }
    return -1;
  }

  public Point getNextStepTarget(Point point) {
    for (Point p : points) {
      if (p.x > point.x) {
        return p;
      }
    }
    return null;
  }
}
