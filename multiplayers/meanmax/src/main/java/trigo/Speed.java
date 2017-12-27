package trigo;

public class Speed {
  public double vx;
  public double vy;
  
  public Speed() {
  }
  public Speed(double d, double e) {
    vx = d;
    vy = e;
  }

  public final boolean isNull() {
    return (vx == 0.0 && vy == 0.0);
  }

  public double length2() {
    return vx * vx + vy * vy;
  }

  public double length() {
    return Math.sqrt(vx * vx + vy * vy);
  }

  public void copyFrom(Speed speed) {
    this.vx = speed.vx;
    this.vy = speed.vy;
  }
  public final double dist(Speed p) {
    return Math.sqrt((p.vx-vx)*(p.vx-vx) + (p.vy-vy)*(p.vy-vy));
  }
}
