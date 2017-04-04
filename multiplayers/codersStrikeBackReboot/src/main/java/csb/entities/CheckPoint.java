package csb.entities;

import trigonometry.Point;

public class CheckPoint extends Entity {
  public CheckPoint(int checkpointX, int checkpointY) {
    super(600);
    this.position = new Point(checkpointX, checkpointY);
  }
}
