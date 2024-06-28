package fall2023;

import java.util.Objects;

public class Pos {
  public int x;
  public int y;
  
  public Pos(int droneX, int droneY) {
    x = droneX;
    y = droneY;
  }

  public Pos(Pos model) {
    this.x = model.x;
    this.y = model.y;
  }

  public int dist2(Pos p) {
    return (x- p.x) * (x-p.x)+ (y-p.y)*(y-p.y) ;
  }
  
  public void add(int vx, int vy) {
    this.x += vx;
    this.y += vy;
  }

  public void add(Vec v) {
    this.x += v.vx;
    this.y += v.vy;
  }

  public void set(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public void set(Pos model) {
    this.x = model.x;
    this.y = model.y;
  }
  
  @Override
  public String toString() {
    return "("+x+","+y+")";
  }

  public boolean inRange(Pos p, int range) {
    return (p.x - x)*(p.x - x) + (p.y - y)*(p.y - y) <= range * range;
  }

  public boolean inRange2(Pos p, int range2) {
    return (p.x - x)*(p.x - x) + (p.y - y)*(p.y - y) <= range2;
  }

  public void copyFrom(Pos model) {
    this.x = model.x;
    this.y = model.y;
  }

  public static Pos from(Pos pos) {
    return new Pos(pos.x, pos.y);
  }

  public int dist(Pos o) {
    return (int)(Math.sqrt(dist2(o)));
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Pos other = (Pos) obj;
    return x == other.x && y == other.y;
  }

  public int distToBorder() {
    return  Math.min(x,  10000 - x);
  }

  public boolean outOfGame() {
    return x < 0 || x>= 10000;
  }
  
  
}
