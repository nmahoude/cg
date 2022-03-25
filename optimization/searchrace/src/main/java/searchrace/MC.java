package searchrace;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MC {
  private static final int DEPTH = 10;
  State current = new State();
  Random random = ThreadLocalRandom.current();
  
  int angles[] = new int[100];
  int thrusts[] = new int[100];
  
  int bestAngle;
  int bestThrust;
  
  private static double[] depthFactor = new double[DEPTH];
  static {
    depthFactor[0] = 1.0;
    for (int i=1;i<DEPTH;i++) {
      depthFactor[i] = 0.9 * depthFactor[i-1];
    }
  }
  
  public void think(State original) {
    double bestScore = Double.NEGATIVE_INFINITY;
    bestAngle = 0;
    bestThrust = 0;
    int sims = 0;
    
    while(true) {
      sims ++;
      if ((sims & 1024-1) == 0 && System.currentTimeMillis() - Player.start > 45) {
        break;
      }
      current.copyFrom(original);
      
      double score = 0.0;
      for (int t=0;t<DEPTH;t++) {
        int lastCheckpoint = current.checkpointIndex;
        if (random.nextDouble() > 0.5) {
          angles[t] = random.nextInt(11) - 5;
        } else {
          angles[t] = random.nextInt(36+1) - 18;
        }
        
        if (random.nextDouble() > 0.7) {
          thrusts[t] = 200;
        } else {
          thrusts[t] = random.nextInt(201);
        }
        current.apply(angles[t], thrusts[t]);
      
        int distToCurrentCheckPoint = 
          (current.x - current.checkpointX[current.checkpointIndex])*(current.x - current.checkpointX[current.checkpointIndex])
          + (current.y - current.checkpointY[current.checkpointIndex])*(current.y - current.checkpointY[current.checkpointIndex])
          
          ;
      
        score += depthFactor[t] * (- 0.000001 * distToCurrentCheckPoint );
        score += depthFactor[t] * (1_000_000 * (current.checkpointIndex - lastCheckpoint ));
        
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
          
          score += depthFactor[t] * 1_000 * directionToNextCheckpoint;
        }

      }
      if (score > bestScore) {
        bestScore = score;
        bestAngle = angles[0];
        bestThrust = thrusts[0];
      }
    }
    
    System.err.println("Sims : "+sims);
  }
}
