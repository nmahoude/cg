package csb;

import csb.entities.CheckPoint;
import csb.entities.Pod;
import csb.entities.Team;
import csb.game.PhysicsEngine;
import fast.read.FastReader;
import trigonometry.Vector;

public class State {
  public final PhysicsEngine physicsEngine = new PhysicsEngine();
  public static CheckPoint[] checkPoints;
  public static int checkpointCount;
  
  public int turn;
  private int b_turn;

  public static int laps;
  public static double lapLength;
  public static double cpLengths[];

  public Team[] teams = new Team[] { new Team(), new Team() };
  public Pod[] pods = new Pod[] { new Pod(0, teams[0]), new Pod(1, teams[0]), new Pod(0, teams[1]), new Pod(1, teams[1])};
  
  public State() {
  }
  
  public void readInit(FastReader in) {
  	turn = 0;
    laps = in.nextInt();
    checkpointCount = in.nextInt();
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
    
    System.err.println("Lap length is "+lapLength);
  }
  
  private static final Vector DIR_X = new Vector(1,0);
	public void readTurn(FastReader in) {
  	turn++;
    debugStateToStdErr();

    for (int i = 0; i < 4; i++) {
      int x = in.nextInt();
      int y = in.nextInt();
      int vx = in.nextInt();
      int vy = in.nextInt();
      int angle = in.nextInt();
      int nextCheckPointId = in.nextInt();

      if (turn == 1 && i< 2) {
        // get the angle as it pleases us, it's first turn
        Vector dir = new Vector(State.checkPoints[1].x - x, State.checkPoints[1].y - y).normalize();
        angle = (int) (-Math.signum(dir.ortho().dot(DIR_X)) * Math.acos(dir.dot(DIR_X)) * 180 / Math.PI);
        if (angle < 0 ) angle += 360;
      }

      pods[i].readInput(x, y, vx, vy, angle, nextCheckPointId);

    }
    backup();
    
  }

  public void readState(FastReader in) {
  	this.turn = in.nextInt();
  	pods[0].lap = in.nextInt();
  	pods[1].lap = in.nextInt();
  	backup();
  }
  
  public void backup() {
  	b_turn = turn;
    teams[0].backup();
    teams[1].backup();
    
    pods[0].backup();
    pods[1].backup();
    pods[2].backup();
    pods[3].backup();
    
    // no need to backup checkpoints
  }

  public void restore() {
  	turn = b_turn;
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
    backup();
  }

	public void debugStateToStdErr() {
		System.err.println(String.format("^ %d %d %d ", this.turn, pods[0].lap, pods[1].lap));
	}
}
