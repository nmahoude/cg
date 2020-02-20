package fantasticBitsMulti.simulation;

import fantasticBitsMulti.units.Unit;

public class Collision {
  public double t;
  public int dir;
  public Unit a;
  public Unit b;
  
  public Collision() {
  }

  public Collision update(double t, Unit unit, int dir) {
    this.t = t;
    this.dir = dir;
    this.a = unit;
    this.b = null;
    return this;
  }

  public Collision update(double t, Unit a, Unit b) {
    this.dir = 0;
    this.t = t;
    this.a = a;
    this.b = b;

    return this;
  }

}
