package others.jahz;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class JahzPlayer {

  // Easy to find switches
  public static final boolean isDebugOn = false;
  public static final boolean isCompareFailureOn = false;

  // AIs
  public static AI nearestWreckAI = new NearestWreckAI();
  public static AI waitAI = new WaitAI();
  public static AI MCAI = new MonteCarloAI(new ScoreAndNearestWreckSolutionEvaluation(), waitAI, 4);
  public static AI testAI = new NearestWreckAIWithRandomRage();
  public static AI testNoRandomAI0 = new NearestWreckAIWithFixedRage(0);
  public static AI testNoRandomAI1 = new NearestWreckAIWithFixedRage(1);
  public static AI testNoRandomAI2 = new NearestWreckAIWithFixedRage(2);
  public static AI ai = MCAI;

  // Game constants, write them here once for all. The match constants however should go in MatchConstants
  public static final int areaRadius = 6000;
  public static final int waterTownRadius = 3000;
  public static final int waterTownRadiusSquare = waterTownRadius * waterTownRadius;
  public static final int maxRound = 200;
  public static final int maxWater = 50;
  public static final int nbPlayer = 3;
  public static final int nbLooter = 3;
  public static final int nbReaper = 3;
  public static final int nbDestroyer = 3;
  public static final int nbDoof = 3;
  public static final int maxThrottle = 300;
  public static final int maxRage = 300;
  public static final int maxWrecks = 10;
  public static final int maxTankers = 10;
  public static final int maxOils = 9;
  public static final int maxTars = 9;
  public static final int maxGrenades = 3;
  public static final int myId = 0;
  public static final Point center = new Point(0, 0);

  public static final int skillRange = 2000;
  public static final int skillRangeSquare = skillRange * skillRange;
  public static final int skillRadius = 1000;
  public static final int skillDuration = 3;
  public static final int reaperSkillRage = 30;
  public static final int destroyerSkillRage = 60;
  public static final int doofSkillRage = 30;
  public static final int tarMassIncrease = 10;
  public static final int grenadeThrottle = 1000;
  public static final int tankerThrottle = 500;
  public static final int tankerMaxDistance = 8000;
  public static final double DOOF_RAGE_COEF = 1.0 / 100.0;

  public static double EPSILON = 0.00001;

  // Game variables
  private static GameState previousGameState;
  private static GameState predictedGameState;
  private static boolean stopGame = false;

  public static void main(String args[]) {

    Scanner in = new Scanner(System.in);
    initMatch(in);

    // game loop
    while (true) {

      GameState gs = initRound(in);

      Action[] actions = ai.computeIntact(gs);

      finalizeRound(actions, gs);

      out(actions);

    }
  }

  private static void initMatch(Scanner in) {

    if (isDebugOn) {
      Print.debug("Starting the match !");
      ai.printAI();
    }

  }

  private static GameState initRound(Scanner in) {

    GameState result = null;
    GameEngine.nbApplyAction = 0; // Reset it for each round

    int myScore = in.nextInt();
    Time.startRoundTimer();

    if (previousGameState == null) {
      result = new GameState(1);
    } else {
      result = new GameState(previousGameState.round + 1);
    }

    result.score[0] = myScore;
    result.score[1] = in.nextInt();
    result.score[2] = in.nextInt();
    result.rage[0] = in.nextInt();
    result.rage[1] = in.nextInt();
    result.rage[2] = in.nextInt();

    int unitCount = in.nextInt();
    for (int i = 0; i < unitCount; i++) {
      int unitId = in.nextInt();
      int unitType = in.nextInt();
      int player = in.nextInt();
      float mass = in.nextFloat();
      int radius = in.nextInt();
      int x = in.nextInt();
      int y = in.nextInt();
      int vx = in.nextInt();
      int vy = in.nextInt();
      int extra = in.nextInt();
      int extra2 = in.nextInt();

      switch (unitType) {
      case 0:
        Reaper reaper = new Reaper(unitId, new Point(x, y), new Vector(vx, vy), player);
        result.reapers[player] = reaper;
        break;
      case 1:
        Destroyer destroyer = new Destroyer(unitId, new Point(x, y), new Vector(vx, vy), player);
        result.destroyers[player] = destroyer;
        break;
      case 2:
        Doof doof = new Doof(unitId, new Point(x, y), new Vector(vx, vy), player);
        result.doofs[player] = doof;
        break;
      case 3:
        Tanker tanker = new Tanker(unitId, new Point(x, y), radius, new Vector(vx, vy), mass, extra, extra2);
        result.tankers.add(tanker);
        break;
      case 4:
        Wreck wreck = new Wreck(unitId, new Point(x, y), radius, extra);
        result.wrecks.add(wreck);
        break;
      case 5:
        Tar tar = new Tar(unitId, new Point(x, y), radius, extra);
        result.tars.add(tar);
        break;
      case 6:
        Oil oil = new Oil(unitId, new Point(x, y), radius, extra);
        result.oils.add(oil);
        break;
      default:
        Print.debugForced("Unrecognized unit type: " + unitType);
        stopGame = true;
        break;
      }

    }

    if (isDebugOn) {
      // result.print();
    }

    compareInputAgainstPrediction(result);

    return result;

  }

  // Runs a comparison between what CG gives us, and what we had predicted. Will stop the game if any difference is found, in order to highlight the need of a new test
  private static void compareInputAgainstPrediction(GameState gameStateFromInput) {
    if (isCompareFailureOn && predictedGameState != null && !predictedGameState.equals(gameStateFromInput)) {

      if (isDebugOn) {
        Print.debug("Ran comparison between the input and the prediction and predicted:");
        predictedGameState.print();
        Print.debug("Stop the game");
      }

      stopGame = true;

    } else {
      if (isDebugOn) {
        Print.debug("Prediction ok !");
      }
    }

  }

  private static void finalizeRound(Action[] actions, GameState gs) {

    if (!stopGame) {

      Action[] opActions1 = JahzPlayer.waitAI.computeIntact(gs);
      Action[] opActions2 = JahzPlayer.waitAI.computeIntact(gs);

      if (isDebugOn) {
        Print.debug("My actions: |" + actions[0] + "|" + actions[1] + "|" + actions[2]);
        Print.debug("Op1actions: |" + opActions1[0] + "|" + opActions1[1] + "|" + opActions1[2]);
        Print.debug("Op2actions: |" + opActions2[0] + "|" + opActions2[1] + "|" + opActions2[2]);
      }

      previousGameState = gs;
      // predictedGameState = GameEngine.applyActionWithCopy(gs, actions, opActions1, opActions2);

      if (isDebugOn && predictedGameState != null) {
        if (predictedGameState.gameResult == GameResult.THIRD) {
          Print.debug("We'll loose this game :(");
        } else if (predictedGameState.gameResult == GameResult.FIRST) {
          Print.debug("We'll win this game :)");
        } else if (predictedGameState.gameResult == GameResult.SECOND) {
          Print.debug("We'll finish second :|");
        }
        Print.debug("Nb iterations done during the round: " + GameEngine.nbApplyAction);
        Time.debugDuration("Total round duration");
      }

    }

  }

  private static void out(Action[] actions) {
    if (stopGame) {
      System.out.println("Failure!");
    } else {

      for (Action action : actions) {
        if (action != null) {
          switch (action.actionType) {
          case THROTTLE:
            System.out.println((int) action.target.x + " " + (int) action.target.y + " " + action.throttle + " " + action.message);
            break;
          case SKILL:
            System.out.println(action.actionType + " " + (int) action.target.x + " " + (int) action.target.y + " " + action.message);
            break;
          case WAIT:
            System.out.println(action.actionType + " " + action.message);
            break;
          default:
            break;
          }
        } else {
          System.out.println(ActionType.WAIT + " NULL");
        }
      }

    }

  }

}

class Time {
  // Time constants
  private static final int maxRoundTime = 50; // 50 ms max to answer
  private static final int roundTimeMargin = 15;
  private static final int maxFirstRoundTime = 1000; // 1 s max to answer for first turn only
  private static final int firstRoundTimeMargin = 50;
  private static final int maxRoundTimeWithMargin = maxRoundTime - roundTimeMargin;
  private static final int maxFirstRoundTimeWithMargin = maxFirstRoundTime - firstRoundTimeMargin;
  public static boolean noTimeLimit = false;

  // Time variables
  private static long roundStartTime;

