package csb.game;

import csb.entities.CheckPoint;
import csb.entities.Collision;
import csb.entities.Pod;
import csb.entities.Type;

public class PhysicsEngine {
  public Pod pods[];
  public CheckPoint checkPoints[];

  public void play() {
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
}
