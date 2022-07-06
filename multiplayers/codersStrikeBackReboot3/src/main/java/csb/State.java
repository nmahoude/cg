package csb;

import csb.entities.CheckPoint;
import csb.entities.Pod;
import csb.entities.Team;
import csb.game.PhysicsEngine;
import fast.read.FastReader;

public class State {
  public final PhysicsEngine physicsEngine = new PhysicsEngine();
  public static CheckPoint[] checkPoints;
  public static int laps;
  public static double lapLength;
  public static double cpLengths[];

  public Team[] teams = new Team[] { new Team(), new Team() };
  public Pod[] pods = new Pod[] { new Pod(0, teams[0]), new Pod(1, teams[0]), new Pod(0, teams[1]), new Pod(1, teams[1])};
  
  public State() {
  }
  
  public void readInit(FastReader in) {
    laps = in.nextInt();
    int checkpointCount = in.nextInt();
    System.err.println(String.format("^ %d %d", laps, checkpointCount));
    checkPoints = new CheckPoint[checkpointCount];
    for (int i = 0; i < checkpointCount; i++) {
      int checkpointX = in.nextInt();
      int checkpointY = in.nextInt();
      System.err.println(String.format("^ %d %d", checkpointX, checkpointY));
      checkPoints[i] = new CheckPoint(i, checkpointX, checkpointY);
    }
    
    calculateLapLength();

    physicsEngine.checkPoints = State.checkPoints;

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
  
  public void readTurn(FastReader in) {
    for (int i = 0; i < 4; i++) {
      int x = in.nextInt();
      int y = in.nextInt();
      int vx = in.nextInt();
      int vy = in.nextInt();
      int angle = in.nextInt();
      int nextCheckPointId = in.nextInt();
      pods[i].readInput(x, y, vx, vy, angle, nextCheckPointId);
    }
    backup();
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

  public void apply(double angle0, double thrust0, double angle1, double thrust1) {
    pods[0].apply(angle0, thrust0);
    pods[1].apply(angle1, thrust1);
    
    physicsEngine.simulate(checkPoints, pods);
  }

  public void copyFrom(State model) {
    teams[0].copyFrom(model.teams[0]);
    teams[1].copyFrom(model.teams[1]);
    
    pods[0].copyFrom(model.pods[0]);
    pods[1].copyFrom(model.pods[1]);
    pods[2].copyFrom(model.pods[2]);
    pods[3].copyFrom(model.pods[3]);
    
  }
}
