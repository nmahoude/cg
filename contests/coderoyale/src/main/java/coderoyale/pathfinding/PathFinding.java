package coderoyale.pathfinding;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import coderoyale.Player;
import coderoyale.Pos;
import coderoyale.units.Disk;
import trigonometry.Circle;
import trigonometry.Point;
import trigonometry.Vector;

public class PathFinding {
  
  public static Route getPath(Pos from, Pos to) {
    Segment firstSegment = new Segment(new Point(from.x, from.y), new Point(to.x, to.y));
    Route currentRoute = new Route(firstSegment);
    
    List<Route> routesToExplore = new ArrayList<>();
    routesToExplore.add(currentRoute);
    
    Route bestRoute = null;
    while (!routesToExplore.isEmpty()) {
      routesToExplore = routesToExplore.stream().sorted((r1, r2) -> { return Double.compare(r1.length(), r2.length()); }).collect(Collectors.toList());
      if (bestRoute != null) {
        final double maxLength = bestRoute.length();
        routesToExplore.removeIf((Route r) -> { return r.length() > maxLength; });
      }
      Route route = routesToExplore.remove(0);
      Segment segment = route.currentEnd;
      List<Segment> newChildren = getCollisions(segment);
      if (newChildren == null) {
        // finished ! 
        Route finishedRoute = new Route(route, segment);
        if (bestRoute == null || finishedRoute.length() < bestRoute.length()) {
          bestRoute = finishedRoute;
        }
      } else {
        System.err.println("Subdiving");
        for (Segment child : newChildren) {
          System.err.println("   "+child);
          Route newRoute = new Route(currentRoute, child);
          routesToExplore.add(newRoute);
        }
      }
    }
    
    return bestRoute;
  }

  /**
   * return collisions with object on the map
   * @param segment
   * @return
   */
  private static List<Segment> getCollisions(Segment segment) {
    List<Segment> segments = new ArrayList<>();

    Disk closest = null;
    double bestDist = Double.POSITIVE_INFINITY;
    for (Disk disk : Player.allSites) {
      Circle c = new Circle(new Point(disk.pos.x, disk.pos.y), disk.radius+Player.me.radius);
      List<Point> intersectingPoints = c.getIntersectingPoints(segment.from, segment.to);
      for (Point p : intersectingPoints) {
        Vector direction = p.sub(segment.from);
        if (direction.dot(segment.to.sub(segment.from)) < 0) {
          break; // no collision with back sites
        }
        double dist = p.distTo(segment.from);
        if (dist < bestDist) {
          closest = disk;
          bestDist = dist;
        }
      }
    }
    
    if (closest == null) {
      return null; // no intersection, end of route
    } else {
      // find tangeantial points
      Circle c = new Circle(new Point(closest.pos.x, closest.pos.y), closest.radius+Player.me.radius);
      Vector OC = c.center.sub(segment.from);
      Vector CP = segment.to.sub(segment.from).normalize();
      double radius = 1.1  *c.radius;
      Vector CPp1 = new Vector(radius * CP.vy, -radius * CP.vx);
      Vector CPp2 = new Vector(- radius * CP.vy, radius * CP.vx);

      segments.add(new Segment(segment.from, segment.from.add(OC).add(CPp1)));
      segments.add(new Segment(segment.from, segment.from.add(OC).add(CPp2)));
      return segments;
    }
  }
}
