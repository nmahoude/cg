package csb.simulation;

import java.util.Random;

import csb.entities.CheckPoint;
import csb.entities.Collision;
import csb.entities.Pod;
import csb.entities.Type;
import trigonometry.Vector;

public class AGSolution {
  private static final double MAX_DIST = 16_000.0 * 16_000.0;
  static Random random = new Random();
  private static final int ACTION_SIZE = 10;
  public Action[] actions0 = new Action[ACTION_SIZE];
  public Action[] actions1 = new Action[ACTION_SIZE];
  
  public Pod pods[];
  CheckPoint checkPoints[];
  
  public double score1;
  public double score2;
  
  public AGSolution(Pod pods[],CheckPoint checkpoints[]) {
    this.pods = pods;
    this.checkPoints = checkpoints;
  }

  public void random() {
    for (int i=0;i<ACTION_SIZE;i++) {
      actions0[i] = new Action();
      actions0[i].angle = (0.5 - random.nextDouble())* 2 *  Math.PI / 10;
      actions0[i].thrust = 100; //0 + 1+random.nextInt(100); // TODO review the distribution toward 100 !
      
      actions1[i] = new Action();
      actions1[i].angle = (0.5 - random.nextDouble())* 2 * Math.PI / 10;
      actions1[i].thrust = 100;//0 + 1+random.nextInt(100); // TODO review the distribution toward 100 !
    }
  }
  
  public void zero() {
    for (int i=0;i<ACTION_SIZE;i++) {
      actions0[i] = new Action();
      actions0[i].angle = (0.5 - random.nextDouble())* 2 *  Math.PI / 10;
      actions0[i].thrust = 75 + 1+random.nextInt(25); // TODO review the distribution toward 100 !
      
      actions1[i] = new Action();
      actions1[i].angle = (0.5 - random.nextDouble())* 2 * Math.PI / 10;
      actions1[i].thrust = 75 + 1+random.nextInt(25); // TODO review the distribution toward 100 !
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
      play();
      if (i == 0) {
        //score += 0.1 * evaluate();
      }
    }
    
    score += 1.0*evaluate();
    return score;
  }

  private void applyMoves(int i) {
    Pod pod0 = pods[0];
    Vector direction0 = pod0.direction.rotate(actions0[i].angle);
    pod0.apply(direction0, actions0[i].thrust);
    
    if (pods.length > 1) {
      Pod pod1 = pods[1];
      Vector direction1 = pod1.direction.rotate(actions1[i].angle);
      pod1.apply(direction1, actions1[i].thrust);
    }
  }
  
  private void play() {
    // get collision
    Collision nextCollision = null, collision;
    double t = 0.0;
    
    while(t <1.0) {
      nextCollision = null;
      for (int i=0;i<pods.length;i++) {
          Pod pod = pods[i];
          pod.radius = 1;// sale hack pour ne considerer que le centre du pod
          collision = pod.collision(checkPoints[pod.nextCheckPointId], t);
          if (collision != null && (nextCollision == null || nextCollision.t > collision.t)) {
            nextCollision = collision;
          }
          pod.radius = 400; 
        
        for (int j=i+1;j<pods.length;j++) {
          collision = pod.collision(pods[j], t);
          if (collision != null && (nextCollision == null || nextCollision.t > collision.t)) {
            nextCollision = collision;
          }
        }
      }    

      if (nextCollision != null) {
        double delta = nextCollision.t - t;
        for (Pod pod : pods) {
          pod.move(delta);
        }
        t = nextCollision.t;
        if (nextCollision.b.type == Type.CHECKPOINT) {
          Pod pod = (Pod) nextCollision.a;
          pod.nextCheckPointId++;
          pod.timeout = 100;
          if (pod.nextCheckPointId == checkPoints.length) {
            pod.nextCheckPointId = 0;
          }
        } else { /* POD */
          nextCollision.a.bounce(nextCollision.b);
        }
      } else {
        double delta = 1.0 - t;
        for (Pod pod : pods) {
          pod.move(delta);
        }
        break;
      }
    }

    for (Pod pod : pods) {
      pod.end();
    }
  }

  private double evaluate() {
    score1 = 0;
    score2 = 0;
    Pod pod;
    CheckPoint nextCheckPoint;

    int i = 0;
    pod = pods[i];
    nextCheckPoint = checkPoints[pods[0].nextCheckPointId];

    if (pod.nextCheckPointId != pod.b_nextCheckPointId) {
      score1 += 3.0;

    }
    // bonus for distance
    score1 += (MAX_DIST - Math.min(MAX_DIST, pod.position.squareDistance(nextCheckPoint.position))) / MAX_DIST;
    // bonus to facing next checkpoint
    double angle = pod.speed.normalize().dot(nextCheckPoint.position.sub(pod.position).normalize());
    score1 += angle;

    if (pods.length == 1) return score1;
    
    i = 1;
    nextCheckPoint = checkPoints[pods[i].nextCheckPointId];
    pod = pods[i];
    if (pod.nextCheckPointId != pod.b_nextCheckPointId) {
      score2 += 3.0;

    }
    score2 += (MAX_DIST - Math.min(MAX_DIST, pod.position.squareDistance(nextCheckPoint.position))) / MAX_DIST;

    angle = Math.acos(pod.direction.dot(nextCheckPoint.position.sub(pod.position).normalize()));
    score2 += 1.0 * (Math.PI - angle) / Math.PI;
    
    return score1 + score2;
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
    return ""+(int)(pod.position.x+3000*direction.vx)+" "+(int)(pod.position.y+ 3000*direction.vy)+" "+(int)action.thrust;
  }

}
