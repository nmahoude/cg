package csb.entities;

import trigonometry.Point;
import trigonometry.Vector;

public class Pod extends Entity {
  public static final int TIMEOUT = 100;
  public static Vector xVector = new Vector(1, 0);
  public Vector direction = new Vector(1,0);
  public int timeout = TIMEOUT;
  public int nextCheckPointId;
  public int shield;
  
  public Vector b_direction;
  public int b_nextCheckPointId;
  public int b_timeout;
  public int b_shield;
  
  public Pod(int id) {
    super(Type.POD, id, 400);
  }
  
  public Pod clone() {
    Pod pod = new Pod(id);
    copy(pod);
    return pod;
  }
  
  public void copy(Pod pod) {
    super.copy(pod);
    pod.direction = direction;
    pod.nextCheckPointId = nextCheckPointId;
    pod.shield = shield;
  }

  public void apply(Vector newDirection, double thrust) {
    direction = newDirection;
    
    if (shield == 0) {
      speed = speed.add(direction.dot(thrust));
    }
  }
  
  public void move(double t) {
    position = position.add(speed.dot(t));
  }
  
  public void end() {
    position = new Point((int)position.x, (int)position.y);
    speed = new Vector((int)(speed.vx*0.85), (int)(speed.vy*0.85));
    timeout-=1;
  }
  
  @Override
  public void backup() {
    super.backup();
    b_direction = direction;
    b_nextCheckPointId =nextCheckPointId;
    b_timeout = timeout;
    b_shield = shield;
  }
  
  @Override
  public void restore() {
    super.restore();
    direction = b_direction;
    nextCheckPointId = b_nextCheckPointId;
    timeout = b_timeout;
    shield = b_shield;
  }

  public void readInput(int x, int y, int vx, int vy, int angle, int nextCheckPointId) {
    System.err.println("pod.readInput("+x+","+y+","+vx+","+vy+","+angle+","+nextCheckPointId+");");
    this.position = new Point(x, y);
    this.speed = new Vector(vx, vy);
    this.direction = new Vector(Math.cos(angle * Math.PI / 180.0), Math.sin(angle * Math.PI / 180.0));
    this.nextCheckPointId = nextCheckPointId;
    backup();
  }
}
