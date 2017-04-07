package csb.entities;

import trigonometry.Point;

public class CheckPoint extends Entity {
  public CheckPoint(int id, double d, double e) {
    super(Type.CHECKPOINT, id, 600);
    this.position = new Point(d, e);
  }
}
