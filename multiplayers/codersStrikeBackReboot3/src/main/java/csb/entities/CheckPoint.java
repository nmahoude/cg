package csb.entities;

public class CheckPoint extends Entity {
  public static final int RADIUS = 595;

  public CheckPoint(int id, double x, double y) {
    super(Type.CHECKPOINT, id, RADIUS);
    this.x = x;
    this.y = y;
  }
}
