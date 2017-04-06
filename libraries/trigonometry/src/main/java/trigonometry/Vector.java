package trigonometry;

public class Vector {

  public static final Vector ZERO = new Vector(0,0);
  
  public final double vx, vy;
  public Vector(double vx, double vy) {
    this.vx = vx;
    this.vy = vy;
  }
  @Override
  public String toString() {
    return "V("+vx+","+vy+")";
  }
  public Vector normalize() {
    return new Vector(vx / length(), vy / length());
  }
  /**
   * @param angle in radians
   * @return
   */
  public Vector rotate(double angle) {
    return new Vector(vx*Math.cos(angle) - vy*Math.sin(angle),
        vx*Math.sin(angle) + vy*Math.cos(angle));
  }
  public Vector add(Vector v) {
    return new Vector(vx+v.vx, vy+v.vy);
  }
  public Vector dot(double d) {
    return new Vector(d*vx, d*vy);
  }
  public double dot(Vector v) {
    return vx*v.vx + vy*v.vy;
  }

  public double squareLength() {
    return vx*vx + vy*vy;
  }
  public double length() {
    return Math.sqrt(vx*vx + vy*vy);
  }
  public double angle(Vector v) {
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
  public Vector sub(Vector v) {
    return new Vector(vx-v.vx, vy-v.vy);
  }

  static Point[] getInertialPointsIntersection(Vector currentSpeed, Vector desiredDirection, double maxForce) {
    double sx = currentSpeed.vx;
    double sy = currentSpeed.vy;
    double dx = desiredDirection.vx;
    double dy = desiredDirection.vy;
    
    double constant = (sx*dy-sy*dx) / dx; 
    
    double a = (dy/dx)*(dy/dx);
    double b = 2*constant*dy/dx+1;
    double c = constant*constant - maxForce*maxForce;
    
    double results[] = MathUtil.resolve2ndDegree(a, b, c);
    if (results == null) {
      return null;
    } else if (results.length == 1) {
      double vx = results[0];
      double vy = Math.sqrt(maxForce*maxForce - vx*vx);
      return new Point[]{  new Point(vx, vy) };
    } else {
      double vx1 = results[0];
      double vy1 = Math.sqrt(maxForce*maxForce - vx1*vx1);
      double vx2 = results[1];
      double vy2 = Math.sqrt(maxForce*maxForce - vx2*vx2);
      return new Point[]{  new Point(vx1, vy1), new Point(vx2, vy2) };
    }
  }
  public Vector ortho() {
    return new Vector(-vy, vx);
  }
}
