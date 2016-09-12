import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {
  Scanner in;

  class Game {
    Point opponentPosition;
    boolean allCheckpointsDiscovered = false;
    List<Point> checkpoints = new ArrayList<Point>();
    
    void updateCheckPoints(Point checkPoint) {
      if (allCheckpointsDiscovered) {
        return;
      }
      if (!isKnownCheckpoint(checkPoint)) {
        checkpoints.add(checkPoint);
      } else {
        if (checkpoints.size() > 1 && checkPoint.equals(checkpoints.get(0))) {
          allCheckpointsDiscovered = true;
        }
      }
    }

    boolean isKnownCheckpoint(Point p) {
      for (Point checkpoint : checkpoints) {
        if (checkpoint.equals(p)) {
          return true;
        }
      }
      return false;
    }
  }
  class Pod {
    Point lastPosition = new Point(0,0);
    Point position = lastPosition;
    Vector speed = new Vector(0,0);

    Point nextCheckpoint;
    int nextCheckpointDist; // distance to the next checkpoint
    int nextCheckpointAngle; // angle between your pod orientation and the direction of the next checkpoint
    int boostLeft = 0;
    
    String getOutputCommand(Game game) {
      String result = "";
      if (shouldBoost()) {
        result = "BOOST";
        pod.boostLeft--;
      } else if (shouldFullBreak()) {
        result = "0";
      } else {
        result = calculateThrust();
      }
      return (int)(pod.nextCheckpoint.x) + " " + (int)(pod.nextCheckpoint.y) + " " + result;
    }
    
    private boolean shouldBoost() {
      return boostLeft > 0
          && (nextCheckpointDist > 4000 && Math.abs(nextCheckpointAngle) < 10);
    }

    private boolean shouldFullBreak() {
      return (nextCheckpointAngle > 120 || nextCheckpointAngle < -120);
    }

    private String calculateThrust() {
      int thrust = 100;
      int slowdownDistance = 1000;
      if (nextCheckpointDist - slowdownDistance < 0) {
        double ratio = (1.0 * (slowdownDistance - nextCheckpointDist)) / slowdownDistance;
        ratio = Math.pow(ratio, 1);
        thrust = 75 + (int) (25 * ratio);
      }
      return "" + thrust;
    }

    public String doPrediction() {
      Point nextPosition = position.add(speed);
      Vector nextSpeed = speed.dot(0.85);
      return "P: "+nextPosition.toString()+",S: "+nextSpeed.toString();
    }
  }

  Game game = new Game();
  Pod pod = new Pod();
  
  public static void main(String args[]) {
    new Player().play();
  }

  boolean firstGameInput = true;
  void readGameInput() {
    pod.lastPosition = pod.position;
    
    pod.position = new Point(in.nextInt(), in.nextInt());
    pod.nextCheckpoint = new Point(in.nextInt(), in.nextInt());
    pod.nextCheckpointDist = in.nextInt(); // distance to the next checkpoint
    pod.nextCheckpointAngle = in.nextInt(); // angle between your pod orientation
    game.opponentPosition = new Point(in.nextInt(), in.nextInt());

    if (firstGameInput) {
      pod.lastPosition = pod.position;
      firstGameInput = false;
    } else {
      pod.speed = pod.position.sub(pod.lastPosition);
    }

    game.updateCheckPoints(pod.nextCheckpoint);
  }

  void play() {
    in = new Scanner(System.in);

    // game loop
    while (true) {
      readGameInput();
      String output = pod.getOutputCommand(game);
      
      System.err.println("prediction: "+pod.doPrediction());
      //System.err.println("currentSpeed : "+pod.speed.length()+" "+pod.speed.toString());
      System.out.println(output);
    }
  }
  /**
   *  V 1.0
   */
  static class Point {
    double x,y;

    public Point(double x, double y) {
      super();
      this.x = x;
      this.y = y;
    }
    @Override
    public String toString() {
      return "P("+x+","+y+")";
    }
    
    Vector sub(Point p2) {
      return new Vector(x-p2.x, y-p2.y);
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
    double vx, vy;
    public Vector(double vx, double vy) {
      this.vx = vx;
      this.vy = vy;
    }
    @Override
    public String toString() {
      return "V("+vx+","+vy+")";
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
    private static final double PRECISION = 0.001;
    Point center;
    double radius;
    
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