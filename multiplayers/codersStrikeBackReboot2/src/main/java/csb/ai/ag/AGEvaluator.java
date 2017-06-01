package csb.ai.ag;

import csb.GameState;
import csb.entities.CheckPoint;
import csb.entities.Pod;
import lib.trigo.VectorLib;

public class AGEvaluator {
  private static double PATIENCE_COEFF = 0.9;
  private static double[] patience = new double[AGSolution.DEPTH];
  static {
    for (int i=0;i<patience.length;i++) {
      patience[i] = Math.pow(PATIENCE_COEFF, i);
    }
  }
  
  GameState state;
  
  
  AGEvaluator(GameState state) {
    this.state = state;
  }

  public void evaluate(AGSolution sol, int depth) {
    double myRunnerDist = 3*state.lapLength - state.distToFinishLine(state.myRunner);
    double energy = 0.0
        // runner
        + 5000*myRunnerDist
        + 0.5*exitSpeedFeature(state.myRunner)
        - 100*(state.myRunner.shield > 0 ? 1.0 : 0.0)
        
        // blocker
        - 100*(state.myBlocker.shield > 0 ? 1.0 : 0.0)
        + 3.0 * distanceToCheckPointFeature(state.checkPoints[state.hisRunner.nextCheckPointId], state.myBlocker)
        + 1.0 * distance2ToPodFeature(state.myBlocker, state.hisRunner)
        - 1.0 * checkPointPassedFeature(state.hisRunner)
        + 0.5*distanceToCheckPointFeature(state.checkPoints[state.hisRunner.nextCheckPointId], state.hisRunner)
        + 3.0*distance2ToPodFeature(state.myRunner, state.hisBlocker)
        ;
    
    if (state.myRunner.lap >=3) {
      energy = Double.POSITIVE_INFINITY;
    }
    sol.energy += patience [depth] * energy;
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

  public void clear() {
    
  }
}
