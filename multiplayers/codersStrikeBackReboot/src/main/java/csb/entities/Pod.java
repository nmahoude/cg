package csb.entities;

import trigonometry.Point;
import trigonometry.Vector;

public class Pod extends Entity {
  public static Vector xVector = new Vector(1, 0);
  public Vector speed;
  public double angle;
  public int nextCheckPointId;
  
  private Vector b_speed;
  private double b_angle;
  private int b_nextCheckPointId;

  public Pod() {
    super(400);
  }
  
  public void apply(double angle, double thrust) {
    Vector direction = xVector.rotate(angle);
    speed = speed.dot(0.85).add(direction.dot(thrust));
  }
  
  @Override
  public void backup() {
    super.backup();
    b_speed = speed;
    b_angle =angle;
    b_nextCheckPointId =nextCheckPointId;
  }
  @Override
  public void restore() {
    super.restore();
    speed = b_speed;
    angle = b_angle;
    nextCheckPointId = b_nextCheckPointId;
  }

  public void readInput(int x, int y, int vx, int vy, int angle, int nextCheckPointId) {
    this.position = new Point(x, y);
    this.speed = new Vector(vx, vy);
    this.angle = angle;
    this.nextCheckPointId = nextCheckPointId;
    
    backup();
  }
}
