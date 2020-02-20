package coderoyale;

public class CRVector {
  double x, y;

  public CRVector() {
    x = y = 0;
  }
  public CRVector(double x, double y) {
    super();
    this.x = x;
    this.y = y;
  }

  public CRVector normalize() {
    double d = Math.sqrt(x*x+y*y);
    return new CRVector (x / d, y / d );
  }

  public CRVector mult(double d) {
    return new CRVector(x*d, y*d);
  }

  public CRVector add(CRVector a) {
    return new CRVector(x+a.x, y+a.y);
  }
  
  @Override
  public String toString() {
    return String.format("v(%.2f, %.2f) ",x,y);
  }
}
