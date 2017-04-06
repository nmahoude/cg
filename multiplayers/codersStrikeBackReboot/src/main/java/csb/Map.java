package csb;

import java.util.Scanner;

import csb.entities.CheckPoint;
import csb.entities.Pod;

public class Map {
  public CheckPoint[] checkPoints;

  public Pod[] pods = new Pod[4];
  public Pod[] myPods = new Pod[2];
  public Pod[] hisPods = new Pod[2];
  
  public Map() {
    myPods[0] = pods[0] = new Pod();
    myPods[1] = pods[1] = new Pod();
    hisPods[0] = pods[2] = new Pod();
    hisPods[1] = pods[3] = new Pod();
  }

  public void readCheckpoints(Scanner in) {
    int checkpointCount = in.nextInt();
    checkPoints = new CheckPoint[checkpointCount];
    for (int i = 0; i < checkpointCount; i++) {
      int checkpointX = in.nextInt();
      int checkpointY = in.nextInt();
      checkPoints[i] = new CheckPoint(checkpointX, checkpointY);
    }
  }
}
