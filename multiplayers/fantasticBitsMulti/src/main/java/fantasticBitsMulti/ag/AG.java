package fantasticBitsMulti.ag;

import fantasticBitsMulti.Player;
import fantasticBitsMulti.simulation.Simulation;
import random.FastRand;

public class AG {
  public static final int POOL = 50;
  public static final int MUTATION = 2;
  public static final int DEPTH = 4;
  static final double COEF_PATIENCE = 0.9;
  public static final int SPELL_DEPTH = 8;

  private static AGSolution best = new AGSolution();
  private static FastRand rand = new FastRand(42);

  static double patiences[] = new double[DEPTH];
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
    Simulation.simulate(sol);
    
    best.copy(sol);
    
    AGSolution tempBest = buildFirstGeneration(base, pool, sol);

    double limit = Player.turn != 0 ? 85_000_000 : 800_000_000;
    int generation = 1;
    int bestGeneration = 1;
    int poolFE;
    
    while (System.nanoTime() - Player.start < limit) {
      // New generation

      // Force the actual best with a mutation to be in the pool
      AGSolution solution = newPool[0];
      solution.copy(tempBest);
      solution.mutate();
      Simulation.simulate(solution);
      if (solution.energy > tempBest.energy) {
        tempBest = solution;
      }

      poolFE = 1;
      while (poolFE < POOL && System.nanoTime() - Player.start < limit) {
        AGSolution child = merge2Solutions(newPool[poolFE++], pool );
        Simulation.simulate(child);
        if (child.energy > tempBest.energy) {
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
    
    // Play a last time to check some infos
    Player.myWizard1.apply(best, 0, 1);
    Player.myWizard2.apply(best, 0, 2);
    Simulation.dummies();
    Simulation.play();
    
    Simulation.smyMana = Player.myMana;
    Player.bludgers[0].slast = Player.bludgers[0].last;
    Player.bludgers[1].slast = Player.bludgers[1].last;

    for (int i = 0; i < 16; ++i) {
      Player.spells[i].save();
    }
    Simulation.reset();
    
    // Burn last generation !!
    //TODO revoir les deletes ...
//    for (int i = 0; i < poolFE; ++i) {
//      delete pool[i];
//    }
//
//    delete [] pool;
//    delete [] newPool;

    System.err.println("Generations : "+generation);
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
        
        Simulation.simulate(solution);
        
        if (solution.energy > tempBest.energy) {
          tempBest = solution;
        }
      }
      startI = POOL / 5;
    }
    
    for (int i = startI; i < POOL; ++i) {
      AGSolution solution = pool[i];
      solution.randomize();

      Simulation.simulate(solution);

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
