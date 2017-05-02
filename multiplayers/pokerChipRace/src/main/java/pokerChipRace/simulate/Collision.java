package pokerChipRace.simulate;

import pokerChipRace.entities.Entity;

public class Collision {
  public static final int HORIZONTAL = 1;
  public static final int VERTICAL = 2;
  
  public Entity a;
  public Entity b;
  public int dir;
  
  public double t;

  public Collision() {
  }

  public static Collision buildFake() {
    Collision c = new Collision();
    c.t = +99;
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
}
