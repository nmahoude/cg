package csb.entities;

import trigonometry.Point;
import trigonometry.Vector;

public class Pod extends Entity {
  public static Vector xVector = new Vector(1, 0);
  public Vector speed = new Vector(0,0);
  public Vector direction = new Vector(1,0);
  public int nextCheckPointId;
  
  private Vector b_speed;
  private Vector b_direction;
  private int b_nextCheckPointId;

  public Pod() {
    super(400);
  }
  
  public void apply(Vector newDirection, double thrust) {
    direction = newDirection;
    speed = speed.add(direction.dot(thrust));
  }
  
  
  @Override
  public void backup() {
    super.backup();
    b_speed = speed;
    b_direction = direction;
    b_nextCheckPointId =nextCheckPointId;
  }
  @Override
  public void restore() {
    super.restore();
    speed = b_speed;
    direction = b_direction;
    nextCheckPointId = b_nextCheckPointId;
  }

  public void readInput(int x, int y, int vx, int vy, int angle, int nextCheckPointId) {
    this.position = new Point(x, y);
    this.speed = new Vector(vx, vy);
    this.direction = new Vector(Math.cos(angle * Math.PI / 180.0), Math.sin(angle * Math.PI / 180.0));
    this.nextCheckPointId = nextCheckPointId;
    
    backup();
  }

}
