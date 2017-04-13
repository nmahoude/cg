package csb.entities;

import csb.Team;
import trigonometry.Point;
import trigonometry.Vector;

public class Pod extends Entity {
  public static final int TIMEOUT = 100;

  public final Team team;
  
  public static Vector xVector = new Vector(1, 0);
  public Vector direction = new Vector(1,0);
  public int nextCheckPointId = 1;
  public int shield;
  public int lap;
  
  public Vector b_direction;
  public int b_nextCheckPointId;
  public int b_shield;
  public int b_lap;
  
  public Pod(int id, Team team) {
    super(Type.POD, id, 400);
    this.team = team;
    lap = 0;
  }
  
  public Pod clone() {
    Pod pod = new Pod(id, team);
    copyTo(pod);
    return pod;
  }
  
  public void copyTo(Pod pod) {
    super.copyTo(pod);
    pod.direction = direction;
    pod.nextCheckPointId = nextCheckPointId;
    pod.shield = shield;
    pod.lap = lap;
  }

  public void apply(double angle, double thrust) {
    Vector newDirection = direction.rotate(angle);
    apply(newDirection, thrust);
  }

  public void apply(Vector newDirection, double thrust) {
    direction = newDirection;
    
    if (thrust < -0.5) {
      shield = 4;
    }
    if (shield == 0) {
      Vector dot = direction.dot(thrust);
      vx +=dot.vx;
      vy +=dot.vy;
    }
  }

  public void applyNoAngleCheck(Point target, double thrust) {
    Vector desiredDirection = target.sub(new Point(x,y)).normalize();
    apply(desiredDirection, thrust);
  }
  
  public void apply(Point target, double thrust) {
    Vector desiredDirection = target.sub(new Point(x,y)).normalize();
    if (Math.acos(desiredDirection.dot(direction)) > Math.PI / 10 ) {
      Vector ortho = direction.ortho();
      double signum = ortho.dot(desiredDirection) > 0 ? 1.0 : -1.0;
      desiredDirection = direction.rotate(signum * Math.PI/10);
    }
    apply(desiredDirection, thrust);
  }

  public void move(double t) {
    x = x+vx*t;
    y = y+vy*t;
  }
  
  public void end() {
    x = Math.round(x);
    y = Math.round(y);
    vx = (int)(vx*0.85);
    vy = (int)(vy*0.85);
    if (shield>0) shield--;
  }
  
  @Override
  public void backup() {
    super.backup();
    b_direction = direction;
    b_nextCheckPointId =nextCheckPointId;
    b_shield = shield;
    b_lap = lap;
  }
  
  @Override
  public void restore() {
    super.restore();
    direction = b_direction;
    nextCheckPointId = b_nextCheckPointId;
    shield = b_shield;
    lap = b_lap;
  }

  public void readInput(int x, int y, int vx, int vy, int angle, int nextCheckPointId) {
    System.err.println("pod.readInput("+x+","+y+","+vx+","+vy+","+angle+","+nextCheckPointId+");");
    if (nextCheckPointId == 1 && this.nextCheckPointId == 0) {
      lap++;
    }
    
    this.x = x;
    this.y = y;
    this.vx = vx;
    this.vy = vy;
    this.direction = new Vector(Math.cos(angle * Math.PI / 180.0), Math.sin(angle * Math.PI / 180.0));
    this.nextCheckPointId = nextCheckPointId;
  }

}