  public static void startRoundTimer() {
    roundStartTime = System.currentTimeMillis();
  }

  public static boolean isTimeLeft(boolean firstTurn) {
    return getRoundDuration() < maxRoundTimeWithMargin || (firstTurn && getRoundDuration() < maxFirstRoundTimeWithMargin) || noTimeLimit;
  }

  public static boolean isTimeLeft() {
    return isTimeLeft(false);
  }

  public static long getRoundDuration() {
    return System.currentTimeMillis() - roundStartTime;
  }

  public static void debugDuration(String message) {
    Print.debug(message + ": " + getRoundDuration());
  }

}

class Print {

  public static void debug(String message) {
    if (JahzPlayer.isDebugOn) {
      System.err.println(message);
    }
  }

  public static void debugForced(String message) {
    System.err.println(message);
  }

  private static final String debugStartLine = "\"";
  private static final String debugEndLine = "\",";
  public static final String debugSep = " ";

  // Debug for later input in tests
  public static void debugForInput(String message) {
    debug(debugStartLine + message + debugEndLine);
  }
}

enum ActionType {
  THROTTLE, SKILL, WAIT
}

class Action {
  public ActionType actionType;
  public Point target;
  public int throttle;
  public String message;

  private static final Action waitAction = new Action(ActionType.WAIT, null, -1, "Wait what ?");

  private Action(ActionType actionType, Point target, int throttle, String message) {
    super();
    this.actionType = actionType;
    this.target = target;
    this.throttle = throttle;
    this.message = message;
  }

  public static Action getThrottleAction(Point target, int throttle, String message) {
    return new Action(ActionType.THROTTLE, target, throttle, message);
  }

  public static Action getSkillAction(Point target, String message) {
    return new Action(ActionType.SKILL, target, -1, message);
  }

  public static Action getWaitAction() {
    return waitAction;
  }

  @Override
  public String toString() {
    return "Action " + actionType + " " + target + " " + throttle;
  }

}

class GameStateObject {

  public void print() {
    Print.debugForInput(toString());
  }

}

class Point extends GameStateObject {
  public double x;
  public double y;

  public static double epsilon = 0.00000001;

  public Point(double x, double y) {
    super();
    this.x = x;
    this.y = y;
  }

  public void round() {
    x = CGRound(x);
    y = CGRound(y);
  }

  public void truncate() {
    x = (int) x;
    y = (int) y;
  }

  public void addVector(Vector v) {
    this.x += v.x;
    this.y += v.y;
  }

  public Point copy() {
    return new Point(x, y);
  }

  public static int CGRound(double d) {
    return (int) Math.signum(d) * (int) Math.round(Math.abs(d));
  }

  public static double getDistance(Point p1, Point p2) {
    return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
  }

  public static double getDistanceSquare(Point p1, Point p2) {
    return (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y);
  }

  private static final DecimalFormat formatter = new DecimalFormat("+#00000.00;-#");

  @Override
  public String toString() {
    return "Point " + formatter.format(x) + " " + formatter.format(y);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(x);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Point other = (Point) obj;
    if (Math.abs(x - other.x) > epsilon)
      return false;
    if (Math.abs(y - other.y) > epsilon)
      return false;
    return true;
  }

  public boolean equalsRounded(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Point other = (Point) obj;
    if (CGRound(x) != CGRound(other.x))
      return false;
    if (CGRound(y) != CGRound(other.y))
      return false;
    return true;
  }

}

class Vector extends GameStateObject {
  public double x;
  public double y;
  private double norm;
  private double normSquare;

  private static final int offsetDegree = 90;

  public Vector(double x, double y) {
    super();
    this.x = x;
    this.y = y;
  }

  public Vector(Point p1, Point p2) {
    this(p2.x - p1.x, p2.y - p1.y);
  }

  public Vector(double angle, double norm, int i) {
    this(norm * Math.cos(Math.toRadians(angle + offsetDegree)), norm * Math.sin(Math.toRadians(angle + offsetDegree)));
  }

  public double getNorm() {
    if (norm == 0) {
      norm = Math.sqrt(x * x + y * y);
    }
    return norm;
  }

  public double getNormSquare() {
    if (normSquare == 0) {
      normSquare = x * x + y * y;
    }
    return normSquare;
  }

  public void normalize() {
    x = x / getNorm();
    y = y / getNorm();
    this.norm = 1;
    this.normSquare = 1;
  }

  public void add(Vector v) {
    this.x += v.x;
    this.y += v.y;
    resetNorm();
  }

  public void mul(double d) {
    this.x *= d;
    this.y *= d;
    resetNorm();
  }

  public void resetNorm() {
    this.norm = 0;
    this.normSquare = 0;
  }

  public void round() {
    x = Point.CGRound(x);
    y = Point.CGRound(y);
  }

  public Vector copy() {
    return new Vector(x, y);
  }

  private static final DecimalFormat formatter = new DecimalFormat("+#0000.00;-#");

  @Override
  public String toString() {
    return "Vector " + formatter.format(x) + " " + formatter.format(y);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(x);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Vector other = (Vector) obj;
    if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
      return false;
    if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
      return false;
    return true;
  }

  public boolean equalsRounded(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Vector other = (Vector) obj;
    if (Point.CGRound(x) != Point.CGRound(other.x))
      return false;
    if (Point.CGRound(y) != Point.CGRound(other.y))
      return false;
    return true;
  }

}

enum EntityType {
  REAPER, DESTROYER, DOOF, TANKER, WRECK, TAR, OIL
}

abstract class Entity extends GameStateObject {

  public EntityType type;
  public int id;
  public Point p;
  public int radius;

  public Entity(EntityType type, int id, Point p, int radius) {
    super();
    this.type = type;
    this.id = id;
    this.p = p;
    this.radius = radius;
  }

  public Entity(Entity original) {
    super();
    this.type = original.type;
    this.id = original.id;
    this.p = original.p.copy();
    this.radius = original.radius;
  }

  public abstract Entity copy();

  @Override
  public String toString() {
    return String.format("%1$-9s", type) + " " + String.format("%02d", id) + " " + p + " " + String.format("%04d", radius);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((p == null) ? 0 : p.hashCode());
    result = prime * result + radius;
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Entity other = (Entity) obj;
    if (p == null) {
      if (other.p != null)
        return false;
    } else if (!p.equals(other.p))
      return false;
    if (radius != other.radius)
      return false;
    if (type != other.type)
      return false;
    return true;
  }

}

abstract class MovingEntity extends Entity {

  public Vector speed;
  public double mass;
  public double friction;
  public boolean isInOil;

  public MovingEntity(EntityType type, int id, Point p, int radius, Vector speed, double mass, double friction) {
    super(type, id, p, radius);
    this.speed = speed;
    this.mass = mass;
    this.friction = friction;
    this.isInOil = false;
  }

  public MovingEntity(MovingEntity original) {
    super(original);
    this.speed = original.speed.copy();
    this.mass = original.mass;
    this.friction = original.friction;
    this.isInOil = original.isInOil;
  }

  public void boost(Point target, int thrust) {

    double distance = Point.getDistance(this.p, target);

    // Avoid a division by zero
    if (Math.abs(distance) > JahzPlayer.EPSILON) {
      double coef = (((double) thrust) / mass) / distance;
      speed.x += (target.x - this.p.x) * coef;
      speed.y += (target.y - this.p.y) * coef;
      speed.resetNorm();
    }

  }

  public void move(double time) {
    p.x += speed.x * time;
    p.y += speed.y * time;
  }

  public void applyFriction() {
    speed.mul(1.0 - friction);
  }

  public void round() {
    p.round();
    speed.round();
  }

