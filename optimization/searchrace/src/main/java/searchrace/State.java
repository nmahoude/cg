package searchrace;

import java.util.Scanner;

public class State {
  public static int checkpointX[];
  public static int checkpointY[];
  public static int distanceRemaining[];
  

  static double cosinuses[] = new double[361];
  static double sinuses[] = new double[361];
  static {
    for (int a=0;a<=360;a++) {
      cosinuses[a] = Math.cos(a * Math.PI / 180);
      sinuses[a] = Math.sin(a * Math.PI / 180);
    }
  }
  
  public int checkpointIndex;
  public int x;
  public int y;
  public int vx;
  public int vy;
  public int angle;
  public boolean finished;
  private static int checkpointsCount;

  public static void readInit(Scanner in) {
    checkpointsCount = in.nextInt();
    checkpointX = new int[checkpointsCount+2];
    checkpointY = new int[checkpointsCount+2];
    distanceRemaining = new int[checkpointsCount+2];
    
    for (int i = 0; i < checkpointsCount; i++) {
      int cpX = in.nextInt(); // Position X
      int cpY = in.nextInt(); // Position Y
      checkpointX[i] = cpX;
      checkpointY[i] = cpY;
    }

    checkpointX[checkpointsCount] = Integer.MAX_VALUE;
    checkpointY[checkpointsCount] = Integer.MAX_VALUE;

    for (int i = checkpointsCount-1; i > 0 ; i--) {
      int dist = (int)Math.sqrt(
                (checkpointX[i] - checkpointX[i-1])*(checkpointX[i] - checkpointX[i-1])
                + (checkpointY[i] - checkpointY[i-1])*(checkpointY[i] - checkpointY[i-1])
                );
      distanceRemaining[i-1] = distanceRemaining[i] + dist;
    
    }
  }

  public void read(Scanner in) {
    checkpointIndex = in.nextInt();
    x = in.nextInt();
    y = in.nextInt();
    vx = in.nextInt();
    vy = in.nextInt();
    angle = in.nextInt();
  }

  public void copyFrom(State model) {
    this.checkpointIndex = model.checkpointIndex;
    this.x = model.x;
    this.y = model.y;
    this.vx = model.vx;
    this.vy = model.vy;
    this.angle = model.angle;
    this.finished = model.finished;
  }
  
  /*
   * Assume 
   *  angle will be [-18,+18]
   *  thrust will be [0,200]
   */
  public void apply(int angleOffset, int thrust) {
    this.angle += angleOffset;
    
    if (this.angle > 360) {
      this.angle -=360;
    } else if (this.angle < 0) {
      this.angle += 360;
    }
    double dirx = thrust * cosinuses[angle];
    double diry = thrust * sinuses[angle];
    
    x += this.vx + dirx;
    y += this.vy + diry;
    
    vx = (int) (0.85 * (this.vx + dirx));
    vy = (int) (0.85 * (this.vy + diry));
    
    
    int distToCp2 = (checkpointX[checkpointIndex] - x)*(checkpointX[checkpointIndex] - x) + 
        (checkpointY[checkpointIndex] - y)*(checkpointY[checkpointIndex] - y);
    
    if (distToCp2 < 600*600) {
      checkpointIndex++;
      if (checkpointIndex == checkpointsCount) {
        finished = true;
      }
    }
  }

  public void debug() {
    System.err.println("(x,y) = "+x+" "+y);
    System.err.println("(vx,vy) = "+vx+" "+vy);
    System.err.println("(angle) = "+angle);
  }
  
}
