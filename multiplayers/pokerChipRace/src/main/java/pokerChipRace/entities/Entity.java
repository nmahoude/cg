package pokerChipRace.entities;

import static pokerChipRace.GameState.HEIGHT;
import static pokerChipRace.GameState.WIDTH;

import pokerChipRace.simulate.Collision;
import pokerChipRace.simulate.Simulation;

public class Entity {
  public final int owner;
  public double x, y;
  public double radius;
  public double vx;
  public double vy;
  public double mass;
  
  public double targetx, targety; // <0 is wait
  
  public double _x,_y,_radius,_vx,_vy, _mass;
  
  public Entity(int owner) {
    this.owner = owner;
  }

  public void update(double e, double f, double d, float vx, float vy) {
    this.x = e;
    this.y = f;
    this.radius = d;
    this.vx = vx;
    this.vy = vy;
    this.mass = Math.PI * d * d;
  }
  
  public void backup() {
    _x = x;
    _y = y;
    _vx = vx;
    _vy = vy;
    _radius = radius;
    _mass = mass;
  }
  public void restore() {
    x = _x;
    y = _y;
    vx = _vx;
    vy = _vy;
    radius = _radius;
    mass = _mass;
  }
  
  public Collision collisionOnWall(double from, Collision possibleCollision) {
    double tx = 2.0;
    double ty = tx;

    if (x + vx < radius) {
      tx = (radius - x)/vx;
    } else if (x + vx > WIDTH - radius) {
      tx = (WIDTH - radius - x)/vx;
    }

    if (y + vy < radius) {
      ty = (radius - y)/vy;
    } else if (y + vy > HEIGHT - radius) {
      ty = (HEIGHT - radius - y)/vy;
    }

    int dir;
    double t;

    if (tx < ty) {
      dir = Collision.HORIZONTAL;
      t = tx + from;
    } else {
      dir = Collision.VERTICAL;
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
    double mcoeff = (mass + u.mass) / (mass * u.mass);
    double nx = x - u.x;
    double ny = y - u.y; // TODO vector
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
    if (dir == Collision.HORIZONTAL) {
      vx = -vx;
    } else {
      vy = -vy;
    }
  }

  public void move(double t) {
    x= x + vx*t;
    y =y + vy*t;
  }

}
