package csb._cgfx;

import csb.entities.Pod;

public class Pos {
  public double x, y;
  
  public void copyFrom(double x2, double y2) {
    this.x = x2;
    this.y = y2;
  }

  public void copyFrom(Pod pod) {
    this.x = pod.x;
    this.y = pod.y;
    
  }
}
