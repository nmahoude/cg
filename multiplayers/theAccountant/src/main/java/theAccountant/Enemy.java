package theAccountant;

import java.util.ArrayList;
import java.util.List;
import trigonometry.Vector;
import trigonometry.Point;

public class Enemy extends Movable {

  private static final double ONE_OVER_ONE_POINT_2 = 0.833333;
  static final int ENEMY_WOLFF_RANGE = 2000;
  static final int ENEMY_DATAPOINT_RANGE = 500;
  static final int ENEMY_MOVE = 500;
  int lifePoints;
  int id;
  int turnToReachTarget;
  
  public Enemy(GameEngine engine) {
    super(engine,ENEMY_MOVE);
  }

  public void init() {
    DataPoint dp = findNearestDataPoint();
    turnToReachTarget = (int)Math.round(dp.p.squareDistance(p) / (ENEMY_MOVE*ENEMY_MOVE))+1; // FIXME bug here
  }
  
  boolean checkForDeath(Point wolffPos) {
    return wolffPos.squareDistance(p) <= ENEMY_WOLFF_RANGE*ENEMY_WOLFF_RANGE;
  }

  public void moveToNearestTarget() {
    DataPoint dp = findNearestDataPoint();
    move(dp.p);
  }

  public void checkReachTarget() {
    DataPoint dp = findNearestDataPoint();
    if (dp.p.squareDistance(p) < ENEMY_DATAPOINT_RANGE*ENEMY_DATAPOINT_RANGE) {
      gameEngine.removeDataPoint(dp);
    }
  }
  
  // validated against game engine
  List<Point> stepsToTarget(Point target) {
    List<Point> ps = new ArrayList<>();
    Point futurePos = p;
    while (true) {
      Vector v = new Vector(target.x-futurePos.x, target.y-futurePos.y);
      double length = v.length();
      if (length < ENEMY_DATAPOINT_RANGE) {
        ps.add(target);
        return ps;
      } else {
        futurePos = new Point((int)(futurePos.x+ENEMY_MOVE * v.vx / length), (int)(futurePos.y+ENEMY_MOVE * v.vy / length));
        ps.add(futurePos);
      }
    }
  }
  
  public DataPoint findNearestDataPoint() {
    DataPoint closestDP = null;
    int minDist = Integer.MAX_VALUE;
    int minId = Integer.MAX_VALUE;
    for (DataPoint dp : gameEngine.dataPoints) {
      int distance = (int)dp.p.squareDistance(p);
      if (distance < minDist 
          || distance == minDist && minId > dp.id) {
        minDist = distance;
        closestDP = dp;
        minId = dp.id;
      }
    }
    return closestDP;
  }

  public int distanceToOneShot() {
    return (int)Math.pow(125_000.0/lifePoints, ONE_OVER_ONE_POINT_2);
  }
  
  public Enemy duplicate(GameEngine newEngine) {
    Enemy e = new Enemy(newEngine);
    e.p = p;
    e.id = id;
    e.lifePoints = lifePoints;
    e.turnToReachTarget = turnToReachTarget;
    return e;
  }

  public void updateTurnToReachTarget() {
    DataPoint dp = findNearestDataPoint();
    turnToReachTarget = (int)Math.round(1.0*dp.p.distTo(p) / ENEMY_MOVE); // FIXME bug here
  }

}
