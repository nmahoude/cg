package fall2023;

public class Vec {
  public static final Vec ZERO = new Vec(0,0);
  public int vx, vy;

  public Vec(int vx, int vy) {
    this.vx  =vx;
    this.vy = vy;
  }

  public Vec(Pos target, Pos from) {
    this.vx = target.x - from.x;
    this.vy = target.y - from.y;
  }

  public Vec(Vec model) {
    this.vx = model.vx;
    this.vy = model.vy;
  }

  public void set(int vx, int vy) {
    this.vx  =vx;
    this.vy = vy;
  }

  public void set(Vec model) {
    this.vx = model.vx;
    this.vy = model.vy;
  }

  public Vec limitTo(int length) {
    if (vx*vx+vy*vy < length*length) {
      return this;
    } else {
      return normalize(length);
    }
     
  }
  
  public Vec normalize(int length) {
    double dist = Math.sqrt(vx*vx+vy*vy);
    vx = (int)(length * vx / dist);
    vy = (int)(length * vy / dist);
    return this;
  }
  
  public void hsymmetric() {
    this.vx = -this.vx;
  }

  public void vsymmetric() {
    this.vy = -this.vy;
  }

  public int length2() {
    return vx*vx+vy*vy;
  }

  public void clampTo(int clamp) {
    double l = length2();
    if (l > clamp * clamp) {
      l = Math.sqrt(l);
      vx = (int)(Math.round(vx * clamp / l));
      vy = (int)(Math.round(vy * clamp / l));
    }
  }
  
  @Override
  public String toString() {
    return "["+vx+","+vy+"]";
  }

  public boolean isZero() {
    return vx == 0 && vy == 0;
  }

  public Vec mult(int value) {
    vx *= value;
    vy *= value;
    return this;
  }

  public void copyFrom(Vec speed) {
    this.vx = speed.vx;
    this.vy = speed.vy;
  }
}