  // Search the next collision with the map border
  Collision getCollision() {
    // Check instant collision
    if (Point.getDistance(p, JahzPlayer.center) + radius >= JahzPlayer.areaRadius) {
      return new Collision(0.0, this, null);
    }

    // We are not moving, we can't reach the map border
    if (speed.getNormSquare() == 0) {
      return Collision.NULL_COLLISION;
    }

    // Search collision with map border
    // Resolving: sqrt((x + t*vx)^2 + (y + t*vy)^2) = MAP_RADIUS - radius <=> t^2*(vx^2 + vy^2) + t*2*(x*vx + y*vy) + x^2 + y^2 - (MAP_RADIUS - radius)^2 = 0
    // at^2 + bt + c = 0;
    // a = vx^2 + vy^2
    // b = 2*(x*vx + y*vy)
    // c = x^2 + y^2 - (MAP_RADIUS - radius)^2

    double a = speed.getNormSquare();

    if (a <= 0.0) {
      return Collision.NULL_COLLISION;
    }

    double b = 2.0 * (p.x * speed.x + p.y * speed.y);
    double c = p.x * p.x + p.y * p.y - (JahzPlayer.areaRadius - radius) * (JahzPlayer.areaRadius - radius);
    double delta = b * b - 4.0 * a * c;

    if (delta <= 0.0) {
      return Collision.NULL_COLLISION;
    }

    double t = (-b + Math.sqrt(delta)) / (2.0 * a);

    if (t <= 0.0) {
      return Collision.NULL_COLLISION;
    }

    return new Collision(t, this, null);
  }

  // Search the next collision with an other unit
  Collision getCollision(MovingEntity u) {
    // Check instant collision
    if (Point.getDistance(p, u.p) <= radius + u.radius) {
      return new Collision(0.0, this, u);
    }

    // Both units are motionless
    if (speed.x == 0.0 && speed.y == 0.0 && u.speed.x == 0.0 && u.speed.y == 0.0) {
      return Collision.NULL_COLLISION;
    }

    // Change referencial
    // Unit u is not at point (0, 0) with a speed vector of (0, 0)
    double x2 = p.x - u.p.x;
    double y2 = p.y - u.p.y;
    double r2 = radius + u.radius;
    double vx2 = speed.x - u.speed.x;
    double vy2 = speed.y - u.speed.y;

    double a = vx2 * vx2 + vy2 * vy2;

    if (a <= 0.0) {
      return Collision.NULL_COLLISION;
    }

    double b = 2.0 * (x2 * vx2 + y2 * vy2);
    double c = x2 * x2 + y2 * y2 - r2 * r2;
    double delta = b * b - 4.0 * a * c;

    if (delta < 0.0) {
      return Collision.NULL_COLLISION;
    }

    double t = (-b - Math.sqrt(delta)) / (2.0 * a);

    if (t <= 0.0) {
      return Collision.NULL_COLLISION;
    }

    return new Collision(t, this, u);
  }

  private static double IMPULSE_COEFF = 0.5;
  private static double MIN_IMPULSE = 30.0;

  // Bounce between 2 units
  void bounce(MovingEntity u) {
    double mcoeff = (mass + u.mass) / (mass * u.mass);
    double nx = p.x - u.p.x;
    double ny = p.y - u.p.y;
    double nxnysquare = nx * nx + ny * ny;
    double dvx = speed.x - u.speed.x;
    double dvy = speed.y - u.speed.y;
    double product = (nx * dvx + ny * dvy) / (nxnysquare * mcoeff);
    double fx = nx * product;
    double fy = ny * product;
    double m1c = 1.0 / mass;
    double m2c = 1.0 / u.mass;

    speed.x -= fx * m1c;
    speed.y -= fy * m1c;
    u.speed.x += fx * m2c;
    u.speed.y += fy * m2c;
    speed.resetNorm();
    u.speed.resetNorm();

    fx = fx * IMPULSE_COEFF;
    fy = fy * IMPULSE_COEFF;

    // Normalize vector at min or max impulse
    double impulse = Math.sqrt(fx * fx + fy * fy);
    double coeff = 1.0;
    if (impulse > JahzPlayer.EPSILON && impulse < MIN_IMPULSE) {
      coeff = MIN_IMPULSE / impulse;
    }

    fx = fx * coeff;
    fy = fy * coeff;

    speed.x -= fx * m1c;
    speed.y -= fy * m1c;
    u.speed.x += fx * m2c;
    u.speed.y += fy * m2c;

    double diff = (Point.getDistance(p, u.p) - radius - u.radius) / 2.0;
    if (diff <= 0.0) {
      // Unit overlapping. Fix positions.
      moveTo(u.p, diff - JahzPlayer.EPSILON);
      u.moveTo(this.p, diff - JahzPlayer.EPSILON);
    }
  }

  // Move the point to an other point for a given distance
  private void moveTo(Point other, double distance) {
    double d = Point.getDistance(this.p, other);

    if (d < JahzPlayer.EPSILON) {
      return;
    }

    double dx = other.x - p.x;
    double dy = other.y - p.y;
    double coef = distance / d;

    this.p.x += dx * coef;
    this.p.y += dy * coef;
  }

  // Bounce with the map border
  void bounce() {
    double mcoeff = 1.0 / mass;
    double nxnysquare = p.x * p.x + p.y * p.y;
    double product = (p.x * speed.x + p.y * speed.y) / (nxnysquare * mcoeff);
    double fx = p.x * product;
    double fy = p.y * product;

    speed.x -= fx * mcoeff;
    speed.y -= fy * mcoeff;

    fx = fx * IMPULSE_COEFF;
    fy = fy * IMPULSE_COEFF;

    // Normalize vector at min or max impulse
    double impulse = Math.sqrt(fx * fx + fy * fy);
    double coeff = 1.0;
    if (impulse > JahzPlayer.EPSILON && impulse < MIN_IMPULSE) {
      coeff = MIN_IMPULSE / impulse;
    }

    fx = fx * coeff;
    fy = fy * coeff;
    speed.x -= fx * mcoeff;
    speed.y -= fy * mcoeff;

    double diff = Point.getDistance(p, JahzPlayer.center) + radius - JahzPlayer.areaRadius;
    if (diff >= 0.0) {
      // Unit still outside of the map, reposition it
      moveTo(JahzPlayer.center, diff + JahzPlayer.EPSILON);
    }
  }

  @Override
  public String toString() {
    return super.toString() + " " + speed + " " + mass + " " + friction;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    long temp;
    temp = Double.doubleToLongBits(friction);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(mass);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + ((speed == null) ? 0 : speed.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    MovingEntity other = (MovingEntity) obj;
    if (Double.doubleToLongBits(friction) != Double.doubleToLongBits(other.friction))
      return false;
    if (Double.doubleToLongBits(mass) != Double.doubleToLongBits(other.mass))
      return false;
    if (speed == null) {
      if (other.speed != null)
        return false;
    } else if (!speed.equals(other.speed))
      return false;
    return true;
  }

}

class Tanker extends MovingEntity {

  private static final double tankerFriction = 0.4;
  public static final double TANKER_MASS_BY_WATER = 0.5;

  public int water;
  public int capacity;

  public Tanker(int id, Point p, int radius, Vector speed, double mass, int water, int capacity) {
    super(EntityType.TANKER, id, p, radius, speed, mass, tankerFriction);
    this.water = water;
    this.capacity = capacity;
  }

  public Tanker(Tanker original) {
    super(EntityType.TANKER, original.id, original.p.copy(), original.radius, original.speed.copy(), original.mass, tankerFriction);
    this.water = original.water;
    this.capacity = original.capacity;
  }

  @Override
  public Tanker copy() {
    return new Tanker(this);
  }

  @Override
  public Collision getCollision() {
    // Tankers can go outside of the map
    return Collision.NULL_COLLISION;
  }

  Wreck die() {
    // Don't spawn a wreck if our center is outside of the map
    if (Point.getDistance(p, JahzPlayer.center) >= JahzPlayer.areaRadius) {
      return null;
    }
    return new Wreck(30, new Point(round(p.x), round(p.y)), radius, water);
  }

  static public int round(double x) {
    int s = x < 0 ? -1 : 1;
    return s * (int) Math.round(s * x);
  }

  @Override
  public String toString() {
    return super.toString() + " " + water + " " + capacity;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + capacity;
    result = prime * result + water;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    Tanker other = (Tanker) obj;
    if (capacity != other.capacity)
      return false;
    if (water != other.water)
      return false;
    return true;
  }

  public boolean isFull() {
    return water == capacity;
  }

}

abstract class Looter extends MovingEntity {

  private static final int looterRadius = 400;

  public int playerId;

  public Looter(EntityType type, int id, Point p, Vector speed, double mass, double friction, int playerId) {
    super(type, id, p, looterRadius, speed, mass, friction);
    this.playerId = playerId;
  }

  @Override
  public String toString() {
    return super.toString() + " " + playerId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + playerId;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    Looter other = (Looter) obj;
    if (playerId != other.playerId)
      return false;
    return true;
  }

}

class Reaper extends Looter {

