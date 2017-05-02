package pokerChipRace.entities;

import pokerChipRace.GameState;
import pokerChipRace.simulate.Collision;
import pokerChipRace.simulate.Simulation;
import trigonometry.Vector;

public class Entity {
  public final int id;
  public final int owner;
  public double x, y;
  public double radius;
  public double vx;
  public double vy;
  public double mass;
  public int ignore = -1;
  
  public double targetx, targety; // <0 is wait
  
  public double _x,_y,_radius,_vx,_vy, _mass;
  public int _ignore;
  
  public Entity(int id, int owner) {
    this.id = id;
    this.owner = owner;
  }

  public void update(double x, double y, double radius, double vx, double vy) {
    this.x = x;
    this.y = y;
    this.radius = radius;
    this.vx = vx;
    this.vy = vy;
    this.mass = Math.PI * radius * radius;
  }
  
  public void backup() {
    _x = x;
    _y = y;
    _vx = vx;
    _vy = vy;
    _radius = radius;
    _mass = mass;
    _ignore = ignore;
  }
  public void restore() {
    x = _x;
    y = _y;
    vx = _vx;
    vy = _vy;
    radius = _radius;
    mass = _mass;
    ignore = _ignore;
  }
  
  public Collision collisionOnWall(double from, Collision possibleCollision) {
    double tx = 2.0;
    double ty = tx;

    if (x + vx < radius) {
      tx = (radius - x)/vx;
    } else if (x + vx > GameState.WIDTH - radius) {
      tx = (GameState.WIDTH - radius - x)/vx;
    }

    if (y + vy < radius) {
      ty = (radius - y)/vy;
    } else if (y + vy > GameState.HEIGHT - radius) {
      ty = (GameState.HEIGHT - radius - y)/vy;
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
    if (radius > u.radius && u.ignore != id) {
      eat(u);
    } else if (radius < u.radius && ignore != u.id) {
      u.eat(this);
    } else {
      // elastic collision
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
  }

  /**
   * this is always the biggest entity
   * @param other
   */
  private void eat(Entity other) {
    vx = (vx*mass + other.vx*other.mass) / (mass+other.mass);
    vy = (vy*mass + other.vy*other.mass) / (mass+other.mass);

    x = (x*mass + other.x*other.mass) / (mass+other.mass);
    y = (y*mass + other.y*other.mass) / (mass+other.mass);
    
    
    mass += other.mass;
    radius = Math.sqrt(mass / Math.PI);

    checkEntityBoundaries();
    
    other.setDead();
  }

  /**
   * check if the entity is still in the boundaries, 
   * Teleport it within boundaries if not
   */
  private void checkEntityBoundaries() {
    if (x-radius < 0) {
      x = radius;
    } else if (x + radius > GameState.WIDTH) {
      x = GameState.WIDTH-radius;
    }
    if (y-radius < 0) {
      y = radius;
    } else if (y+radius > GameState.HEIGHT) {
      y = GameState.HEIGHT-radius;
    }
  }

  private void setDead() {
    radius = -1; // dead entity
    x = -1000;
    y = -1000;
    vx = 0;
    vy = 0;    
  }

  public void bounce(int dir) {
    ignore = -1;
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

  public void debug() {
    System.err.println("readEntity("+id+","+owner+","+x+", "+y+", "+vx+", "+vy+","+radius+");");
  }

  public void eject(Entity droplet, Vector dir) {
    double dr = radius * 0.25819888974;
    radius = radius * 0.96609178307;
    
    droplet.update(x - (radius - dr) * dir.vx, y - (radius - dr) * dir.vy, dr, vx - 200.0*dir.vx, vy - 200.0*dir.vy);
    droplet.ignore = id;

    vx += 14.2857142857*dir.vx;
    vy += 14.2857142857*dir.vy;
  }
  
  @Override
  public String toString() {
    return ""+id+" pos("+x+","+y+") r="+radius;
  }
}
