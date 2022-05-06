package spring2022;

import java.util.Objects;

public class Vec {
  public int vx;
  public int vy;
  
  public Vec(int dx, int dy) {
    this.vx = dx;
    this.vy = dy;
  }

  public Vec() {
    this.vx = 0;
    this.vy = 0;
  }
  
  
  public void copyFrom(Vec model) {
    this.vx = model.vx;
    this.vy = model.vy;
  }

  public void inverse() {
    vx = -vx;
    vy = -vy;
  }

  public void target(Pos origin, Pos target) {
    int dist = target.dist(origin);
    this.vx = (State.MOB_MOVE * (target.x - origin.x) / dist);
    this.vy = ( State.MOB_MOVE * (target.y - origin.y) / dist);
  }

  public void set(int dx, int dy) {
    vx = dx;
    vy = dy;
  }

  @Override
  public int hashCode() {
    return Objects.hash(vx, vy);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Vec other = (Vec) obj;
    return Double.doubleToLongBits(vx) == Double.doubleToLongBits(other.vx)
        && Double.doubleToLongBits(vy) == Double.doubleToLongBits(other.vy);
  }
  
  @Override
  public String toString() {
    if (Player.inversed) {
      return ""+(-vx)+","+(-vy);
    } else {
      return ""+vx+","+vy;
    }
  }

  public void alignTo(Pos from, Pos target, int speedMax) {
    double dx = target.x - from.x;
    double dy = target.y - from.y;
    double dist = (int) Math.sqrt(dx * dx + dy * dy);
    dx = dx * speedMax / dist;
    dy = dy * speedMax / dist;
    this.set((int)dx, (int)dy);
  }
  
}
