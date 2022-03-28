package searchrace.ag;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import searchrace.AISolution;
import searchrace.Player;
import searchrace.State;

public class AG {
  private static final int POPULATION_BEST_POOL = 20;
  private static final int POPULATION_POOL_TOTAL = 50;
  
  
  private static final Random random = ThreadLocalRandom.current();
  private static final State work = new State();
  
  public int bestAngle;
  public int bestThrust;

  public AISolution best = new AISolution();
  AISolution[] population = new AISolution[POPULATION_POOL_TOTAL];
  
  public AG() {
    for (int i=0;i<POPULATION_POOL_TOTAL;i++) {
      population[i] = new AISolution();
    }
  }
  
  
  public void think(State state) {
    best.updateScore(Double.NEGATIVE_INFINITY);
    
    
    initPopulation(state);
    Arrays.sort(population, (s1, s2) -> Double.compare(s2.aiScore, s1.aiScore));
    if (population[0].aiScore > best.aiScore) {
      best.copyFrom(population[0]);
    }

    int sims = 0;
    
end:
  while(true) {
      for (int p=POPULATION_BEST_POOL;p<POPULATION_POOL_TOTAL;p++) {
        sims ++;
        if ((sims & 4096-1) == 0 && System.currentTimeMillis() - Player.start > 40) {
          break end;
        }
        int p1 = random.nextInt(POPULATION_BEST_POOL);
        int p2 = random.nextInt(POPULATION_BEST_POOL);
  
        population[p].merge(population[p1], population[p2]);
        work.copyFrom(state);
        population[p].apply(work);
        
        if (population[p].aiScore > best.aiScore) {
          best.copyFrom(population[p]);
        }
      }
      Arrays.sort(population, (s1, s2) -> Double.compare(s2.aiScore, s1.aiScore));
    }
    
    System.err.println("Sims : "+(sims));
    bestAngle = best.angles[0];
    bestThrust = best.thrusts[0];
  }

  private void initPopulation(State original) {
    for (int i=0;i<POPULATION_BEST_POOL;i++) {
      population[i].reinitFromLast(population[i]);
      work.copyFrom(original);
      population[i].apply(work);
    }
    
    for (int i=POPULATION_BEST_POOL;i<POPULATION_POOL_TOTAL;i++) {
      work.copyFrom(original);
      population[i].random();
      population[i].apply(work);
    }
  }
}
