package fantasticBitsMulti.simulation;

import fantasticBitsMulti.units.Unit;

public class Collision {
  public double t;
  public Unit a;
  public Unit b;
  
  public Collision() {
  }

  public Collision update(double t, Unit a, Unit b) {
    this.t = t;
    this.a = a;
    this.b = b;

    return this;
  }

}
