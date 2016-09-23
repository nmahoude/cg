import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Player {
  Scanner in;

  class Game {
    Pod pods[] = new Pod[4];
    List<Point> checkpoints = new ArrayList<Point>();
    public int laps;
    
    public void init() {
      game.laps = in.nextInt();
      int checkPointCount = in.nextInt();
      for (int i=0;i<checkPointCount;i++) {
        int checkPointX= in.nextInt();
        int checkPointY= in.nextInt();
        game.addCheckPoint(new Point(checkPointX, checkPointY));
      }
      for (int i=0;i<4;i++) {
        pods[i] = new Pod();
        pods[i].index = i;
      }
    }
    
    public void addCheckPoint(Point point) {
      checkpoints.add(point);
    }

    public void update() {
      // TODO Auto-generated method stub
      
    }
  }
  class Pod {
    public int index;
    static final int SLOWDOWN_DISTANCE= 2000;
    Solution lastBestSolution;
    Point lastPosition = new Point(0,0);
    Point position = lastPosition;
    Vector speed = new Vector(0,0);
    Vector direction;
    
    Point nextCheckpoint;
    int nextCheckpointDist; // distance to the next checkpoint
    int nextCheckpointAngle; // angle between your pod orientation and the direction of the next checkpoint
    int boostLeft = 1;
    
    public void debug() {
      Point nextPosition = position.add(speed);
      Vector nextSpeed = speed.dot(0.85);

      System.err.println("direction : "+direction.toString());
      System.err.println("P: "+nextPosition.toString()+",S: "+nextSpeed.toString());
      System.err.println("distance to Target : "+nextCheckpointDist);
    }

    boolean firstGameInput = true;
    public void update(Point pos,Vector speed, Point ckPoint, int angle) {
      lastPosition = position;
      
      position = pos;
      this.speed = speed;
      nextCheckpoint = ckPoint;
      nextCheckpointAngle = angle; // angle between your pod orientation

      if (firstGameInput) {
        lastPosition = position;
        firstGameInput = false;
      } else {
      }
      direction = nextCheckpoint.sub(position).normalize();
    }
  }

  Game game = new Game();
  
  
  public static void main(String args[]) {
    new Player().play();
  }

  void readGameInput() {
    for (int i=0;i<4;i++) {
      Point position = new Point(in.nextInt(), in.nextInt());
      Vector speed = new Vector(in.nextInt(), in.nextInt());
      int angle = in.nextInt();
      Point nextCheckPoint = game.checkpoints.get(in.nextInt());
      
      game.pods[i].update(position,speed, nextCheckPoint,angle);
    }
    game.update();
  }

  void play() {
    in = new Scanner(System.in);
    game.init();
    
    // game loop
    boolean debugBestSolution = false;
    while (true) {
      readGameInput();
      
      for (int podIndex=0;podIndex<2;podIndex++) {
        Pod currentPod = game.pods[podIndex];
        Solution bestSolution = simulate(currentPod, currentPod.nextCheckpoint);
        if (debugBestSolution) {
          System.err.print("Best solution: ");
          bestSolution.debug(currentPod);
        }
        Point target = currentPod.position.add(
            bestSolution.predictedDir.dot(1000)
            );
        String output;
        if (bestSolution instanceof BoostSolution) {
          output = ""+(int)(target.x)+" "+(int)(target.y)+" BOOST";
        } else {
          output = ""+(int)(target.x)+" "+(int)(target.y)+" "+(int)(bestSolution.thrust);
        }
        System.out.println(output);
        currentPod.lastBestSolution = bestSolution;
      
        // **** DEBUG messages
        // String output = pod.getOutputCommand(game);
        //pod.debug();
        if (debugBestSolution && currentPod.lastBestSolution != null) {
          System.err.println("Simulation error:");
          System.err.println(currentPod.lastBestSolution.predictedPosition);
          System.err.println(currentPod.position);
          System.err.println("error :"+currentPod.position.distTo(currentPod.lastBestSolution.predictedPosition));
        }
      }
    }
  }

  static Solution simulate(Pod pod, Point target) {
    if (target == null) {
      return new TurnSolution(100, 0, 
            pod.position, pod.direction, pod.speed);
    }
    List<Solution> solutions = new ArrayList<>();
    double thrusts[] = { 0.0, 50.0, 100.0};
    double angles[] = {-Math.toRadians(18), -Math.toRadians(9), 0, 
        Math.toRadians(9), Math.toRadians(18)};

    for (double angle : angles) {
      for (double thrust : thrusts) {
        Solution solution = new TurnSolution(thrust, angle, 
            pod.position, pod.direction, pod.speed);
        solutions.add(solution);
      }
      // consider boost only if some left
      if (pod.boostLeft > 0) {
        solutions.add(new BoostSolution(650, angle, 
            pod.position, pod.direction, pod.speed));
      }
    }
    // fitnesse value is only distance ATM (TODO)
    double minDist = Integer.MAX_VALUE;
    Solution bestSolution = null;
    for (Solution solution : solutions) {
      double distance = target.distTo(solution.predictedPosition);
      if ( distance < minDist) {
        minDist = distance;
        bestSolution  = solution;
      }
    }
    // force to slowdone near CP(TODO should be handle by fitnesse method ?)
    if (minDist < 2000 && bestSolution.thrust < 100) {
      bestSolution.thrust *=0.85;
    }
    return bestSolution;
  }
  static abstract class Solution {
    double steering; // from -18 to 18
    double thrust; // 0 to 100
    Point predictedPosition;
    Vector predictedSpeed;
    Vector predictedDir;
    
    public Solution(double thrust, double angle, Point position, Vector direction, Vector speed) {
      this.steering = angle;
      this.thrust = thrust;
      predictedDir = direction.rotate(angle);
      predictedSpeed = speed.dot(0.85).add(predictedDir.dot(thrust));
      predictedPosition = position.add(predictedSpeed);
    }

    public void debug(Pod pod) {
      System.err.println("steering="+steering+", thrust="+thrust);
      System.err.println("preSpeed:"+predictedSpeed+", preDir:"+predictedDir);
      System.err.println("prePos:"+predictedPosition+", distToTarget:"+pod.nextCheckpoint.distTo(predictedPosition));
    }

    public abstract String output();
  }
  static class TurnSolution extends Solution {
 
    public TurnSolution(double thrust, double angle, Point position, Vector direction, Vector speed) {
      super(thrust, angle, position, direction, speed);
    }
    @Override
    public String output() {
      return null;
    }
  }
  static class BoostSolution extends Solution {

    public BoostSolution(double thrust, double angle, Point position, Vector direction, Vector speed) {
      super(thrust, angle, position, direction, speed);
    }

    @Override
    public String output() {
      return null;
    }
    
  }
  
  
  
  /**
   *  Trigonometry _ V 1.0
   */
  static final double PRECISION = 0.001;
  static class Point {
    final double x,y;

    public Point(double x, double y) {
      super();
      this.x = x;
      this.y = y;
    }
    public Vector minus(Point pos) {
      return new Vector(pos.x-x, pos.y-y);
    }
    @Override
    public String toString() {
      return "P("+x+","+y+")";
    }
    
    Point add(Point addedPoint) {
      return new Point(x+addedPoint.x, y+addedPoint.y);
    }
    Point add(Vector vec) {
      return new Point(x+vec.vx, y+vec.vy);
    }
    double distTo(Point p) {
      return Math.sqrt( (p.x-x)*(p.x-x) + (p.y-y)*(p.y-y) );
    }

    double distTo(Point p, Vector v) {
      Point p2 = p.add(v);
      return distTo(p, p2);
    }

    double distTo(Point p1, Point p2) {
      return Math.abs( (p2.y-p1.y)*x - (p2.x-p1.x)*y + p2.x*p1.y - p2.y*p1.x) / 
        Math.sqrt((p2.y-p1.y)*(p2.y-p1.y) + (p2.x-p1.x)*(p2.x-p1.x));
    }
    Vector sub(Point p2) {
      return new Vector(x-p2.x, y-p2.y);
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
      if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
        return false;
      if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
        return false;
      return true;
    }
    
  }
  
  static class Vector {
    final double vx, vy;
    public Vector(double vx, double vy) {
      this.vx = vx;
      this.vy = vy;
    }
    @Override
    public String toString() {
      return "V("+vx+","+vy+")";
    }
    Vector normalize() {
      return new Vector(vx / length(), vy / length());
    }
    Vector rotate(double angle) {
      return new Vector(vx*Math.cos(angle) - vy*Math.sin(angle),
          vx*Math.sin(angle) + vy*Math.cos(angle));
    }
    Vector add(Vector v) {
      return new Vector(vx+v.vx, vy+v.vy);
    }
    Vector dot(double d) {
      return new Vector(d*vx, d*vy);
    }
    double dot(Vector v) {
      return vx*v.vx + vy*v.vy;
    }
    double length() {
      return Math.sqrt(vx*vx + vy*vy);
    }
    double angle(Vector v) {
      return Math.acos(this.dot(v) / (this.length() * v.length()));
    }
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(vx);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(vy);
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
      if (Double.doubleToLongBits(vx) != Double.doubleToLongBits(other.vx))
        return false;
      if (Double.doubleToLongBits(vy) != Double.doubleToLongBits(other.vy))
        return false;
      return true;
    }
  }
  
  static class Circle {
    final Point center;
    final double radius;
    
    Circle(Point center, double radius) {
      this.center = center;
      this.radius = radius;
    }
    
    boolean isIn(Point p) {
      return center.distTo(p) <= radius;
    }
    boolean isOn(Point p) {
      return Math.abs(center.distTo(p) - radius) < PRECISION;
    }
  }

  static class Engine {
    Vector getNewSpeed(Trajectory trajectory) {
      return trajectory.speed;
    }
    Point getNewPosition(Trajectory trajectory) {
      return trajectory.position.add(trajectory.speed);
    }
  }

  static class FrictionEngine extends Engine {
    private double frictionCoeff;
    public FrictionEngine(double frictionCoeff) {
      this.frictionCoeff = frictionCoeff;
    }
    @Override
    Vector getNewSpeed(Trajectory trajectory) {
      return trajectory.speed.dot(frictionCoeff);
    }
  }
  static class Trajectory {
    Vector speed;
    Point position;
    Engine engine = new Engine();
    
    public Trajectory(Point position, Vector speed) {
      this.position = position;
      this.speed = speed;
    }
    void  simulate() {
      simulate(1);
    }
    void simulate(int step) {
      this.speed = engine.getNewSpeed(this);
      this.position = engine.getNewPosition(this);
    }
  }
}