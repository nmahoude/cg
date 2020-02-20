package thales.physics;

import thales.Entity;

public class Collision {
  public static final int HORIZONTAL = 1;
  public static final int VERTICAL = 2;
  
  public Entity a;
  public Entity b;
  public int dir;
  
  public double t;

  public Collision() {
  }

  @Override
  public String toString() {
    if (b == null) {
      if (dir == HORIZONTAL) {
        return ""+a.id+"-> horizontal Wall";
      } else {
        return ""+a.id+"-> vertical Wall";
      }
    }
    return ""+a.id +"("+a.radius+") <-> " + b.id +"("+b.radius+") @ "+t;
  }
  public static Collision buildFake() {
    Collision c = new Collision();
    c.t = +99999;
    c.a = null;
    c.b = null;
    return c;
  }

  public Collision update(double t, Entity entity, int dir) {
    this.t = t;
    this.a = entity;
    this.dir = dir;
    this.b = null;
     
    return this;
  }

  public Collision update(double t, Entity entity, Entity b) {
    this.t = t;
    this.a = entity;
    this.dir = 0;
    this.b = b;
     
    return this;
  }
  
  public void copy(Collision model) {
    this.t = model.t;
    this.a = model.a;
    this.dir = model.dir;
    this.b = model.b;
  }
}
