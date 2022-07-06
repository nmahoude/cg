package csb.ai;

import csb.State;
import csb.VectorLib;
import csb.entities.CheckPoint;
import csb.entities.Pod;

public class Evaluator {
  private static double PATIENCE_COEFF = 0.9;
  private static double[] patience = new double[AGSolution.DEPTH];
  static {
    for (int i=0;i<patience.length;i++) {
      patience[i] = Math.pow(PATIENCE_COEFF, i);
    }
  }
  
  public double evaluate(State state, AGSolution sol, int depth) {
    
    Pod myRunner   = state.pods[0];
    Pod myBlocker  = state.pods[1];
    Pod hisRunner  = state.pods[2];
    Pod hisBlocker = state.pods[3];
    
    
    double myRunnerDist = 3*State.lapLength - distToFinishLine(myRunner);
    double energy = 0.0
        // runner
        + 5000*myRunnerDist
        + 0.5*exitSpeedFeature(myRunner)
        - 100*(myRunner.shield > 0 ? 1.0 : 0.0)
        
        // blocker
        - 100*(myBlocker.shield > 0 ? 1.0 : 0.0)
        + 3.0 * distanceToCheckPointFeature(state.checkPoints[hisRunner.nextCheckPointId], myBlocker)
        + 1.0 * distance2ToPodFeature(myBlocker, hisRunner)
        - 1.0 * checkPointPassedFeature(hisRunner)
        + 0.5*distanceToCheckPointFeature(state.checkPoints[hisRunner.nextCheckPointId], hisRunner)
        + 3.0*distance2ToPodFeature(myRunner, hisBlocker)
        ;
    
    if (myRunner.lap >=3) {
      energy = 1_000_000;
    }
    return patience [depth] * energy;
  }
  
  private double distance2ToPodFeature(Pod pod, Pod target) {
    final int MAX_DIST = 16000*16000+9000*9000;
    double dist2 = VectorLib.distance2(pod.x-target.x, pod.y-target.y);
    return (MAX_DIST - Math.min(MAX_DIST, dist2)) / MAX_DIST;
  }

  private double exitSpeedFeature(Pod pod) {
    if (pod.nextCheckPointId == pod.b_nextCheckPointId) return 0.0;
    
    final int MAX_SPEED = 600;
    double speed = VectorLib.length(pod.vx, pod.vy) / MAX_SPEED;
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
  private double distanceToCheckPointFeature(CheckPoint nextCheckPoint, Pod pod) {
    final int MAX_DIST = 16000*16000+9000*9000;
    double dist2 = VectorLib.distance2(pod.x-nextCheckPoint.x, pod.y-nextCheckPoint.y) - CheckPoint.RADIUS*CheckPoint.RADIUS;
    return (MAX_DIST - Math.min(MAX_DIST, dist2)) / MAX_DIST;
  }
  
  public double distToFinishLine(Pod pod) {
    double l = Math.max(0, (2 - pod.lap) * State.lapLength);
    
    l += VectorLib.length(pod.x-State.checkPoints[pod.nextCheckPointId].x, pod.y-State.checkPoints[pod.nextCheckPointId].y);
    if (pod.nextCheckPointId != 0) {
      for (int i=pod.nextCheckPointId;i<State.checkPoints.length;i++) {
        l+= State.cpLengths[i];
      }
    }
    return l;
  }

}
