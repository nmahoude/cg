package csb;

import java.util.Scanner;

import csb.entities.CheckPoint;
import csb.entities.Pod;
import lib.trigo.VectorLib;

public class GameState {
  public CheckPoint[] checkPoints;

  public Team[] teams = new Team[2];
  public Pod[] pods = new Pod[4];
  int timeouts[] = new int[2];
  int b_timeouts[] = new int[2];
  
  public Pod myRunner = pods[0];
  public Pod myBlocker = pods[1];
  public Pod hisRunner = pods[2];
  public Pod hisBlocker = pods[3];

  private int lapLength;
  private double cpLengths[];
  
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
    lapLength = 0;
    cpLengths = new double[checkPoints.length];
    
    for (int i=0;i<checkPoints.length;i++) {
      CheckPoint p1 = checkPoints[i];
      CheckPoint p2 = checkPoints[(i+1)%checkPoints.length];
      double length = VectorLib.length(p2.x-p1.x, p2.y-p1.y);
      cpLengths[i] = length;
      lapLength += length;
    }
  }

  private void updateRunnersBlockers() {
  myRunner = pods[0];
  myBlocker = pods[1];
  hisRunner = pods[2];
  hisBlocker = pods[3];
    
//    double l1 = distToEndOfRace(pods[0]);
//    double l2 = distToEndOfRace(pods[1]);
//    if (l1 < l2) {
//      myRunner = pods[0];
//      myBlocker = pods[1];
//    } else {
//      myRunner = pods[1];
//      myBlocker = pods[0];
//    }
//    
//    double l3 = distToEndOfRace(pods[2]);
//    double l4 = distToEndOfRace(pods[3]);
//    if (l3 < l4) {
//      hisRunner = pods[2];
//      hisBlocker = pods[3];
//    } else {
//      hisRunner = pods[3];
//      hisBlocker = pods[2];
//    }
  }
  
  private double distToEndOfRace(Pod pod) {
    double l = (3 - pod.lap) * lapLength;  
    l += VectorLib.length(pod.x-checkPoints[pod.nextCheckPointId].x, pod.y-checkPoints[pod.nextCheckPointId].y);
    for (int i=pod.nextCheckPointId;i<checkPoints.length;i++) {
      l+= cpLengths[i];
    }
    System.err.println("dist to end for pod "+pod.id+" : "+l);
    return l;
  }

  public void backup() {
    teams[0].backup();
    teams[1].backup();
    
    pods[0].backup();
    pods[1].backup();
    pods[2].backup();
    pods[3].backup();
    
    updateRunnersBlockers();
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

  public void setCheckPoints(CheckPoint[] checkPoints2) {
    this.checkPoints = checkPoints2;
    calculateLapLength();
  }

}
