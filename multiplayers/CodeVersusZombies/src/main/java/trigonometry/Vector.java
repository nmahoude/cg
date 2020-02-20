package trigonometry;

public class Vector {

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

}
