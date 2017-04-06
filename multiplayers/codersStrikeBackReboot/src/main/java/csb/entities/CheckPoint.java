package csb.entities;

import trigonometry.Point;

public class CheckPoint extends Entity {
  public CheckPoint(double d, double e) {
    super(600);
    this.position = new Point(d, e);
  }
}
