package pokerChipRace.entities;

import pokerChipRace.GameState;
import pokerChipRace.simulate.Collision;
import pokerChipRace.simulate.Simulation;
import trigonometry.Vector;

public class Entity {
  public final int id;
  public int owner;
  public double x = -100, y=-100;
  public double radius = -100;
  public double vx = 0;
  public double vy = 0;
  public double mass;
  public int ignoreId = -1;
  
  public double targetx =-100, targety = -100; // <0 is wait
  
  public double _x,_y,_radius,_vx,_vy, _mass;
  public int _ignoreId;
  
  public Entity(int id, int owner) {
    this.id = id;
    this.owner = owner;
  }

  public void update(int owner, double x, double y, double radius, double vx, double vy) {
    this.owner = owner;
    this.x = x;
    this.y = y;
    this.radius = radius;
    this.vx = vx;
    this.vy = vy;
    this.mass = Math.PI * radius * radius;
    targetx = targety = -100;
  }
  
  public void backup() {
    _x = x;
    _y = y;
    _vx = vx;
    _vy = vy;
    _radius = radius;
    _mass = mass;
    _ignoreId = ignoreId;
  }
  public void restore() {
    x = _x;
    y = _y;
    vx = _vx;
    vy = _vy;
    radius = _radius;
    mass = _mass;
    ignoreId = _ignoreId;
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
    if (radius > u.radius && u.ignoreId != id) {
      eat(u);
    } else if (radius < u.radius && ignoreId != u.id) {
      u.eat(this);
    } else {
      // collision
      //       impulse = normalVector*2*(normalVector · relativeVelocity)/(1/entity1mass + 1/entity2mass)


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
    if (x<radius+1) { x=radius+1;}
    if (y<radius+1) { y=radius+1;}
    if (x+radius+1>GameState.WIDTH) { x = GameState.WIDTH-radius-1; }
    if (y+radius+1>GameState.HEIGHT) { y = GameState.HEIGHT-radius-1; }
  }

  public final boolean isDead() {
    return radius < 0;
  }

  public void setDead() {
    radius = -1; // dead entity
    x = -1000;
    y = -1000;
    vx = 0;
    vy = 0;    
  }

  public void bounce(int dir) {
    ignoreId = -1;
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
    mass = Math.PI * radius * radius;

    droplet.update(-1, x - (radius - dr) * dir.vx, y - (radius - dr) * dir.vy, dr, vx - 200.0*dir.vx, vy - 200.0*dir.vy);
    droplet.ignoreId = id;

    vx += 14.2857142857*dir.vx;
    vy += 14.2857142857*dir.vy;
    droplet.checkEntityBoundaries();
  }
  
  @Override
  public String toString() {
    return ""+id+" pos("+x+","+y+") r="+radius;
  }
}
