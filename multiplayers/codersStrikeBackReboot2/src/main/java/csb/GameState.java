package csb;

import java.util.Scanner;

import csb.entities.CheckPoint;
import csb.entities.Pod;

public class GameState {
  public CheckPoint[] checkPoints;

  public Team[] teams = new Team[2];
  public Pod[] pods = new Pod[4];
  int timeouts[] = new int[2];
  int b_timeouts[] = new int[2];
  
  private int lapLength;
  
  public GameState() {
    teams[0] = new Team();
    teams[1] = new Team();
    
    pods[0] = new Pod(0, teams[0]);
    pods[1] = new Pod(1, teams[0]);
    pods[2] = new Pod(2, teams[1]);
    pods[3] = new Pod(3, teams[1]);
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
    teams[0].backup();
    teams[1].backup();
    
    pods[0].backup();
    pods[1].backup();
    pods[2].backup();
    pods[3].backup();
    
    // no need to backup checkpoints
  }

  public void restore() {
    teams[0].restore();
    teams[1].restore();

    pods[0].restore();
    pods[1].restore();
    pods[2].restore();
    pods[3].restore();

  }

}
