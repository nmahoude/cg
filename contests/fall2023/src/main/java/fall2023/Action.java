package fall2023;

public class Action {

  public static final Action WAIT = new Action();
  public int dx;
  public int dy;
  boolean lamp = false;

  public String output(Drone d) {
    return "MOVE " +(d.pos.x + dx)+" "+(d.pos.y + dy)+ " " + (lamp ? "1" : "0");
  }
  
  @Override
  public String toString() {
    return "Action | dx="+dx+" dy="+dy+" lamp = "+lamp;
  }

  public void copyFrom(Action model) {
    this.dx = model.dx;
    this.dy = model.dy;
    this.lamp = model.lamp;
  }
  
}