  private static final double reaperMass = 0.5;
  private static final double reaperFriction = 0.2;

  public Reaper(int id, Point p, Vector speed, int playerId) {
    super(EntityType.REAPER, id, p, speed, reaperMass, reaperFriction, playerId);
  }

  public Reaper(Reaper original) {
    super(EntityType.REAPER, original.id, original.p.copy(), original.speed.copy(), reaperMass, reaperFriction, original.playerId);
  }

  @Override
  public Reaper copy() {
    return new Reaper(this);
  }

}

class Destroyer extends Looter {

  private static final double destroyerMass = 1.5;
  private static final double destroyerFriction = 0.3;

  public Destroyer(int id, Point p, Vector speed, int playerId) {
    super(EntityType.DESTROYER, id, p, speed, destroyerMass, destroyerFriction, playerId);
  }

  public Destroyer(Destroyer original) {
    super(EntityType.DESTROYER, original.id, original.p.copy(), original.speed.copy(), destroyerMass, destroyerFriction, original.playerId);
  }

  @Override
  public Destroyer copy() {
    return new Destroyer(this);
  }

}

class Doof extends Looter {

  private static final double doofMass = 1;
  private static final double doofFriction = 0.25;

  public Doof(int id, Point p, Vector speed, int playerId) {
    super(EntityType.DOOF, id, p, speed, doofMass, doofFriction, playerId);
  }

  public Doof(Doof original) {
    super(EntityType.DOOF, original.id, original.p.copy(), original.speed.copy(), doofMass, doofFriction, original.playerId);
  }

  @Override
  public Doof copy() {
    return new Doof(this);
  }

}

class Wreck extends Entity {

  public int water;

  public Wreck(int id, Point p, int radius, int water) {
    super(EntityType.WRECK, id, p, radius);
    this.water = water;
  }

  public Wreck(Wreck original) {
    super(original);
    this.water = original.water;
  }

  public Wreck copy() {
    return new Wreck(this);
  }

  @Override
  public String toString() {
    return super.toString() + " " + water;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + water;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    Wreck other = (Wreck) obj;
    if (water != other.water)
      return false;
    return true;
  }

}

abstract class TempEntity extends Entity {

  public int remainingDuration;

  public TempEntity(EntityType type, int id, Point p, int radius, int remainingDuration) {
    super(type, id, p, radius);
    this.remainingDuration = remainingDuration;
  }

  public TempEntity(TempEntity original) {
    super(original.type, original.id, original.p.copy(), original.radius);
    this.remainingDuration = original.remainingDuration;
  }

  @Override
  public String toString() {
    return super.toString() + " " + remainingDuration;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + remainingDuration;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    TempEntity other = (TempEntity) obj;
    if (remainingDuration != other.remainingDuration)
      return false;
    return true;
  }

}

class Tar extends TempEntity {

  public Tar(int id, Point p, int radius, int remainingDuration) {
    super(EntityType.TAR, id, p, radius, remainingDuration);
  }

  public Tar(Tar original) {
    super(original);
  }

  @Override
  public Tar copy() {
    return new Tar(this);
  }

}

class Oil extends TempEntity {

  public Oil(int id, Point p, int radius, int remainingDuration) {
    super(EntityType.OIL, id, p, radius, remainingDuration);
  }

  public Oil(Oil original) {
    super(original);
  }

  @Override
  public Oil copy() {
    return new Oil(this);
  }

}

enum GameResult {
  UNKNOWN, // Game not yet finished
  FIRST, // Game finished and won by us :)
  SECOND, // Game finished and we're second
  THIRD, // Game finished and lost :(
}

class GameState extends GameStateObject {

  public int round;
  public GameResult gameResult;

  public int[] score;
  public int[] rage;

  // Looters
  public Reaper[] reapers;
  public Destroyer[] destroyers;
  public Doof[] doofs;

  public List<Tanker> tankers;
  public List<Wreck> wrecks;
  public List<Oil> oils;
  public List<Tar> tars;

  public GameState(int round) {
    this.round = round;
    this.gameResult = GameResult.UNKNOWN;

    this.score = new int[JahzPlayer.nbPlayer];
    this.rage = new int[JahzPlayer.nbPlayer];

    this.reapers = new Reaper[JahzPlayer.nbReaper];
    this.destroyers = new Destroyer[JahzPlayer.nbDestroyer];
    this.doofs = new Doof[JahzPlayer.nbDoof];

    this.tankers = new ArrayList<Tanker>(JahzPlayer.maxTankers);
    this.wrecks = new ArrayList<Wreck>(JahzPlayer.maxWrecks);
    this.oils = new ArrayList<Oil>(JahzPlayer.maxOils);
    this.tars = new ArrayList<Tar>(JahzPlayer.maxTars);

  }

  public GameState(GameState original) {
    this.round = original.round;
    this.gameResult = original.gameResult;

    this.score = original.score.clone();
    this.rage = original.rage.clone();

    this.reapers = new Reaper[JahzPlayer.nbReaper];
    for (int i = 0; i < reapers.length; i++) {
      this.reapers[i] = original.reapers[i].copy();
    }
    this.destroyers = new Destroyer[JahzPlayer.nbDestroyer];
    for (int i = 0; i < destroyers.length; i++) {
      this.destroyers[i] = original.destroyers[i].copy();
    }
    this.doofs = new Doof[JahzPlayer.nbDoof];
    for (int i = 0; i < doofs.length; i++) {
      this.doofs[i] = original.doofs[i].copy();
    }

    this.tankers = new ArrayList<Tanker>(JahzPlayer.maxTankers);
    for (Tanker tanker : original.tankers) {
      this.tankers.add(tanker.copy());
    }
    this.wrecks = new ArrayList<Wreck>(JahzPlayer.maxWrecks);
    for (Wreck wreck : original.wrecks) {
      this.wrecks.add(wreck.copy());
    }
    this.oils = new ArrayList<Oil>(JahzPlayer.maxOils);
    for (Oil oil : original.oils) {
      this.oils.add(oil.copy());
    }
    this.tars = new ArrayList<Tar>(JahzPlayer.maxTars);
    for (Tar tar : original.tars) {
      this.tars.add(tar.copy());
    }
  }

  public GameState copy() {
    return new GameState(this);
  }

  @Override
  public String toString() {
    return "GameState " + round + Print.debugSep + score[0] + Print.debugSep + score[1] + Print.debugSep + score[2] + Print.debugSep + rage[0] + Print.debugSep + rage[1] + Print.debugSep
        + rage[2];
  }

