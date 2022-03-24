package searchrace;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MC {
  State current = new State();
  Random random = ThreadLocalRandom.current();
  
  int angles[] = new int[100];
  int thrusts[] = new int[100];
  
  int bestAngle;
  int bestThrust;
  
  public void think(State original) {
    double bestScore = Double.NEGATIVE_INFINITY;
    bestAngle = 0;
    bestThrust = 0;
    int sims = 0;
    
    while(true) {
      sims ++;
      if ((sims & 1024-1) == 0 && System.currentTimeMillis() - Player.start > 40) {
        break;
      }
      current.copyFrom(original);
      
      for (int t=0;t<10;t++) {
        angles[t] = random.nextInt(36+1) - 18;
        
        if (random.nextDouble() > 0.7) {
          thrusts[t] = 200;
        } else {
          thrusts[t] = random.nextInt(201);
        }
        current.apply(angles[t], thrusts[t]);
      }
      int distToCurrentCheckPoint = 
          (current.x - current.checkpointX[current.checkpointIndex])*(current.x - current.checkpointX[current.checkpointIndex])
          + (current.y - current.checkpointY[current.checkpointIndex])*(current.y - current.checkpointY[current.checkpointIndex])
          
          ;
      
      double score = - 0.000001 * distToCurrentCheckPoint ;
      score += 1_000_000 * (current.checkpointIndex - original.checkpointIndex );

      if (score > bestScore) {
        bestScore = score;
        bestAngle = angles[0];
        bestThrust = thrusts[0];
      }
    }
    
    System.err.println("Sims : "+sims);
  }
}
