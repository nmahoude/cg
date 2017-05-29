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

  double lapLength;
  double cpLengths[];
  
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
      checkPoints[i] = new CheckPoint(i, checkpointX, checkpointY);
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
    double distPod0 = distToFinishLine(pods[0]);
    double distPod1 = distToFinishLine(pods[1]);
    if (distPod0 < distPod1) {
      myRunner = pods[0];
      myBlocker = pods[1];
    } else {
      myRunner = pods[1];
      myBlocker = pods[0];
    }
    
    double distPod2 = distToFinishLine(pods[2]);
    double distPod3 = distToFinishLine(pods[3]);
    if (distPod2 < distPod3) {
      hisRunner = pods[2];
      hisBlocker = pods[3];
    } else {
      hisRunner = pods[3];
      hisBlocker = pods[2];
    }
  }
  
  public double distToFinishLine(Pod pod) {
    double l = Math.max(0, (2 - pod.lap) * lapLength);
    
    l += VectorLib.length(pod.x-checkPoints[pod.nextCheckPointId].x, pod.y-checkPoints[pod.nextCheckPointId].y);
    if (pod.nextCheckPointId != 0) {
      for (int i=pod.nextCheckPointId;i<checkPoints.length;i++) {
        l+= cpLengths[i];
      }
    }
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