  @Override
  public void print() {
    super.print();
    for (int i = 0; i < JahzPlayer.nbLooter; i++) {
      reapers[i].print();
      destroyers[i].print();
      doofs[i].print();
    }
    for (Tanker tanker : tankers) {
      tanker.print();
    }
    for (Wreck wreck : wrecks) {
      wreck.print();
    }
    for (Oil oil : oils) {
      oil.print();
    }
    for (Tar tar : tars) {
      tar.print();
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(destroyers);
    result = prime * result + Arrays.hashCode(doofs);
    result = prime * result + ((gameResult == null) ? 0 : gameResult.hashCode());
    result = prime * result + ((oils == null) ? 0 : oils.hashCode());
    result = prime * result + Arrays.hashCode(rage);
    result = prime * result + Arrays.hashCode(reapers);
    result = prime * result + round;
    result = prime * result + Arrays.hashCode(score);
    result = prime * result + ((tankers == null) ? 0 : tankers.hashCode());
    result = prime * result + ((tars == null) ? 0 : tars.hashCode());
    result = prime * result + ((wrecks == null) ? 0 : wrecks.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    GameState other = (GameState) obj;
    if (!Arrays.equals(destroyers, other.destroyers))
      return false;
    if (!Arrays.equals(doofs, other.doofs))
      return false;
    if (gameResult != other.gameResult)
      return false;
    if (oils == null) {
      if (other.oils != null)
        return false;
    } else if (!oils.equals(other.oils))
      return false;
    if (!Arrays.equals(rage, other.rage))
      return false;
    if (!Arrays.equals(reapers, other.reapers))
      return false;
    if (round != other.round)
      return false;
    if (!Arrays.equals(score, other.score))
      return false;
    if (tars == null) {
      if (other.tars != null)
        return false;
    } else if (!tars.equals(other.tars))
      return false;
    if (wrecks == null) {
      if (other.wrecks != null)
        return false;
    } else if (!wrecks.equals(other.wrecks))
      return false;
    return true;
  }

}

class Collision {
  double t;
  MovingEntity a;
  MovingEntity b;

  public final static Collision NULL_COLLISION = new Collision(1.0 + JahzPlayer.EPSILON, null, null);

  Collision(double t, MovingEntity a, MovingEntity b) {
    this.t = t;
    this.a = a;
    this.b = b;
  }

  Tanker dead() {
    if (a.type == EntityType.DESTROYER && b.type == EntityType.TANKER && b.mass < JahzPlayer.tarMassIncrease) {
      return (Tanker) b;
    }

    if (b.type == EntityType.DESTROYER && a.type == EntityType.TANKER && a.mass < JahzPlayer.tarMassIncrease) {
      return (Tanker) a;
    }

    return null;
  }
}

class GameEngine {

  public static int nbApplyAction = 0;

  // Applies the provided action on the given GameState, copying it first. Bad perf-wise (extra cost for the copy), but the provided GameState is kept intact.
  public static GameState applyActionWithCopy(GameState gs, Action[] myActions, Action[] opActions1, Action[] opActions2) {
    GameState result = gs.copy();
    applyActionWithoutCopy(result, myActions, opActions1, opActions2);
    return result;
  }

  // Applies the provided action on the given GameState, definitely altering it (for a basic example, gs.round will be incremented for sure)
  public static void applyActionWithoutCopy(GameState gs, Action[] myActions, Action[] opActions1, Action[] opActions2) {

    if (gs.gameResult == GameResult.UNKNOWN) {
      // The game is not yet finished

      // Creation of the new skill effects (Tar pools and Oil pools).
      createTars(gs, myActions, opActions1, opActions2);
      createOils(gs, myActions, opActions1, opActions2);

      // Vehicles in a Tar pool have their mass increased by 10.
      increaseTarEntityMass(gs);

      // Vehicles in a Nitro Grenade zone undergo an equivalent THROTTLE of 1000 away from the center of the zone.
      grenades(gs, myActions, opActions1, opActions2);

      // Tankers apply their acceleration.
      boostTankers(gs);

      // Throttles are applied to players' Looters.
      boostAllLooters(gs, myActions, opActions1, opActions2);

      // Move vehicles and play collisions until the end of the turn.
      play(gs);

      // Check who is in oil at the end of the turn
      updateMovingEntitiesInOil(gs);

      // Full tankers farther than 8000 from Water Town are removed. Others fill if possible
      updateTankers(gs);

      // Spawn new Tankers.
      // NOT needed: enough time to see them coming (> simu depth) --> less code, less cpu time

      // Reapers harvest water from Wrecks. Except if the Reaper is in a Oil pool.
      // Then Empty Wrecks are removed.
      harvest(gs);

      // Friction is applied except to vehicles in an Oil pool.
      // Positions and velocities are rounded.
      finalizeMoves(gs);

      // The Doof warrior plays an epic guitar solo and generates rage.
      playEpicSolo(gs);

      // Restore vehicle masses (mass can be modified by a Tar pool).
      restoreMovingEntitiesMass(gs);

      // Finished skill effects are removed from the game.
      decreaseThenRemoveExpiredTarsAndOils(gs.tars);
      decreaseThenRemoveExpiredTarsAndOils(gs.oils);

      // Finalize GameState
      finalizeGameState(gs);

      nbApplyAction++;
    }

  }

  private static void play(GameState gs) {

    double t = 0;
    List<MovingEntity> units = getAllMovingEntities(gs);
    // Play the round. Stop at each collisions and play it. Reapeat until t > 1.0

    Collision collision = getNextCollision(units, gs);

    while (collision.t + t <= 1.0) {
      double delta = collision.t;
      moveAllVehicules(delta, gs);
      t += collision.t;

      playCollision(collision, units, gs);

      collision = getNextCollision(units, gs);
    }

    // No more collision. Move units until the end of the round
    double delta = 1.0 - t;
    moveAllVehicules(delta, gs);

  }

  private static List<MovingEntity> getAllMovingEntities(GameState gs) {
    List<MovingEntity> result = new ArrayList<>(20);
    result.addAll(Arrays.asList(gs.reapers));
    result.addAll(Arrays.asList(gs.destroyers));
    result.addAll(Arrays.asList(gs.doofs));
    result.addAll(gs.tankers);
    return result;
  }

  // Get the next collision for the current round
  // All units are tested
  private static Collision getNextCollision(List<MovingEntity> units, GameState gs) {
    Collision result = Collision.NULL_COLLISION;

    for (int i = 0; i < units.size(); ++i) {
      MovingEntity unit = units.get(i);

      // Test collision with map border first
      Collision collision = unit.getCollision();

      if (collision.t < result.t) {
        result = collision;
      }

      for (int j = i + 1; j < units.size(); ++j) {
        collision = unit.getCollision(units.get(j));

        if (collision.t < result.t) {
          result = collision;
        }
      }
    }

    return result;
  }

  // Play a collision
  private static void playCollision(Collision collision, List<MovingEntity> units, GameState gs) {
    if (collision.b == null) {
      // Bounce with border
      collision.a.bounce();
    } else {
      Tanker dead = collision.dead();

      if (dead != null) {
        // A destroyer kill a tanker
        gs.tankers.remove(dead);
        units.remove(dead);

        Wreck wreck = dead.die();

        // If a tanker is too far away, there's no wreck
        if (wreck != null) {
          gs.wrecks.add(wreck);
        }
      } else {
        // Bounce between two units
        collision.a.bounce(collision.b);
      }
    }
  }

  private static void moveAllVehicules(double t, GameState gs) {
    for (Reaper reaper : gs.reapers) {
      reaper.move(t);
    }
    for (Destroyer destroyer : gs.destroyers) {
      destroyer.move(t);
    }
    for (Doof doof : gs.doofs) {
      doof.move(t);
    }
    for (Tanker tanker : gs.tankers) {
      tanker.move(t);
    }
  }

  private static void finalizeGameState(GameState gs) {

    if ((gs.round < JahzPlayer.maxRound && (gs.score[0] == JahzPlayer.maxWater || gs.score[1] == JahzPlayer.maxWater || gs.score[2] == JahzPlayer.maxWater)) || gs.round == JahzPlayer.maxRound) {
      // Game is finished ! Rank players

      if (gs.score[0] >= gs.score[1]) {
        if (gs.score[0] >= gs.score[2]) {
          gs.gameResult = GameResult.FIRST;
        } else {
          gs.gameResult = GameResult.SECOND;
        }
      } else {
        if (gs.score[0] >= gs.score[2]) {
          gs.gameResult = GameResult.SECOND;
        } else {
          gs.gameResult = GameResult.THIRD;
        }
      }

    }

    gs.round++;

  }

  private static void decreaseThenRemoveExpiredTarsAndOils(List<? extends TempEntity> tempEntities) {
    Iterator<? extends TempEntity> it = tempEntities.iterator();
    while (it.hasNext()) {
      TempEntity entity = it.next();
      entity.remainingDuration--;
      if (entity.remainingDuration == 0) {
        it.remove();
      }
    }
  }

  private static void restoreMovingEntitiesMass(GameState gs) {
    for (Reaper reaper : gs.reapers) {
      restoreMovingEntityMass(reaper);
    }
    for (Destroyer destroyer : gs.destroyers) {
      restoreMovingEntityMass(destroyer);
    }
    for (Doof doof : gs.doofs) {
      restoreMovingEntityMass(doof);
    }
    for (Tanker tanker : gs.tankers) {
      restoreMovingEntityMass(tanker);
    }

  }

  private static void restoreMovingEntityMass(MovingEntity movingEntity) {
    if (movingEntity.mass >= JahzPlayer.tarMassIncrease) {
      movingEntity.mass = movingEntity.mass % JahzPlayer.tarMassIncrease;
    }
  }

  private static void playEpicSolo(GameState gs) {

    for (int i = 0; i < gs.rage.length; i++) {
      gs.rage[i] = Math.min(JahzPlayer.maxRage, gs.rage[i] + (int) Math.floor(gs.doofs[i].speed.getNorm() * JahzPlayer.DOOF_RAGE_COEF));
    }

  }

  private static void finalizeMoves(GameState gs) {

    for (Reaper reaper : gs.reapers) {
      if (!reaper.isInOil) {
        reaper.applyFriction();
      }
      reaper.round();
    }
    for (Destroyer destroyer : gs.destroyers) {
      if (!destroyer.isInOil) {
        destroyer.applyFriction();
      }
      destroyer.round();
    }
    for (Doof doof : gs.doofs) {
      if (!doof.isInOil) {
        doof.applyFriction();
      }
      doof.round();
    }
    for (Tanker tanker : gs.tankers) {
      if (!tanker.isInOil) {
        tanker.applyFriction();
      }
      tanker.round();
    }

  }

  private static void updateMovingEntitiesInOil(GameState gs) {
    for (Reaper reaper : gs.reapers) {
      reaper.isInOil = isMovingEntityInOil(reaper, gs.oils);
    }
    for (Destroyer destroyer : gs.destroyers) {
      destroyer.isInOil = isMovingEntityInOil(destroyer, gs.oils);
    }
    for (Doof doof : gs.doofs) {
      doof.isInOil = isMovingEntityInOil(doof, gs.oils);
    }
    for (Tanker tanker : gs.tankers) {
      tanker.isInOil = isMovingEntityInOil(tanker, gs.oils);
    }

  }

  private static boolean isMovingEntityInOil(MovingEntity vehicule, List<Oil> oils) {
    boolean result = false;
    for (Oil oil : oils) {
      double d = Point.getDistanceSquare(oil.p, vehicule.p);
      if (d <= (JahzPlayer.skillRadius + vehicule.radius) * (JahzPlayer.skillRadius + vehicule.radius)) {
        result = true;
        break;
      }
    }
    return result;
  }

  private static void harvest(GameState gs) {

    Iterator<Wreck> wrecksIt = gs.wrecks.iterator();

    while (wrecksIt.hasNext()) {

      Wreck wreck = wrecksIt.next();
      for (int i = 0; i < gs.reapers.length; i++) {
        Reaper reaper = gs.reapers[i];
        if (!reaper.isInOil && Point.getDistanceSquare(wreck.p, reaper.p) <= wreck.radius * wreck.radius) {
          gs.score[i]++;
          wreck.water--;
        }
      }

      if (wreck.water <= 0) {
        wrecksIt.remove();
      }

    }

  }

  private static void updateTankers(GameState gs) {

    Iterator<Tanker> tankersIt = gs.tankers.iterator();
    while (tankersIt.hasNext()) {
      Tanker tanker = tankersIt.next();

      double distanceFromCenterSquare = Point.getDistanceSquare(tanker.p, JahzPlayer.center);
      boolean full = tanker.isFull();

      if (distanceFromCenterSquare <= JahzPlayer.waterTownRadiusSquare && !full) {
        // A non full tanker in watertown collect some water
        tanker.water += 1;
        tanker.mass += Tanker.TANKER_MASS_BY_WATER;
      } else if (distanceFromCenterSquare >= (JahzPlayer.tankerMaxDistance + tanker.radius) * (JahzPlayer.tankerMaxDistance + tanker.radius) && full) {
        // Remove too far away and not full tankers from the game
        tankersIt.remove();
      }
    }

  }

  private static void boostAllLooters(GameState gs, Action[] myActions, Action[] opActions1, Action[] opActions2) {
    boostLootersForOnePlayer(gs, myActions, 0);
    boostLootersForOnePlayer(gs, opActions1, 1);
    boostLootersForOnePlayer(gs, opActions2, 2);

  }

  private static void boostLootersForOnePlayer(GameState gs, Action[] actions, int playerId) {
    if (actions[0].actionType == ActionType.THROTTLE) {
      gs.reapers[playerId].boost(actions[0].target, actions[0].throttle);
    }
    if (actions[1].actionType == ActionType.THROTTLE) {
      gs.destroyers[playerId].boost(actions[1].target, actions[1].throttle);
    }
    if (actions[2].actionType == ActionType.THROTTLE) {
      gs.doofs[playerId].boost(actions[2].target, actions[2].throttle);
    }

  }

  private static void boostTankers(GameState gs) {
    for (Tanker tanker : gs.tankers) {
      if (tanker.water >= tanker.capacity) {
        // Move out
        tanker.boost(JahzPlayer.center, -JahzPlayer.tankerThrottle);
      } else if (Point.getDistanceSquare(tanker.p, JahzPlayer.center) > JahzPlayer.waterTownRadiusSquare) {
        // Move toward center
        tanker.boost(JahzPlayer.center, JahzPlayer.tankerThrottle);
      }
    }

  }

  private static void grenades(GameState gs, Action[] myActions, Action[] opActions1, Action[] opActions2) {
    List<Point> grenadePoints = new ArrayList<>(JahzPlayer.maxGrenades);
    addGrenade(gs, grenadePoints, myActions[1], 0);
    addGrenade(gs, grenadePoints, opActions1[1], 1);
    addGrenade(gs, grenadePoints, opActions2[1], 2);

    for (Point grenade : grenadePoints) {
      for (Reaper reaper : gs.reapers) {
        explode(grenade, reaper);
      }
      for (Destroyer destroyer : gs.destroyers) {
        explode(grenade, destroyer);
      }
      for (Doof doof : gs.doofs) {
        explode(grenade, doof);
      }
      for (Tanker tanker : gs.tankers) {
        explode(grenade, tanker);
      }
    }

  }

  private static void explode(Point grenade, MovingEntity movingEntity) {
    double d = Point.getDistanceSquare(grenade, movingEntity.p);
    if (d > 0 && d <= (JahzPlayer.skillRadius + movingEntity.radius) * (JahzPlayer.skillRadius + movingEntity.radius)) {
      movingEntity.boost(grenade, -JahzPlayer.grenadeThrottle);
    }

  }

  private static void addGrenade(GameState gs, List<Point> grenadePoints, Action action, int playerId) {
    if (action.actionType == ActionType.SKILL && Point.getDistanceSquare(gs.destroyers[playerId].p, action.target) <= JahzPlayer.skillRangeSquare && gs.rage[playerId] >= JahzPlayer.destroyerSkillRage) {
      // Add new grenade
      grenadePoints.add(action.target);
      gs.rage[playerId] -= JahzPlayer.destroyerSkillRage;
    }

  }

  private static void increaseTarEntityMass(GameState gs) {
    for (Tar tar : gs.tars) {
      for (Reaper reaper : gs.reapers) {
        increaseMass(tar, reaper);
      }
      for (Destroyer destroyer : gs.destroyers) {
        increaseMass(tar, destroyer);
      }
      for (Doof doof : gs.doofs) {
        increaseMass(tar, doof);
      }
      for (Tanker tanker : gs.tankers) {
        increaseMass(tar, tanker);
      }
    }

  }

  private static void increaseMass(Tar tar, MovingEntity movingEntity) {
    double d = Point.getDistanceSquare(tar.p, movingEntity.p);
    if (d > 0 && d <= (tar.radius + movingEntity.radius) * (tar.radius + movingEntity.radius)) {
      movingEntity.mass += JahzPlayer.tarMassIncrease;
    }
  }

  private static void createTars(GameState gs, Action[] myActions, Action[] opActions1, Action[] opActions2) {
    createTar(gs, myActions[0], 0);
    createTar(gs, opActions1[0], 1);
    createTar(gs, opActions2[0], 2);

  }

  private static void createTar(GameState gs, Action action, int playerId) {
    if (action.actionType == ActionType.SKILL && Point.getDistanceSquare(gs.reapers[playerId].p, action.target) <= JahzPlayer.skillRangeSquare && gs.rage[playerId] >= JahzPlayer.reaperSkillRage) {
      // Create new Tar
      gs.tars.add(new Tar(30, action.target, JahzPlayer.skillRadius, JahzPlayer.skillDuration));
      gs.rage[playerId] -= JahzPlayer.reaperSkillRage;
    }

  }

  private static void createOils(GameState gs, Action[] myActions, Action[] opActions1, Action[] opActions2) {
    createOil(gs, myActions[2], 0);
    createOil(gs, opActions1[2], 1);
    createOil(gs, opActions2[2], 2);

  }

  private static void createOil(GameState gs, Action action, int playerId) {
    if (action.actionType == ActionType.SKILL && Point.getDistanceSquare(gs.doofs[playerId].p, action.target) <= JahzPlayer.skillRangeSquare && gs.rage[playerId] >= JahzPlayer.doofSkillRage) {
      // Create new Oil
      gs.oils.add(new Oil(40, action.target, JahzPlayer.skillRadius, JahzPlayer.skillDuration));
      gs.rage[playerId] -= JahzPlayer.doofSkillRage;
    }

  }

}

abstract class AI {

  // Will compute an Action from a provided GameState which is going to stay INTACT during the computation
  // This is the default entry point for an AI
  public Action[] computeIntact(GameState gs) {

    // One way by default to make sure that the provided GameState stays INTACT is to work on a copy
    // Some AI would not even need the copy, then they can override the method
    GameState gsCopy = gs.copy();
    return compute(gsCopy);
  }

  // Compute the action on the provided gs, *potentially* altering it during the computation. At least it doesn't make an upfront copy.
  // Should only be used when:
  // 1) we need perf so we don't want to pay the copy() price or copy() is not implemented, and
  // 2) we don't care about the gs being altered OR we know it's not going to be altered by a specific implementation
  public abstract Action[] compute(GameState gs);

  public void printAIParameters() {
    // Nothing to print by default, can be overriden if needed, when the AI relies on other things than just the turn's input gamestate...
  }

  public void printAI() {
    Print.debug("Using base AI: " + this.getClass().getName());
  }
}

class WaitAI extends AI {

  private static final Action[] waitActions = new Action[] { Action.getWaitAction(), Action.getWaitAction(), Action.getWaitAction() };

  @Override
  public Action[] computeIntact(GameState gs) {
    return waitActions;
  }

  @Override
  public Action[] compute(GameState gs) {
    return waitActions;
  }

}

class NearestWreckAI extends AI {

  @Override
  public Action[] computeIntact(GameState gs) {
    return compute(gs);
  }

  @Override
  public Action[] compute(GameState gs) {

    Action[] result = new Action[JahzPlayer.nbLooter];

    Reaper myReaper = null;
    for (Reaper reaper : gs.reapers) {
      if (reaper.playerId == JahzPlayer.myId) {
        myReaper = reaper;
        break;
      }
    }

    double minDistance = Double.MAX_VALUE;
    Wreck nearestWreck = null;

    for (Wreck wreck : gs.wrecks) {
      double distance = Point.getDistanceSquare(wreck.p, myReaper.p);
      if (distance < minDistance) {
        minDistance = distance;
        nearestWreck = wreck;
      }
    }

    if (nearestWreck != null) {
      result[0] = Action.getThrottleAction(new Point(nearestWreck.p.x - myReaper.speed.x, nearestWreck.p.y - myReaper.speed.y), JahzPlayer.maxThrottle, "Go go go");
    } else {
      result[0] = Action.getThrottleAction(JahzPlayer.center, JahzPlayer.maxThrottle, "Center");
    }

    result[1] = Action.getWaitAction();
    result[2] = Action.getWaitAction();

    return result;
  }

}

class NearestWreckAIWithRandomRage extends AI {

  @Override
  public Action[] compute(GameState gs) {

    Action[] result = JahzPlayer.nearestWreckAI.computeIntact(gs);
    Random r = new Random();

    if (gs.rage[JahzPlayer.myId] >= JahzPlayer.destroyerSkillRage) {
      // Random skill
      switch (r.nextInt(3)) {
      case 0:
        result[0] = Action.getSkillAction(
            new Point(gs.reapers[0].p.x + r.nextInt(JahzPlayer.skillRadius) * 2 - JahzPlayer.skillRadius, gs.reapers[0].p.y + r.nextInt(JahzPlayer.skillRadius) * 2 - JahzPlayer.skillRadius), "Tar");
        result[2] = Action.getThrottleAction(new Point(r.nextInt(JahzPlayer.areaRadius) * 2 - JahzPlayer.areaRadius, r.nextInt(JahzPlayer.areaRadius) * 2 - JahzPlayer.areaRadius), JahzPlayer.maxThrottle, "Fast");
        break;
      case 1:
        result[1] = Action.getSkillAction(
            new Point(gs.destroyers[0].p.x + r.nextInt(JahzPlayer.skillRadius) * 2 - JahzPlayer.skillRadius, gs.destroyers[0].p.y + r.nextInt(JahzPlayer.skillRadius) * 2 - JahzPlayer.skillRadius),
            "Grenade");
        result[2] = Action.getThrottleAction(new Point(r.nextInt(JahzPlayer.areaRadius) * 2 - JahzPlayer.areaRadius, r.nextInt(JahzPlayer.areaRadius) * 2 - JahzPlayer.areaRadius), JahzPlayer.maxThrottle, "Fast");
        break;
      case 2:
        result[2] = Action.getSkillAction(
            new Point(gs.doofs[0].p.x + r.nextInt(JahzPlayer.skillRadius) * 2 - JahzPlayer.skillRadius, gs.doofs[0].p.y + r.nextInt(JahzPlayer.skillRadius) * 2 - JahzPlayer.skillRadius), "Oil");
        break;
      default:
        break;
      }
    } else {
      result[2] = Action.getThrottleAction(new Point(r.nextInt(JahzPlayer.areaRadius) * 2 - JahzPlayer.areaRadius, r.nextInt(JahzPlayer.areaRadius) * 2 - JahzPlayer.areaRadius), JahzPlayer.maxThrottle, "Fast");
    }

    return result;
  }

}

class NearestWreckAIWithFixedRage extends AI {

  public int playerId;

  public NearestWreckAIWithFixedRage(int playerId) {
    super();
    this.playerId = playerId;
  }

  @Override
  public Action[] compute(GameState gs) {

    Action[] result = JahzPlayer.nearestWreckAI.computeIntact(gs);

    if (gs.rage[playerId] >= JahzPlayer.reaperSkillRage) {

      if (gs.round < 100 && gs.rage[playerId] >= JahzPlayer.destroyerSkillRage) {
        // Grenade
        MovingEntity target = getClosestOpMovingEntity(gs.destroyers[playerId], gs);
        result[1] = Action.getSkillAction(new Point(target.p.x + target.speed.x, target.p.y + target.speed.y), "Grenade");
        result[2] = Action.getThrottleAction(JahzPlayer.center, JahzPlayer.maxThrottle, "Fast");
      } else if (gs.round < 150 && gs.round > 100) {
        // Tar
        MovingEntity target = getClosestOpMovingEntity(gs.reapers[playerId], gs);
        result[0] = Action.getSkillAction(target.p, "Tar");
        result[2] = Action.getThrottleAction(JahzPlayer.center, JahzPlayer.maxThrottle, "Fast");

      } else if (gs.round > 150) {
        // Oil
        MovingEntity target = getClosestOpMovingEntity(gs.doofs[playerId], gs);
        result[2] = Action.getSkillAction(target.p, "Oil");

      }
    }

    if (result[2].equals(Action.getWaitAction())) {
      result[2] = Action.getThrottleAction(JahzPlayer.center, JahzPlayer.maxThrottle, "Fast");
    }

    return result;
  }

  private MovingEntity getClosestOpMovingEntity(Looter looter, GameState gs) {
    MovingEntity result = null;
    double minDistance = Double.MAX_VALUE;

    for (int i = 0; i < 3; i++) {
      if (i != playerId) {
        double d = Point.getDistance(looter.p, gs.reapers[i].p);
        if (d < minDistance) {
          result = gs.reapers[i];
          minDistance = d;
        }
        d = Point.getDistance(looter.p, gs.destroyers[i].p);
        if (d < minDistance) {
          result = gs.destroyers[i];
          minDistance = d;
        }
        d = Point.getDistance(looter.p, gs.doofs[i].p);
        if (d < minDistance) {
          result = gs.doofs[i];
          minDistance = d;
        }
      }
    }

    for (Tanker tanker : gs.tankers) {
      double d = Point.getDistance(looter.p, tanker.p);
      if (d < minDistance) {
        result = tanker;
        minDistance = d;
      }
    }

    return result;
  }
}

abstract class SolutionEvaluation {

  protected double maxScore;
  protected double minScore;
  protected double patience;

  public SolutionEvaluation(double maxScore, double patience) {
    super();
    this.maxScore = maxScore;
    this.minScore = -maxScore;
    this.patience = patience;
  }

  // The score of a given GameState
  public double getGameStateScore(GameState gs) {

    double score = 0;

    switch (gs.gameResult) {
    case FIRST:
      score = maxScore;
      break;
    case THIRD:
      score = -maxScore;
      break;
    case SECOND:
      score = 0;
      break;
    case UNKNOWN:
      score = getUnknownGameStateScore(gs);
      break;
    default:
      break;
    }

    return score;
  }

  // The part to be implemented
  protected abstract double getUnknownGameStateScore(GameState gs);

  // The final evaluation of a full solution
  public void computeFinalScore(Solution solution) {

    for (int i = 0; i < solution.scores.length; i++) {
      solution.finalScore += solution.scores[i] * Math.pow(patience, i);
    }
  }

}

class ScoreAndNearestWreckSolutionEvaluation extends SolutionEvaluation {

  private static final double maxScore = Double.MAX_VALUE;
  private static final double patience = 0.8;

  public ScoreAndNearestWreckSolutionEvaluation() {
    super(maxScore, patience);
  }

  @Override
  protected double getUnknownGameStateScore(GameState gs) {

    double result = 0;

    int opId = -1;

    if (gs.score[2] > gs.score[1]) {
      if (gs.score[0] > gs.score[1]) {
        opId = 2;
      } else {
        opId = 1;
      }
    } else {
      if (gs.score[0] > gs.score[2]) {
        opId = 1;
      } else {
        opId = 2;
      }
    }

    result += 100000000 * (gs.score[0] - gs.score[opId]);
    result -= 1 * (getNearestWreckDistance(0, gs) - getNearestWreckDistance(opId, gs));
    // result -= (getDoofDistanceFromOpReaper(0, opId, gs) - getDoofDistanceFromOpReaper(opId, 0, gs));
    // result -= (getDestroyerDistanceFromReaper(0, gs) - getDestroyerDistanceFromReaper(opId, gs));
    // result += (gs.rage[0] - gs.rage[opId]);

    return result;
  }

  private int getNearestWreckDistance(int playerId, GameState gs) {
    double minDistance = Double.MAX_VALUE;

    for (Wreck wreck : gs.wrecks) {
      double distance = Point.getDistanceSquare(wreck.p, gs.reapers[playerId].p);
      if (distance < minDistance) {
        minDistance = distance;
      }
    }
    return (int) minDistance;
  }

  private int getDestroyerDistanceFromReaper(int playerId, GameState gs) {
    return (int) Point.getDistanceSquare(gs.destroyers[playerId].p, gs.reapers[playerId].p);
  }

  private int getDoofDistanceFromOpReaper(int doofPlayerId, int reaperPlayerId, GameState gs) {
    return (int) Point.getDistanceSquare(gs.doofs[doofPlayerId].p, gs.reapers[reaperPlayerId].p);
  }

}

class Solution {

  public Action[][] actions;
  public double[] scores;
  public double finalScore;

  public Solution(int depth) {
    actions = new Action[depth][JahzPlayer.nbLooter];
    scores = new double[depth];
  }

}

class MonteCarloAI extends AI {

  private SolutionEvaluation eval;
  private AI opAI;
  private int depth;
  private Random r;

  private static final int nbAngleCut = 36;
  private static int[][] deltas = new int[nbAngleCut][2];

  public MonteCarloAI(SolutionEvaluation eval, AI opAI, int depth) {
    super();
    this.eval = eval;
    this.opAI = opAI;
    this.depth = depth;
    this.r = new Random();
    initDeltas();
  }

  private void initDeltas() {
    for (int i = 0; i < deltas.length; i++) {
      deltas[i] = new int[2];
      deltas[i][0] = (int) (1000 * Math.cos(Math.PI * 2 * i / (double) nbAngleCut));
      deltas[i][1] = (int) (1000 * Math.sin(Math.PI * 2 * i / (double) nbAngleCut));
    }
  }

  @Override
  public Action[] compute(GameState gs) {

    GameEngine.nbApplyAction = 0;
    int nbFullIterations = 0;

    Solution currentSolution;
    Solution bestSolution = null;

    double bestScore = eval.minScore;

    if (JahzPlayer.isDebugOn) {
      Time.debugDuration("Before AI");
    }

    boolean isFirstRound = (gs.round == 1);

    int opId = -1;

    if (gs.score[2] > gs.score[1]) {
      if (gs.score[0] > gs.score[1]) {
        opId = 2;
      } else {
        opId = 1;
      }
    } else {
      if (gs.score[0] > gs.score[2]) {
        opId = 1;
      } else {
        opId = 2;
      }
    }

    while (Time.isTimeLeft(isFirstRound)) {

      currentSolution = buildAndEvaluateRandomSolution(opId, gs, opAI, eval);

      if (currentSolution.finalScore > bestScore) {
        bestScore = currentSolution.finalScore;
        bestSolution = currentSolution;
      }

      nbFullIterations++;
    }

    if (bestSolution == null) {
      bestSolution = new Solution(depth);
      bestSolution.actions[0] = JahzPlayer.nearestWreckAI.computeIntact(gs);
    }

    if (JahzPlayer.isDebugOn) {
      String bestActionsString = "";
      for (int i = 0; i < bestSolution.actions.length; i++) {
        bestActionsString += "R" + i + " " + bestSolution.actions[i][0] + " " + bestSolution.actions[i][1] + " " + bestSolution.actions[i][2];
      }

      Print.debug("End PureRandomWithScoreAI with " + nbFullIterations + " full iterations, " + GameEngine.nbApplyAction + " iterations and score: " + bestScore);
      Print.debug(bestActionsString);
      Time.debugDuration("After AI");
    }

    // Update the action message, can be interesting for arena battles
    bestSolution.actions[0][0].message = "Nb: " + String.format("%5d", GameEngine.nbApplyAction);
    bestSolution.actions[0][1].message = "Nbf: " + String.format("%5d", nbFullIterations);
    bestSolution.actions[0][2].message = "Bs: " + String.format("%5d", (int) bestScore);

    // And finally returns the first step of the best solution found in the allocated time
    return bestSolution.actions[0];
  }

  private Solution buildAndEvaluateRandomSolution(int opId, GameState gs, AI opAI, SolutionEvaluation eval) {

    Solution solution = new Solution(depth);

    // Important to copy, so that we don't alter the starting gamestate for the next iterations...
    GameState gsCopy = gs.copy();

    for (int i = 0; i < depth; i++) {

      solution.actions[i] = getRandomAction(opId, gsCopy);

      // No need to copy here, since we don't care at all about the intermediate gamestates
      GameEngine.applyActionWithoutCopy(gsCopy, solution.actions[i], opAI.computeIntact(gsCopy), opAI.computeIntact(gsCopy));

      // Stores the score of this new gamestate
      solution.scores[i] = eval.getGameStateScore(gsCopy);
    }

    eval.computeFinalScore(solution);

    return solution;
  }

  private Action[] getRandomAction(int opId, GameState gs) {
    Action[] result = new Action[JahzPlayer.nbLooter];

    boolean maxSpeed = true;
    // boolean maxSpeed = r.nextBoolean();

    result[0] = Action.getThrottleAction(getTargetFromPosition(gs.reapers[0]), maxSpeed ? JahzPlayer.maxThrottle : (int) (JahzPlayer.maxThrottle * (1 - r.nextDouble() / 2.0)), null);
    result[1] = Action.getThrottleAction(getTargetFromPosition(gs.destroyers[0]), maxSpeed ? JahzPlayer.maxThrottle : (int) (JahzPlayer.maxThrottle * (1 - r.nextDouble() / 2.0)), null);

    if (gs.rage[0] >= JahzPlayer.doofSkillRage && r.nextBoolean() && Point.getDistanceSquare(gs.doofs[0].p, addVectorToPoint(gs.reapers[opId].p, gs.reapers[opId].speed)) < JahzPlayer.skillRangeSquare) {
      result[2] = Action.getSkillAction(addVectorToPoint(gs.reapers[opId].p, gs.reapers[opId].speed), null);
    } else {
      result[2] = Action.getThrottleAction(addVectorToPoint(gs.reapers[opId].p, gs.reapers[opId].speed), JahzPlayer.maxThrottle, null);
    }

    return result;
  }

  private Point addVectorToPoint(Point p, Vector v) {
    return new Point(p.x + v.x, p.y + v.y);
  }

  private Point getTargetFromPosition(Looter looter) {
    int rand = r.nextInt(nbAngleCut);
    return new Point(looter.p.x + deltas[rand][0], looter.p.y + deltas[rand][1]);
  }

}
