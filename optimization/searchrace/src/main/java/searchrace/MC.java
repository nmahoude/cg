package searchrace;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MC {
  State currentState = new State();
  Random random = ThreadLocalRandom.current();
  
  AISolution solution = new AISolution();
  AISolution best = new AISolution();
  
  int bestAngle;
  int bestThrust;
  
  public void think(State originalState) {
    double bestScore = Double.NEGATIVE_INFINITY;
    bestAngle = 0;
    bestThrust = 0;
    int sims = 0;
    
    if (Player.turn > 1) {
      bestScore = reevaluateLastSolution(originalState, bestScore);
    }
    
    
    while(true) {
      sims ++;
      if ((sims & 1024-1) == 0 && System.currentTimeMillis() - Player.start > Player.MAX_TIME_TO_THINK) {
        break;
      }

      
      solution.createRandom();
      currentState.copyFrom(originalState);
      solution.applyOn(currentState);
      
      if (solution.aiScore > bestScore) {
        bestScore = solution.aiScore;
        best.copyFrom(solution);
      }
    }

    System.err.println("Sims : "+sims);
    bestAngle = best.angles[0];
    bestThrust = best.thrusts[0];
  }

  private double reevaluateLastSolution(State original, double bestScore) {
    solution.reinitFromLast(best);
    currentState.copyFrom(original);
    solution.applyOn(currentState);
    if (solution.aiScore > bestScore) {
      bestScore = solution.aiScore;
      best.copyFrom(solution);
    }
    return bestScore;
  }

  private double eval(State current, int lastCheckpoint) {
    double score = 0.0; 
    int distToCurrentCheckPoint = 
        (current.x - current.checkpointX[current.checkpointIndex])*(current.x - current.checkpointX[current.checkpointIndex])
        + (current.y - current.checkpointY[current.checkpointIndex])*(current.y - current.checkpointY[current.checkpointIndex])
        
        ;
    
      score += (- 0.000001 * distToCurrentCheckPoint );
      score += (1_000_000 * (current.checkpointIndex - lastCheckpoint ));
      
      if (current.checkpointIndex != lastCheckpoint) {
        double distToNextCheckPoint =Math.sqrt( 
            (current.x - State.checkpointX[current.checkpointIndex])*(current.x - State.checkpointX[current.checkpointIndex])
            + (current.y - State.checkpointY[current.checkpointIndex])*(current.y - State.checkpointY[current.checkpointIndex])
            )
            ;
        
        // 200 -> 0
        double speed = Math.sqrt(current.vx * current.vx + current.vy * current.vy);
        
        // 1 = same dir
        // -1 = opposite dir
        double directionToNextCheckpoint = 
                1.0 * ((State.checkpointX[current.checkpointIndex] - current.x) * current.vx
                + (State.checkpointY[current.checkpointIndex] - current.y) * current.vy)
            / (speed * distToNextCheckPoint) ;
        
        score += 1_000 * directionToNextCheckpoint;
      }
      return score;
  }
}
