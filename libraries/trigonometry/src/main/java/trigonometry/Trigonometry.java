package trigonometry;


public class Trigonometry {
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
