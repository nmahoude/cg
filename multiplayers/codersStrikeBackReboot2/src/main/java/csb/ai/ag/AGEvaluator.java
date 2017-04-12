package csb.ai.ag;

import csb.GameState;
import csb.entities.CheckPoint;
import csb.entities.Pod;

public class AGEvaluator {
  GameState state;
  
  AGEvaluator(GameState state) {
    this.state = state;
  }

  public void evaluate(AGSolution sol) {
    Pod pod0 = state.pods[0];
    Pod pod1 = state.pods[1];
    
    sol.energy = 0.0
        + 10.0*checkPointPassedFeature(pod0)
        + 5.0*distanceToCheckPointFeature(pod0)
        + 0.4*speedFeature(pod0);
        ;
  }

  private double speedFeature(Pod pod) {
    final int MAX_SPEED = 600;
    double speed = pod.speed.length() / MAX_SPEED;
    return speed;
  }

  /**
   * has the pod passed through a checkpoint (only one), [0;1]
   * 1 is yes, 0 is false
   */
  private double checkPointPassedFeature(Pod pod) {
    return pod.nextCheckPointId != pod.b_nextCheckPointId ? 1.0 : 0.0;
  }
  /**
   * distance to next checkpoint score : [0;1]
   * 0 is far
   * 1 is on
   */
  private double distanceToCheckPointFeature(Pod pod) {
    final int MAX_DIST = 16000*16000+9000*9000;
    CheckPoint nextCheckPoint = state.checkPoints[pod.nextCheckPointId];
    return (MAX_DIST - Math.min(MAX_DIST, pod.position.squareDistance(nextCheckPoint.position))) / MAX_DIST;
  }
}
