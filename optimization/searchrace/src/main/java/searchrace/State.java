package searchrace;

import java.util.Scanner;

public class State {
  public static int checkpointX[];
  public static int checkpointY[];
  public static int distanceRemaining[];
  public static int distanceDone[];
  

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
  public double vx;
  public double vy;
  public int angle;
  public boolean finished;
  private static int checkpointsCount;

  public static void readInit(Scanner in) {
    checkpointsCount = in.nextInt();
    checkpointX = new int[checkpointsCount+2];
    checkpointY = new int[checkpointsCount+2];
    distanceRemaining = new int[checkpointsCount+2];
    distanceDone= new int[checkpointsCount+2];
    
    System.err.println("Nb of checkpoints : "+checkpointsCount);
    
    for (int i = 0; i < checkpointsCount; i++) {
      int cpX = in.nextInt(); // Position X
      int cpY = in.nextInt(); // Position Y
      checkpointX[i] = cpX;
      checkpointY[i] = cpY;
    }

    checkpointX[checkpointsCount] = checkpointX[checkpointsCount-1];
    checkpointY[checkpointsCount] = checkpointY[checkpointsCount-1];
    checkpointX[checkpointsCount+1] = checkpointX[checkpointsCount-1];
    checkpointY[checkpointsCount+1] = checkpointY[checkpointsCount-1];

    for (int i = checkpointsCount-1; i > 0 ; i--) {
      int dist = (int)Math.sqrt(
                (checkpointX[i] - checkpointX[i-1])*(checkpointX[i] - checkpointX[i-1])
                + (checkpointY[i] - checkpointY[i-1])*(checkpointY[i] - checkpointY[i-1])
                );
      distanceRemaining[i-1] = distanceRemaining[i] + dist;
    }
    
    for (int i = 1; i < checkpointsCount ; i++) {
      int dist = (int)Math.sqrt(
                (checkpointX[i] - checkpointX[i-1])*(checkpointX[i] - checkpointX[i-1])
                + (checkpointY[i] - checkpointY[i-1])*(checkpointY[i] - checkpointY[i-1])
                );
      distanceDone[i-1] = distanceDone[i] + dist;
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
  
  /**
   * SIMULATION 
   * 
   * 
   * @param angleOffset will be [-18,+18]
   * @param thrust will be [0,200]
   * @param debug 
   */
  public void apply(int angleOffset, int thrust) {
    if (finished) return;
    
    this.angle += angleOffset;
    
    if (this.angle > 360) {
      this.angle -=360;
    } else if (this.angle < 0) {
      this.angle += 360;
    }
    
    vx = vx + thrust * cosinuses[angle];
    vy = vy + thrust * sinuses[angle];
    
    boolean crossCheckPoint = false;
    do {
      crossCheckPoint = false;
      
      if (intersection()) {
        crossCheckPoint = true;
        checkpointIndex++;
        if (checkpointIndex == checkpointsCount) finished = true;
      }
    } while (!finished && crossCheckPoint);
    
    vx = (int) (0.85 * vx);
    vy = (int) (0.85 * vy);
    this.x = (int)(this.x + this.vx);
    this.y = (int)(this.y + this.vy);
    
  }

  private boolean intersection() {
    double ux = checkpointX[checkpointIndex];
    double uy = checkpointY[checkpointIndex];
    
    
    double x2 = x - ux;
    double y2 = y - uy;
    double r2 = 600;

    double a = vx * vx + vy * vy;

    double b = 2.0 * (x2 * vx + y2 * vy);
    double c = x2 * x2 + y2 * y2 - r2 * r2;
    double delta = b * b - 4.0 * a * c;

    if (delta < 0.0) {
        return false;
    }

    double t1 = (-b - Math.sqrt(delta)) / (2.0 * a);
    double t2 = (-b + Math.sqrt(delta)) / (2.0 * a);

    
    boolean collision = false;
    if (t1 >= 0.0 && t1 <= 1.0) {
      collision = true;
    }

    if (t2 >= 0.0 && t2 <= 1.0) {
      collision = true;
    }
    return collision;
  }

  public void debug() {
    System.err.println("(x,y) = "+x+" "+y);
    System.err.println("(vx,vy) = "+vx+" "+vy);
    System.err.println("(angle) = "+angle );
    System.err.println("Angle to next checkpoint : "+angleToNextCheckpoint());
  }
  
  
  public double angleToNextCheckpoint() {
    return 1.0 * (
        (State.checkpointX[checkpointIndex] - x) * State.cosinuses[angle] 
      + (State.checkpointY[checkpointIndex] - y) * State.sinuses[angle] 
    ) / (distToNextCheckPoint() );
  }

  private double distToNextCheckPoint() {
    return Math.sqrt( 
        (x - State.checkpointX[checkpointIndex])*(x - State.checkpointX[checkpointIndex])
        + (y - State.checkpointY[checkpointIndex])*(y - State.checkpointY[checkpointIndex])
        )
        ;
  }
}
