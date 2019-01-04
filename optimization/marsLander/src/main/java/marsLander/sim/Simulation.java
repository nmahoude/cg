package marsLander.sim;

import marsLander.Mars;
import marsLander.MarsLander;

public class Simulation {
  static final double G = 3.711;

  
  static double cos[] = new double[360];
  static double sin[] = new double[360];
  static {
    for (int i=-180;i<180;i++) {
      cos[180+i] = Math.cos(i*Math.PI/180.);
      sin[180+i] = Math.sin(i*Math.PI/180.);
    }
  }
  private Mars mars;
  private MarsLander lander;

  public int result;

  public Simulation(Mars mars, MarsLander lander) {
    this.mars = mars;
    this.lander = lander;
  }
  
  /*
   * return false if lander is destroyed
   */
  public boolean update(int values[]) {
    if (lander.angle + values[0] < -90 || lander.angle + values[0] > 90) values[0] = 0;
    if (lander.thrust + values[1] < 0 || lander.thrust + values[1] > 4) values[1] = 0;
    if (lander.fuel == 0) {
      values[1] = 0;
    }
    
    int angle = lander.angle + values[0];
    int thrust = lander.thrust + values[1];
    
    lander.fuel -= thrust;
    lander.x = lander.x+lander.vx-0.5*sin[180+angle]*thrust;
    lander.y = lander.y+lander.vy+0.5*(cos[180+angle]*thrust-G);
    lander.vx = lander.vx-1.*sin[180+angle]*thrust;
    lander.vy = lander.vy+1.*(cos[180+angle]*thrust-G);
    
    lander.angle  = angle;
    lander.thrust = thrust; // TODO need to keep it ?
    
    result = checkAgainstMars();
    return true;
  }

  /**
   * 0 : still in the air
   * 1 : landing safe
   * -1 : crash
   */
  private int checkAgainstMars() {
    if (lander.x < 0) return -1;
    if (lander.x > 6999) return -1;
    
    if (lander.y < mars.dist[lander.getXAsInt()]) {
      if (mars.distanceToLandingZone(lander) > 0) {
        return -1;
      } else if (lander.angle == 0 && Math.abs(lander.vx) < 20 && Math.abs(lander.vy) < 40) {
        return 1;
      } else {
        return -1;
      }
    }
    return 0;
  }

  public void reset() {
    result = 0;
  }
}
