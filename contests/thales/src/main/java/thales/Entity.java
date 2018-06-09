package thales;

import thales.physics.Collision;
import thales.physics.CollisionType;
import thales.physics.Simulation;

public class Entity {
  public final static int UFO = 0;
  public final static int FLAG = 1;
  
  public final int type;
  public final int id;
  public final double radius;
  public final double mass = 1.0;
  public final Team myTeam;
  public final Team otherTeam;
  public int ignoreId = -1;
  
  public double x;
  public double y;
  public double vx, vy;

  public double _x;
  public double _y;
  public double _vx, _vy;

  public Entity(Team myTeam, Team otherTeam, int id, int type, double radius) {
    this.myTeam = myTeam;
    this.otherTeam = otherTeam;
    this.id = id;
    this.type = type;
    this.radius = radius;
  }
  
  public void backup() {
    _x = x;
    _y = y;
    _vx = vx;
    _vy = vy;
  }
  
  public void restore() {
    x = _x;
    y = _y;
    vx = _vx;
    vy = _vy;
  }
  
  public Collision collisionOnWall(double from, Collision possibleCollision) {
    double tx = 2.0;
    double ty = tx;

    if (x + vx < radius) {
      tx = (radius - x)/vx;
    } else if (x + vx > Player.WIDTH - radius) {
      tx = (Player.WIDTH - radius - x)/vx;
    }

    if (y + vy < radius) {
      ty = (radius - y)/vy;
    } else if (y + vy > Player.HEIGHT - radius) {
      ty = (Player.HEIGHT - radius - y)/vy;
    }

    int dir;
    double t;

    if (tx < ty) {
      dir = CollisionType.HORIZONTAL;
      t = tx + from;
    } else {
      dir = CollisionType.VERTICAL;
      t = ty + from;
    }

    if (t <= 0.0 || t > 1.0) {
      return null;
    }

    return possibleCollision.update(t, this, dir);
  }

  public Collision collision(Entity u, double from, Collision possibleCollision) {
    
    double x2 = x - u.x;
    double y2 = y - u.y;
    double r2 = radius + u.radius;
    double vx2 = vx - u.vx;
    double vy2 = vy - u.vy;
    double a = vx2*vx2 + vy2*vy2;

    if (u.ignoreId != this.id && this.ignoreId != u.id && x2*x2+y2*y2 - r2*r2 < -Simulation.EPSILON) {
      // collision at t = 0!
      return possibleCollision.update(0, this, u);
    }
    
    
    if (a < Simulation.EPSILON) {
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

    return possibleCollision.update(t, this, u);
  }

  
  public void bounce(Entity u) {
    // normal vector
    double nx = x - u.x;
    double ny = y - u.y;

    double r2 = radius+u.radius;
    if (nx*nx+ny*ny - r2*r2 < -Simulation.EPSILON) {
      // move entities to not overlap, possible when entities get the same radius, ie same mass
      double centerx = x + nx / 2.0;
      double centery = y + ny / 2.0;
      double decal = (radius+u.radius) / Math.sqrt(nx*nx+ny*ny);
      x = centerx-0.5*nx*decal;
      y = centery-0.5*ny*decal;
      u.x = centerx+0.5*nx*decal;
      u.y = centery+0.5*ny*decal;

      nx = x - u.x;
      ny = y - u.y;
    }

    // relative velocity
    double dvx = vx - u.vx;
    double dvy = vy - u.vy;

    double mcoeff = (mass + u.mass) / (mass * u.mass);
    double nxny2 = nx*nx + ny*ny;
    
    double product = (nx*dvx + ny*dvy) / (nxny2 * mcoeff);
    
    double fx = nx * product;
    double fy = ny * product;

    double m1c = 1.0 / mass;
    double m2c = 1.0 / u.mass;

    vx = -fx * m1c;
    vy = -fy * m1c;
    u.vx = fx * m2c;
    u.vy = fy * m2c;

  }

  public void bounce(int dir) {
    if (dir == CollisionType.HORIZONTAL) {
      vx = -vx;
    } else {
      vy = -vy;
    }
  }

  public void move(double t) {
    x= x + vx*t;
    y =y + vy*t;
  }

  public void end() {
    x = Math.round(x);
    y = Math.round(y);
    vx = (int)(vx*0.90);
    vy = (int)(vy*0.90);
  }

}
