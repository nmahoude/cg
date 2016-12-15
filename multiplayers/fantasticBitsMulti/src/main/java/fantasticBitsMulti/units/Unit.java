package fantasticBitsMulti.units;

import fantasticBitsMulti.Collision;
import fantasticBitsMulti.Player;
import trigonometry.Point;
import trigonometry.Vector;

public abstract class Unit {
  public static final double WIDTH = 16_000;
  public static final double HEIGHT = 7_500;
  
  public static final int VERTICAL = 1;
  public static final int HORIZONTAL = 2;

  public Wizard carrier;
  int grab;
  public Snaffle snaffle;
  
  public int id;
  public EntityType type;
  int state;
  public boolean dead = false;
  public Point position = new Point(0,0);
  private Point sposition;

  public double vx;
  public double vy;
  double svx;
  double svy;

  
  public double radius;
  public double mass;
  public double friction;

  public Unit(EntityType type, double radius, double mass, double friction) {
    this.type = type;
    this.radius = radius;
    this.mass = mass;
    this.friction = friction;
  }

  public void update(int id, int x, int y, int vx, int vy, int state) {
    this.id = id;
    this.position = new Point(x, y);
    this.vx = vx;
    this.vy = vy;
    this.state = state;
  }

  public void move(double t) {
    position = position.add(new Vector(vx*t, vy*t));
  }
  
  public void thrust(double thrust, double x, double y, double distance) {
    double coef = (thrust/mass) / distance;
    vx += (x - this.position.x) * coef;
    vy += (y - this.position.y) * coef;
  }

  public double speed() {
    return Math.sqrt(vx*vx+vy*vy);
  }
  
  public double speedTo(Point p) {
    double d = 1.0 / position.distTo(p);

    // vitesse dans la direction du checkpoint - (vitesse orthogonale)^2/dist au cheeckpoint
    double dx = (p.x - this.position.x) * d;
    double dy = (p.y - this.position.y) * d;
    double nspeed = vx*dx + vy*dy;
    double ospeed = dy*vx - dx*vy;

    return nspeed - (5 * ospeed * ospeed * d); // TODO c'est quoi ce 5 ???
  }

  public Collision collision(double from) {
    double tx = 2.0;
    double ty = tx;

    if (position.x + vx < radius) {
      tx = (radius - position.x)/vx;
    } else if (position.x + vx > WIDTH - radius) {
      tx = (WIDTH - radius - position.x)/vx;
    }

    if (position.y + vy < radius) {
      ty = (radius - position.y)/vy;
    } else if (position.y + vy > HEIGHT - radius) {
      ty = (HEIGHT - radius - position.y)/vy;
    }

    int dir;
    double t;

    if (tx < ty) {
      dir = HORIZONTAL;
      t = tx + from;
    } else {
      dir = VERTICAL;
      t = ty + from;
    }

    if (t <= 0.0 || t > 1.0) {
      return null;
    }

    return Player.collisionsCache[Player.collisionsCacheFE++].update(t, this, dir);
  }

  public Collision collision(Unit u, double from) {
    double x2 = position.x - u.position.x;
    double y2 = position.y - u.position.y;
    double r2 = radius + u.radius;
    double vx2 = vx - u.vx;
    double vy2 = vy - u.vy;
    double a = vx2*vx2 + vy2*vy2;

    if (a < Player.E) {
      return null;
    }

    double b = -2.0*(x2*vx2 + y2*vy2);
    double delta = b*b - 4.0*a*(x2*x2 + y2*y2 - r2*r2);

    if (delta < 0.0) {
      return null;
    }

    double t = (b - Math.sqrt(delta))*(1.0/(2.0*a));

    if (t <= 0.0) {
      return null;
    }

    t += from;

    if (t > 1.0) {
      return null;
    }

    return Player.collisionsCache[Player.collisionsCacheFE++].update(t, this, u);
  }

  
  public void bounce(Unit u) {
    double mcoeff = (mass + u.mass) / (mass * u.mass);
    double nx = position.x - u.position.x;
    double ny = position.y - u.position.y; // TODO vector
    double nxnydeux = nx*nx + ny*ny;
    double dvx = vx - u.vx;
    double dvy = vy - u.vy;
    double product = (nx*dvx + ny*dvy) / (nxnydeux * mcoeff);
    double fx = nx * product;
    double fy = ny * product;
    double m1c = 1.0 / mass;
    double m2c = 1.0 / u.mass;

    vx -= fx * m1c;
    vy -= fy * m1c;
    u.vx += fx * m2c;
    u.vy += fy * m2c;

      // Normalize vector at 100
    double impulse = Math.sqrt(fx*fx + fy*fy);
    if (impulse < 100.0) {
      double min = 100.0 / impulse;
      fx = fx * min;
      fy = fy * min;
    }

    vx -= fx * m1c;
    vy -= fy * m1c;
    u.vx += fx * m2c;
    u.vy += fy * m2c;
  }

  public void bounce(int dir) {
    if (dir == HORIZONTAL) {
      vx = -vx;
    } else {
      vy = -vy;
    }
  }

  public void end() {
    position = new Point(Math.round(position.x), Math.round(position.y));
    vx = Math.round(vx*friction);
    vy = Math.round(vy*friction);
  }
  
  public boolean can(Unit u) {
    // TODO check the conversion to java
    if (type == EntityType.SNAFFLE) {
      return carrier == null && !dead && u.snaffle == null && u.grab == 0;
    } else if (u.type == EntityType.SNAFFLE) {
      return u.carrier == null && u.dead == false && snaffle == null && grab == 0;
    }
    return true;
  }
  
  public void save() {
    sposition = position;
    svx = vx;
    svy = vy;
  }

  public void reset() {
    position = sposition;
    vx = svx;
    vy = svy;
  }


  public void move() {
    // TODO Auto-generated method stub
  }

  public void print() {
    // TODO Auto-generated method stub
    
  }

}
