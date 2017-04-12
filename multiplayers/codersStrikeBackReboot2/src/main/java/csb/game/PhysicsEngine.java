package csb.game;

import csb.entities.CheckPoint;
import csb.entities.Collision;
import csb.entities.Entity;
import csb.entities.Pod;
import csb.entities.Type;

public class PhysicsEngine {
  public Pod pods[];
  public CheckPoint checkPoints[];

  public boolean collisionSimualtion = true;
  /**
   * perfect simulation of the CG game Engine (w/r to movement, collision)
   * 
   * 
   * input : speed and direction of Pods are updated
   * output : new position of pods, direction, speed & new checkpoint updated
   */
  public void simulate() {
    Collision nextCollision;
    double t = 0.0;
    
    while(t < 1.0) {
      nextCollision = Entity.fakeCollision;
      for (int i=0;i<4;i++) {
          Pod pod = pods[i];

          nextCollision = podCheckPointCollision(nextCollision, t, pod); 
        
        if (collisionSimualtion) {
          nextCollision = podToPodCollision(nextCollision, t, i, pod);
        }
      }    

      if (nextCollision != Entity.fakeCollision) {
        double delta = nextCollision.t - t;
        for (int i=0;i<4;i++) {
          pods[i].move(delta);
        }
        t = nextCollision.t;
        if (nextCollision.b.type == Type.CHECKPOINT) {
          calculateNextCheckpoint(nextCollision);
        } else { /* POD */
          nextCollision.a.bounce(nextCollision.b);
        }
      } else {
        moveToEndOfSimulation(t);
        break;
      }
    }

    for (int i=0;i<4;i++) {
      pods[i].end();
    }
    // add timeout of the team of team leaders
    pods[0].team.timeout++;
    pods[2].team.timeout++;
  }
  private void moveToEndOfSimulation(double t) {
    double delta = 1.0 - t;
    for (Pod pod : pods) {
      pod.move(delta);
    }
  }
  private void calculateNextCheckpoint(Collision nextCollision) {
    Pod pod = (Pod) nextCollision.a;

    pod.team.timeout = 0;
    pod.nextCheckPointId++;
    if (pod.nextCheckPointId == checkPoints.length) {
      pod.nextCheckPointId = 0;
    }
    if (pod.nextCheckPointId == 1) {
      pod.lap++;
    }
  }
  private Collision podToPodCollision(Collision nextCollision, double t, int i, Pod pod) {
    Collision collision;
    for (int j=i+1;j<pods.length;j++) {
      collision = Entity.collision(pod, pods[j], t);
      if (collision != null && nextCollision.t > collision.t) {
        nextCollision = collision;
      }
    }
    return nextCollision;
  }
  private Collision podCheckPointCollision(Collision nextCollision, double t, Pod pod) {
    pod.radius = 1;// sale hack pour ne considerer que le centre du pod
    Collision collision = Entity.collision(pod, checkPoints[pod.nextCheckPointId], t);
    if (nextCollision.t > collision.t) {
      nextCollision = collision;
    }
    pod.radius = 400;
    return nextCollision;
  }
}
