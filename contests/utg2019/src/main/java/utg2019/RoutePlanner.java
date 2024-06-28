package utg2019;

import trigonometryInt.Point;

public class RoutePlanner {

  
  public static int etaToBeOn(Point pos, Point target) {
    int distance = pos.distance(target);
    if (distance == 0) return 0;
    return (int)Math.ceil(1.0*(distance) / 4);
  }

  public static int etaToBeInVicinity(Point pos, Point target, boolean hasItemToGo, int minCoolDown) {

    if (hasItemToGo) {
      return etaToBeInVicinity(pos, target);
    } else {
      int joinBaseEta = Math.max(etaToBeOn(pos, Point.get(0, pos.y)), minCoolDown-1); // if still on cooldown, take it into account
      
      return joinBaseEta +  etaToBeInVicinity(Point.get(0, pos.y), target);
    }
  }

  private static int etaToBeInVicinity(Point pos, Point target) {
    int distance = pos.distance(target);
    return (int)Math.ceil(1.0*(distance-1) / 4);
  }
}
