package csb;

import java.util.Scanner;

import csb.entities.CheckPoint;
import csb.entities.Pod;

public class GameState {
  public CheckPoint[] checkPoints;
  public Pod[] pods = new Pod[4];
  private int lapLength;
  
  public GameState() {
    pods[0] = new Pod(0);
    pods[1] = new Pod(1);
    pods[2] = new Pod(2);
    pods[3] = new Pod(3);
  }

  public void readCheckpoints(Scanner in) {
    int checkpointCount = in.nextInt();
    checkPoints = new CheckPoint[checkpointCount];
    for (int i = 0; i < checkpointCount; i++) {
      int checkpointX = in.nextInt();
      int checkpointY = in.nextInt();
      checkPoints[i] = new CheckPoint(4+i, checkpointX, checkpointY);
    }
    calculateLapLength();
  }

  private void calculateLapLength() {
    double tempDistance=0;
    for (int i=0;i<checkPoints.length;i++) {
      CheckPoint p1 = checkPoints[i];
      CheckPoint p2 = checkPoints[(i+1)%checkPoints.length];
      tempDistance += p1.position.sub(p2.position).length();
    }
    lapLength = (int)tempDistance;
  }

  public void backup() {
    pods[0].backup();
    pods[1].backup();
    pods[2].backup();
    pods[3].backup();
    
    // no need to backup checkpoints
  }

}
