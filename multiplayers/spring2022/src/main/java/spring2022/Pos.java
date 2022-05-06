package spring2022;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pos {
  public static final Pos VOID = new Pos(-10000, -10000);
  
  
  
  public static List<Pos> heroMoveRadius = new ArrayList<>();
  static {
    for (int dy = -800; dy <= 800; dy += 10) {
      for (int dx = -800; dx <= 800; dx += 10) {
        if (dx * dx + dy * dy > 800 * 800) continue;
        
        heroMoveRadius.add(new Pos(dx, dy));
        
      }
    }
  }
  
  
  
  public int x;
  public int y;

  public Pos() {
    this.x = 0;
    this.y = 0;
  }

  public Pos(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public Pos(Pos target) {
    this.x = target.x;
    this.y = target.y;
  }

  public final int dist2(Pos pos) {
    return (this.x - pos.x) * (this.x - pos.x) + (this.y - pos.y) * (this.y - pos.y);
  }

  public boolean isInRange(Pos pos, int range) {
    return dist2(pos) <= range * range;
  }

  public void copyFrom(Pos pos) {
    this.x = pos.x;
    this.y = pos.y;
  }

  public void moveToward(Pos target, int maxMove) {
    int dist2 = this.dist2(target);
    if (dist2 <= maxMove * maxMove) {
      this.copyFrom(target);
    } else {
      int dist = (int) (Math.sqrt(dist2));

      this.x += (target.x - this.x) * maxMove / dist;
      this.y += (target.y - this.y) * maxMove / dist;
    }
  }

  @Override
  public String toString() {
    if (Player.inversed) {
      return String.format("(%d, %d)", State.WIDTH - x, State.HEIGHT - y);
    } else {
      return String.format("(%d, %d)", x, y);
    }
  }

  public String output() {
    if (Player.inversed) {
      return "" + (State.WIDTH - x) + " " + (State.HEIGHT - y);
    } else {
      return "" + x + " " + y;
    }
  }

  public int dist(Pos pos) {
    return (int)Math.sqrt(dist2(pos));
  }

  public String outputEncoded() {
    if (Player.inversed) {
      return "" + (char) (State.WIDTH - x) + (char) (State.HEIGHT - y);
    } else {
      return "" + (char) x + "" + (char) y;
    }
  }

  public void addAndSnap(int dx, int dy) {
    this.add(dx, dy);
    
    if (this.x < 0) this.x = 0;
    if (this.x > State.WIDTH) this.x = State.WIDTH;
    if (this.y < 0) this.y = 0;
    if (this.y > State.HEIGHT) this.y = State.HEIGHT;
  }

  
  private static final int w = State.WIDTH;
  private static final int h = State.HEIGHT;
  private static final int baseRadius = State.BASE_TARGET_DIST;
  
  public void addAndSnapForWind(int dx, int dy) {
    
    int toX = this.x + dx;
    int toY = this.y + dy;
    Pos inter = null;
    
    if (toY >= State.HEIGHT) {
      inter = intersection(this.x, this.y, toX, toY, w - baseRadius, h, w, h);
    } else if (toY < 0) {
        inter = intersection(this.x, this.y, toX, toY, 0, 0, baseRadius, 0);
    }
    if (inter == null) {
        if (toX >= w) {
            inter = intersection(this.x, this.y, toX, toY, w, h - baseRadius, w, h);
        } else if (toX < 0) {
            inter = intersection(this.x, this.y, toX, toY, 0, 0, 0, baseRadius);
        }
    }

    if (inter != null) {
      this.x = inter.x;
      this.y = inter.y;
    } else {
      this.x += dx;
      this.y += dy;
    }
  }
  
  private static Pos intersection = new Pos();
  private Pos intersection(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
    
    double a1 = 1.0*(y2-y1)/(x2-x1);
    double b1 = y1 - a1*x1 ;
    double a2 = 1.0*(y4-y3)/(x4-x3);
    double b2 = y3 - a2*x3;
    
    if ((y2-y1)*(x4-x3) == (x2-x1)*(y4-y3)) return null;
    
    double xi = -(b1-b2)/(a1-a2);
    double yi = a1*xi + b1;
    
    if (xi < Math.min(x1, x2) || xi > Math.max(x1, x2)) return null;
    if (xi < Math.min(x3, x4) || xi > Math.max(x3, x4)) return null;
    
    intersection.x = (int)xi;
    intersection.y = (int)yi;
    return intersection;
    
  }
  
  
  
  public void addAndSnap(double dx, double dy) {
    dx += this.x;
    dy += this.y;

    this.x = (int)Math.round(dx+0.45);
    this.y = (int)Math.round(dy+0.45);
    
    if (this.x < 0) this.x = 0;
    if (this.x > State.WIDTH) this.x = State.WIDTH;
    if (this.y < 0) this.y = 0;
    if (this.y > State.HEIGHT) this.y = State.HEIGHT;
  }
  
  
  public void decode(String string) {
    x = (int) (string.charAt(0));
    y = (int) (string.charAt(1));
  }

  public void add(int dx, int dy) {
    this.x += dx;
    this.y += dy;
  }

  public void copyFrom(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public boolean insideOfMap() {
    if (this.x < 0) return false;
    if (this.x > State.WIDTH) return false;
    if (this.y < 0) return false;
    if (this.y > State.HEIGHT) return false;
    
    return true;
  }

  public static Pos get(int x, int y) {
    return new Pos(x, y);
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

  public void set(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public void inverse() {
    x = State.WIDTH-x;
    y = State.HEIGHT-y;
  }

  public void add(Vec speed) {
    this.x += speed.vx;
    this.y += speed.vy;
  }

  public void addAndSnap(Vec speed) {
    addAndSnap(speed.vx, speed.vy);
  }

  private static final Pos stepFrom = new Pos();
  public Pos stepFrom(Pos pos, int range) {
    int dx = this.x - pos.x;
    int dy = this.y - pos.y;
    double dist = Math.sqrt(dx*dx+dy*dy);
    
    stepFrom.x = (int)(pos.x + range * dx / dist);
    stepFrom.y = (int)(pos.y + range*dy/dist);
    return stepFrom;
  }

  public void add(Pos target) {
    this.x += target.x;
    this.y += target.y;
  }
  
  
  public int fastDist(Pos pos) {
    double d = (pos.x-x)*(pos.x-x) + (pos.y-y)*(pos.y-y);
    double sqrt = Double.longBitsToDouble( ( ( Double.doubleToLongBits( d )-(1l<<52) )>>1 ) + ( 1l<<61 ) );
    double better = (sqrt + d/sqrt)/2.0;
    return (int)better;
  }

  public void add(double dx, double dy) {
    this.x += dx;
    this.y += dy;
  }

}
