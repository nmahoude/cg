package fantasticBitsMulti.ag;

import fantasticBitsMulti.Player;
import random.FastRand;

public class AG {
  public static final int POOL = 50;
  public static final int MUTATION = 2;
  public static final int DEPTH = 6;
  static final double COEF_PATIENCE = 0.9;
  public static final int SPELL_DEPTH = 8;

  private static AGSolution best = new AGSolution();
  private static FastRand rand = new FastRand(42);
  private static AGSimulator simulator = new AGSimulator();
  
  public static double patiences[] = new double[DEPTH];
  static {
    for (int i=0;i<DEPTH;++i) {
      patiences[i] = Math.pow(COEF_PATIENCE, i);
    }
  }

  static AGSolution[] pool = new AGSolution[POOL];
  static AGSolution[] newPool = new AGSolution[POOL];
  static {
    for (int i=0;i<POOL;i++) {
      pool[i] = new AGSolution();
      newPool[i] = new AGSolution();
    }
  }
  
  public static AGSolution evolution() {
    AGSolution base = null;
    
    if (Player.turn != 0) {
      base = new AGSolution();
      base.makeNewSolutionFromLastBest(best);
    }
    
    AGSolution[] temp;
    
    best = new AGSolution();

    AGSolution sol = pool[0];
    sol.randomize();
    simulator.simulate(sol);
    
    best.copy(sol);
    
    AGSolution tempBest = buildFirstGeneration(base, pool, sol);

    int generation = 1;
    int bestGeneration = 1;
    int poolFE;
    
    while (System.currentTimeMillis() - Player.start < Player.TIME_LIMIT) {
      // New generation

      // Force the actual best with a mutation to be in the pool
      AGSolution solution = newPool[0];
      solution.copy(tempBest);
      solution.mutate();
      simulator.simulate(solution);
      if (solution.energy > tempBest.energy) {
        tempBest = solution;
      }

      poolFE = 1;
      while (poolFE < POOL && System.nanoTime() - Player.start < Player.TIME_LIMIT) {
        AGSolution child = merge2Solutions(newPool[poolFE++], pool );
        simulator.simulate(child);
        if (child.energy > tempBest.energy) {
          bestGeneration = generation;
          tempBest = child;
        }
      }

      temp = pool;
      pool = newPool;
      newPool = temp;

      if (tempBest.energy > best.energy) {
        best.copy(tempBest);
        bestGeneration = generation;
      }
      tempBest = best;

      generation += 1;
    }
    
    System.err.println("Generations : "+generation+", best was "+bestGeneration);
    return best;
  }

  private static AGSolution merge2Solutions(AGSolution futureChild, AGSolution[] pool) {
    int firstIndex = findIndex(pool, -1);
    int secondIndex = findIndex(pool, firstIndex);
    AGSolution child = pool[firstIndex].mergeInto(futureChild, pool[secondIndex]);
    if (rand.fastRandInt(MUTATION) == 0) {
      child.mutate();
    }
    return child;
  }

  private static int findIndex(AGSolution[] pool, int otherThanIndex) {
    int aIndex, bIndex;
    do {
      aIndex = rand.fastRandInt(POOL);
    } while (aIndex == otherThanIndex);

    do {
      bIndex = rand.fastRandInt(POOL);
    } while (bIndex == aIndex && bIndex != otherThanIndex);

    return pool[aIndex].energy > pool[bIndex].energy ? aIndex : bIndex;
  }

  private static AGSolution buildFirstGeneration(AGSolution base, AGSolution[] pool, AGSolution sol) {
    AGSolution tempBest = sol;
    // First generation
    int startI = 1;
    if (Player.turn != 0) {
      // Populate the POOL with some copy of the previous best one
      for (int i = startI; i < POOL / 5; ++i) {
        AGSolution solution = pool[i];
        solution.copy(base);
        solution.randomizeLastMove();
        
        simulator.simulate(solution);
        
        if (solution.energy > tempBest.energy) {
          tempBest = solution;
        }
      }
      startI = POOL / 5;
    }
    
    for (int i = startI; i < POOL; ++i) {
      AGSolution solution = pool[i];
      solution.randomize();

      simulator.simulate(solution);

      if (solution.energy > tempBest.energy) {
        tempBest = solution;
      }

    }

    if (tempBest.energy > best.energy) {
      best.copy(tempBest);
    }
    tempBest = best;
    return tempBest;
  }
}
