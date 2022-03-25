package searchrace.ag;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import searchrace.Player;
import searchrace.State;

public class AG {
  private static final int POPULATION_BEST_POOL = 20;
  private static final int POPULATION_POOL_TOTAL = 50;
  
  
  private static final int MAX_SPEED = 200;
  
  private static final Random random = ThreadLocalRandom.current();
  private static final State work = new State();
  
  public int bestAngle;
  public int bestThrust;

  AGSolution best = new AGSolution();
  AGSolution[] population = new AGSolution[POPULATION_POOL_TOTAL];
  
  public AG() {
    for (int i=0;i<POPULATION_POOL_TOTAL;i++) {
      population[i] = new AGSolution();
    }
  }
  
  
  public void think(State state) {
    best.updateScore(Double.NEGATIVE_INFINITY);
    
    
    initPopulation(state);
    Arrays.sort(population, (s1, s2) -> Double.compare(s2.score, s1.score));
    if (population[0].score > best.score) {
      best.copyFrom(population[0]);
    }

    int sims = 0;
    
end:
  while(true) {
      for (int p=POPULATION_BEST_POOL;p<POPULATION_POOL_TOTAL;p++) {
        sims ++;
        if ((sims & 1024-1) == 0 && System.currentTimeMillis() - Player.start > 40) {
          break end;
        }
        int p1 = random.nextInt(POPULATION_BEST_POOL);
        int p2 = random.nextInt(POPULATION_BEST_POOL);
  
        population[p].merge(population[0], population[p2]);
        work.copyFrom(state);
        population[p].apply(work);
        population[p].updateScore(eval(state, work));
        
        if (population[p].score > best.score) {
          best.copyFrom(population[p]);
        }
      }
      Arrays.sort(population, (s1, s2) -> Double.compare(s2.score, s1.score));
    }
    
    System.err.println("Sims : "+(sims));
    bestAngle = best.angles[0];
    bestThrust = best.thrusts[0];
  }

  private void initPopulation(State original) {
    population[0].copyFrom(best);
    population[0].decal();
    
    for (int i=1;i<POPULATION_POOL_TOTAL;i++) {
      work.copyFrom(original);
      population[i].pseudoRandom();
      population[i].apply(work);
      population[i].updateScore(eval(original, work));
    }
  }


  private double eval(State original, State current) {
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
            ((State.checkpointX[current.checkpointIndex] - current.x) * current.vx
            + (State.checkpointY[current.checkpointIndex] - current.y) * current.vy)
        / (speed * distToNextCheckPoint);
        
    
    double score = 0.0;
    //score += 1000 * (current.checkpointIndex - original.checkpointIndex );
    score -= 100 * (current.distanceRemaining[current.checkpointIndex] + 0.3 * distToNextCheckPoint);
    
    score += 100 * directionToNextCheckpoint;
    score += 100.0 * speed / MAX_SPEED;
    score += 10.0 * exitSpeedFeature(original, current);
    score += current.finished ? 1_000_000 : 0;
    return score;
  }
  
  private double exitSpeedFeature(State init, State current) {
    if (current.checkpointIndex == init.checkpointIndex) return 0.0;
    

    double speed = Math.sqrt(current.vx * current.vx + current.vy * current.vy);
    return speed / MAX_SPEED;
  }
}
