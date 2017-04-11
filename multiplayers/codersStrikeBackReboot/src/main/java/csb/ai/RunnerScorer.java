package csb.ai;

import csb.entities.CheckPoint;
import csb.entities.Pod;
import trigonometry.Vector;

public class RunnerScorer implements AGScorer {
  private static final double MAX_DIST = 16_000.0 * 16_000.0;

  @Override
  public double score(Simulation simulation, Pod pod) {
    double score = 0;
    CheckPoint nextCheckPoint;

    CheckPoint[] checkPoints = simulation.engine.checkPoints;
    nextCheckPoint = checkPoints[pod.nextCheckPointId];

    if (pod.nextCheckPointId != pod.b_nextCheckPointId) {
      score += 1.0;
    }
    // bonus for distance
    int lastCheckPoint = pod.nextCheckPointId == 0 ? checkPoints.length-1 : pod.nextCheckPointId-1;
    int nextNextCheckPoint = pod.nextCheckPointId == checkPoints.length-1 ? 0 : pod.nextCheckPointId+1;
    Vector dir = checkPoints[nextNextCheckPoint].position.sub(checkPoints[lastCheckPoint].position).normalize().dot(CheckPoint.RADIUS);
        
    score += (MAX_DIST - Math.min(MAX_DIST, pod.position.squareDistance(nextCheckPoint.position.sub(dir)))) / MAX_DIST;

    return score;
  }

}
