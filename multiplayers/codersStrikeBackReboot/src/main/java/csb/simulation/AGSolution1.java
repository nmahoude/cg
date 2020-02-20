package csb.simulation;

import java.util.Random;

import csb.entities.CheckPoint;
import csb.entities.Pod;
import csb.game.PhysicsEngine;
import trigonometry.Point;
import trigonometry.Vector;

public class AGSolution1 {
  private static final double MAX_DIST = 16_000.0 * 16_000.0;
  static Random random = new Random();
  public static final int ACTION_SIZE = 15;
  public Action[] actions0 = new Action[ACTION_SIZE];
  public Action[] actions1 = new Action[ACTION_SIZE];
  public Action[] actions2 = new Action[ACTION_SIZE];
  public Action[] actions3 = new Action[ACTION_SIZE];
  
  public Point[] points = new Point[ACTION_SIZE];
  
  public Pod pods[];
  CheckPoint checkPoints[];

  static PhysicsEngine physics = new PhysicsEngine();
  
  private double score1;
  private double score2;
  
  public AGSolution1(Pod pods[],CheckPoint checkpoints[]) {
    this.pods = pods;
    this.checkPoints = checkpoints;
  
    physics.pods = pods;
    physics.checkPoints = checkpoints;
  }

  public void random() {
    for (int i=0;i<ACTION_SIZE;i++) {
      actions0[i] = new Action();
      actions0[i].angle = (0.5 - random.nextDouble())* 2 *  Math.PI / 10;
      actions0[i].thrust = 1.0;
      
      actions1[i] = new Action();
      actions1[i].angle = (0.5 - random.nextDouble())* 2 * Math.PI / 10;
      actions1[i].thrust = 1.0;
    }
  }
  
  public void test() {
    for (int i=0;i<ACTION_SIZE;i++) {
      actions0[i] = new Action();
      double rand = 2*(0.5-random.nextDouble());
      
      actions0[i].angle = rand *  Math.PI / 10;
      actions0[i].thrust = random.nextDouble();
      
      actions1[i] = new Action();
      actions1[i].angle = (0.5 - random.nextDouble())* 2 * Math.PI / 10;
      actions1[i].thrust = random.nextDouble();
    }
  }

  public void zero() {
    for (int i=0;i<ACTION_SIZE;i++) {
      actions0[i] = new Action();
      actions0[i].angle = (0.5 - random.nextDouble())* 2 *  Math.PI / 10;
      actions0[i].thrust = 1.0;
      
      actions1[i] = new Action();
      actions1[i].angle = (0.5 - random.nextDouble())* 2 * Math.PI / 10;
      actions1[i].thrust = 1.0;
    }
  }

  public double score() {
    double score = moveAndEvaluate();
    reset();
    return score;
  }

  public double moveAndEvaluate() {
    double score = 0;
    
    for (int i=0;i<ACTION_SIZE;i++) {
      applyMoves(i);
      
      physics.simulate();
      
      points[i] = pods[0].position;

      if (i == 0) {
        //score += 0.1 * evaluate();
      }
    }
    
    score += 1.0*evaluate();
    return score;
  }

  private int thrustGeneToThrust(double geneValue) {
    double thrust = 100;
    if (geneValue < 0.2) {
      thrust = geneValue * 500;
    }
    return (int)thrust;
  }
  
  private void applyMoves(int i) {
    Pod pod0 = pods[0];
    
    Vector direction0 = pod0.direction.rotate(actions0[i].angle);
    pod0.apply(direction0, thrustGeneToThrust(actions0[i].thrust));
    
    if (pods.length == 1) return;

    Pod pod1 = pods[1];
    Vector direction1 = pod1.direction.rotate(actions1[i].angle);
    pod1.apply(direction1, thrustGeneToThrust(actions1[i].thrust));
    
    // simulate pod 2 & 3
    pods[2].apply(checkPoints[pods[2].nextCheckPointId].position, 100);
    pods[3].apply(checkPoints[pods[3].nextCheckPointId].position, 100);
  }
  
  private double evaluate() {
    double angle ;
    
    score1 = 0;
    score2 = 0;
    Pod pod;
    CheckPoint nextCheckPoint;

    int i = 0;
    pod = pods[i];
    nextCheckPoint = checkPoints[pod.nextCheckPointId];

    if (pod.nextCheckPointId != pod.b_nextCheckPointId) {
      score1 += 1.0;
      //score1 += 5*pod.speed.normalize().dot(checkPoints[pod.nextCheckPointId].position.sub(pod.position).normalize());
    }
    // bonus for distance
    int lastCheckPoint = pod.nextCheckPointId == 0 ? checkPoints.length-1 : pod.nextCheckPointId-1;
    int nextNextCheckPoint = pod.nextCheckPointId == checkPoints.length-1 ? 0 : pod.nextCheckPointId+1;
    Vector dir = checkPoints[nextNextCheckPoint].position.sub(checkPoints[lastCheckPoint].position).normalize().dot(CheckPoint.RADIUS);
        
    score1 += (MAX_DIST - Math.min(MAX_DIST, pod.position.squareDistance(nextCheckPoint.position.sub(dir)))) / MAX_DIST;
    // bonus to facing next checkpoint
    //angle = pod.speed.normalize().dot(nextCheckPoint.position.sub(pod.position).normalize());
    //score1 += angle;

    if (pods.length == 1) return score1;
    
    i = 1;
    pod = pods[i];
    nextCheckPoint = checkPoints[pod.nextCheckPointId];
    if (pod.nextCheckPointId != pod.b_nextCheckPointId) {
      score2 += 1.0;
    }
    score2 += (MAX_DIST - Math.min(MAX_DIST, pod.position.squareDistance(nextCheckPoint.position))) / MAX_DIST;

    //angle = Math.acos(pod.direction.dot(nextCheckPoint.position.sub(pod.position).normalize()));
    //score2 += 1.0 * (Math.PI - angle) / Math.PI;
    
    return 1*score1  + 0.1*score2;
  }
  public void reset() {
    for (Pod pod : pods) {
      pod.restore();
    }
  }

  public String actionOutput(int i) {
    Pod pod;
    Action action;
    if (i == 0) {
      pod = pods[0];
      action = actions0[0];
    } else {
      pod = pods[1];
      action = actions1[0];
    }
    
    Vector direction = pod.direction.rotate(action.angle);
    return ""+(int)(pod.position.x+3000*direction.vx)
         +" "+(int)(pod.position.y+ 3000*direction.vy)
         +" "+(int)thrustGeneToThrust(action.thrust);
  }

  public static AGSolution1 getBest(Pod[] pods, CheckPoint[] checkPoints) {
    AGSolution1 best = null;
    double bestScore = Double.NEGATIVE_INFINITY;
    for (int i=0;i<8_000;i++) {
      AGSolution1 solution = new AGSolution1(pods, checkPoints);
      solution.zero();
      double score = solution.score();
      if( score > bestScore) {
        bestScore = score;
        best = solution;
      }
    }
    return best;
  }

}
