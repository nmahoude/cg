package csb.entities;

public class Collision {
  public static final double EPSILON = 0.001;
  public double t;
  public int dir;
  public Entity a;
  public Entity b;
  
  public Collision() {
  }

  public Collision update(double t, Entity Entity, int dir) {
    this.t = t;
    this.dir = dir;
    this.a = Entity;
    this.b = null;
    return this;
  }

  public Collision update(double t, Entity a, Entity b) {
    this.dir = 0;
    this.t = t;
    this.a = a;
    this.b = b;

    return this;
  }

}
