package coderoyale;

public class Vector {
  double x, y;

  public Vector() {
    x = y = 0;
  }
  public Vector(double x, double y) {
    super();
    this.x = x;
    this.y = y;
  }

  public Vector normalize() {
    double d = Math.sqrt(x*x+y*y);
    return new Vector (x / d, y / d );
  }

  public Vector mult(double d) {
    return new Vector(x*d, y*d);
  }

  public Vector add(Vector a) {
    return new Vector(x+a.x, y+a.y);
  }
  
  @Override
  public String toString() {
    return String.format("v(%.2f, %.2f) ",x,y);
  }
}
