package csb.entities;

import trigonometry.Point;

public class CheckPoint extends Entity {
  public static final double RADIUS = 600;

  public CheckPoint(int id, double x, double y) {
    super(Type.CHECKPOINT, id, 600);
    this.position = new Point(x, y);
  }
}
