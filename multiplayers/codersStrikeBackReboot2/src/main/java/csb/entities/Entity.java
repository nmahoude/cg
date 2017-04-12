package csb.entities;

import trigonometry.Point;
import trigonometry.Vector;

public class Entity {
  public static final Vector NO_SPEED = new Vector(0,0);
  public final Type type;
  public final int id;
  public double radius;
  public Point position = new Point(0,0);
  
  public double vx, vy;

  private Point b_position;
  private double b_vx, b_vy;
  
  public Entity(Type type, int id, int radius) {
    this.type = type;
    this.id = id;
    this.radius = radius;
  }
  public void backup() {
    b_position = position;

    b_vx = vx;
    b_vy = vy;
  }
  public void restore() {
    position = b_position;
    vx = b_vx;
    vy = b_vy;
  }
  public void copyTo(Entity entity) {
    entity.position = position;
    entity.vx = vx;
    entity.vy = vy;
  }
  
  public Collision collision(Entity u, double from) {
    double x2 = position.x - u.position.x;
    double y2 = position.y - u.position.y;
    double r2 = radius + u.radius;
    double vx2 = vx - u.vx;
    double vy2 = vy - u.vy;
    double a = vx2*vx2 + vy2*vy2;

    if (a < Collision.EPSILON) {
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

    return new Collision().update(t, this, u);
  }
  
  public void bounce(Entity other) {
    if (type != Type.POD || other.type != Type.POD) {
      return;
    }
    Pod pod1 = (Pod)this;
    Pod pod2 = (Pod)other;
    
    float m1 = pod1.shield>0 ? 10 : 1;
    float m2 = pod2.shield>0 ? 10 : 1;
    
    double mcoeff = (m1 + m2) / (m1 * m2);
    double nx = position.x - other.position.x;
    double ny = position.y - other.position.y; // TODO vector
    double nxnydeux = nx*nx + ny*ny;
    double dvx = vx - other.vx;
    double dvy = vy - other.vy;
    double product = (nx*dvx + ny*dvy) / (nxnydeux * mcoeff);
    double fx = nx * product;
    double fy = ny * product;
    double m1c = 1.0 / m1;
    double m2c = 1.0 / m2;

    vx = vx - fx * m1c;
    vy = vy - fy * m1c;
    other.vx = other.b_vx + fx * m2c;
    other.vy = other.vy + fy * m2c;

      // Normalize vector at 120
    double impulse = Math.sqrt(fx*fx + fy*fy);
    if (impulse < 120.0) {
      double min = 120.0 / impulse;
      fx = fx * min;
      fy = fy * min;
    }

    vx = vx - fx * m1c;
    vy = vy - fy * m1c;
    other.vx = other.vx + fx * m2c;
    other.vy = other.vy + fy * m2c;
  }
}
