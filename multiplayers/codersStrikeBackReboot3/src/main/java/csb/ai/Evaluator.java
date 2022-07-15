package csb.ai;

import csb.State;
import csb.VectorLib;
import csb.entities.CheckPoint;
import csb.entities.Pod;

public class Evaluator {
  
  public double evaluate(State state, AGSolution sol) {
    
    Pod myRunner   = state.pods[0];
    Pod myBlocker  = state.pods[1];
    Pod hisRunner  = state.pods[2];
    Pod hisBlocker = state.pods[3];
    
    
    double myRunnerDist = (3*State.lapLength - distToFinishLine(myRunner)) / State.lapLength;
    double myBlockerDist = (3*State.lapLength - distToFinishLine(myBlocker)) / State.lapLength;
    
    
    int myRunnerCheckpoints = myRunner.lap * (State.checkpointCount) + (myRunner.nextCheckPointId == 0 ? State.checkpointCount : myRunner.nextCheckPointId);
    int myBlockerCheckpoints = myBlocker.lap * (State.checkpointCount) + (myBlocker.nextCheckPointId  == 0 ? State.checkpointCount : myBlocker.nextCheckPointId);
    
    
    
		double runnerCoeff;
		double blockerCoeff;
		if (myRunnerCheckpoints > myBlockerCheckpoints) {
			runnerCoeff = 0.9;
			blockerCoeff = 0.1;
		} else if (myRunnerCheckpoints < myBlockerCheckpoints) {
			runnerCoeff = 0.1;
			blockerCoeff = 0.9;
		} else {
			runnerCoeff = 0.5;
			blockerCoeff = 0.5;
		}
		
		
		
		double energy = 0.0
        // runner
				+ runnerCoeff * (
	    		+ 5_000.0 * myRunnerCheckpoints
	        + -10.0 * distanceToCheckPoint(myRunner)
	        + 2.0 * directionToNextCheckpointScore(myRunner)
        )
				+ blockerCoeff * (
	    		+ 5_000.0 * myBlockerCheckpoints
	    		+ -10.0 * distanceToCheckPoint(myBlocker)
	        + 2.0 * directionToNextCheckpointScore(myBlocker)
    		)
    		;
    
    
    
    return energy;
  }
  
  private double distanceToCheckPoint(Pod myRunner) {
  	CheckPoint cp = State.checkPoints[myRunner.nextCheckPointId];
  	double length;
  	if (myRunner.nextCheckPointId == 0) {
  		length = State.cpLengths[State.checkpointCount-1];
  	} else {
  		length = State.cpLengths[myRunner.nextCheckPointId-1];
  	}
  	
  	return Math.sqrt( (cp.x - myRunner.x) * (cp.x - myRunner.x) + (cp.y - myRunner.y)*(cp.y - myRunner.y)) / length;  
	}

	private double speedOf(Pod myRunner) {
		return Math.sqrt(myRunner.vx*myRunner.vx + myRunner.vy*myRunner.vy);
	}

	private double directionToNextCheckpointScore(Pod pod) {
  	CheckPoint nextCP = State.checkPoints[pod.nextCheckPointId];
  	double dist = Math.sqrt((nextCP.x - pod.x)*(nextCP.x - pod.x) + (nextCP.y - pod.y) * (nextCP.y - pod.y));
  	double speed = Math.sqrt(pod.vx*pod.vx + pod.vy*pod.vy);
  	if (speed == 0) speed = 1.0;
  	if (dist == 0) dist = 1.0;
  	
  	return ((nextCP.x - pod.x) * pod.vx + (nextCP.y - pod.y) * pod.vy) / (dist * speed); 
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
  
  public static double distToFinishLine(Pod pod) {
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
