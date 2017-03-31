package gitc.ag;

import java.util.Random;

import gitc.Player;
import gitc.simulation.Simulation;

public class AG {
  Random rand = new Random(42);
  AGParameters parameters = new AGParameters();
  Simulation simulation;

  // local (turn relative) informations
  int bestGeneration;
  long timeLimit;
  AGSolution best = null; // previous best solution

  // pools
  AGSolution[] pool;
  AGSolution[] newPool;
  
  public AG(Simulation simulation, AGParameters parameters) {
    this.simulation = simulation;
    this.parameters = parameters;

    pool = new AGSolution[parameters.POOL_SIZE];
    newPool = new AGSolution[parameters.POOL_SIZE];
    for (int i=0;i<parameters.POOL_SIZE;i++) {
      pool[i] = new AGSolution();
      newPool[i] = new AGSolution();
    }
  }
  
  /**
   * From the current state of the Player
   * From the last best solution
   * Calculate the best possible new moves
   * 
   * @return AGSolution representing the best child
   */
  public AGSolution evolution(long timeLimit) {
    this.timeLimit = timeLimit;
    bestGeneration = 1;
    int generation = 1;

    AGSolution firstGenBest = build1stGeneration();
    if (best == null) {
      best = new AGSolution();
    }
    best.copy(firstGenBest);

    while (thereIsStillTime()) {
      generation++;
      AGSolution newGenerationBest = buildNewGeneration(generation);
      if (newGenerationBest.energy > best.energy) {
        best.copy(newGenerationBest);
        bestGeneration = generation;
      }

      swapPools();
    }
    
    return best;
  }

  /**
   * return the best element from this generation
   * @return
   */
  private AGSolution build1stGeneration() {
    int poolIndex = 0;
    AGSolution tempBest = null;
    double tempBestEnergy = -1_000_000;
    
    if (best != null) {
      AGSolution base = new AGSolution();
      base.copyFromPreviousTurnBest(best);
      
      while (poolIndex<parameters.BEST_RANDOM_SIZE) {
        AGSolution solution = pool[poolIndex++];
        solution.copy(base);
        solution.randomizeLastMove(); // TODO instead of randomize BEST_RANDOM_SIZE, it's maybe possible to try all ?
        
        simulation.simulate(solution);
        if (solution.energy > tempBestEnergy) {
          tempBest = solution;
          tempBestEnergy = solution.energy;
        }
      }
    }
    
    while (poolIndex < parameters.POOL_SIZE) {
      AGSolution solution = pool[poolIndex++];
      solution.randomize(); // TODO 1st generation, no best than randomize ?
      simulation.simulate(solution);

      if (solution.energy > tempBestEnergy) {
        tempBest = solution;
        tempBestEnergy = solution.energy;
      }
    }
    return tempBest;
  }

  // from pool (previous generation) to newPool
  private AGSolution buildNewGeneration(int generation) {
    AGSolution tempBest = null;
    double tempBestEnergy = -1_000_000;
    int poolIndex = 0;
    
    // current 'Best solution' injected in the current pool after a mutation
    while (poolIndex<parameters.MUTATE_BEST_ITERATION_SIZE) {
      AGSolution solution = newPool[poolIndex++];
      solution.copy(best);
      solution.mutate(); // force mutation
      simulation.simulate(solution);
      if (solution.energy > tempBestEnergy) {
        tempBest = solution;
        tempBestEnergy = solution.energy;
      }
    }
    
    // remaining of the pool from crossing/mutating 2 quite good solution
    while (poolIndex < parameters.POOL_SIZE && thereIsStillTime()) {
      AGSolution solution = newPool[poolIndex++];
      merge2SolutionsFromPool(solution, pool);
      simulation.simulate(solution);
      if (solution.energy > tempBestEnergy) {
        tempBest = solution;
        tempBestEnergy = solution.energy;
      }
    }

    return tempBest;
  }

  private void merge2SolutionsFromPool(AGSolution futureChild, AGSolution[] pool) {
    int firstIndex = findIndex(pool, -1);
    int secondIndex = findIndex(pool, firstIndex);
    futureChild.cross(pool[firstIndex], pool[secondIndex]);
    if (rand.nextInt(parameters.MUTATION_RATE) == 0) {
      futureChild.mutate();
    }
  }

  int findIndex(AGSolution[] pool, int otherThanIndex) {
    int aIndex, bIndex;
    do {
      aIndex = rand.nextInt(parameters.POOL_SIZE);
    } while (aIndex == otherThanIndex);

    for (int i=0;i<parameters.FIND_INDEX_ITERATION;i++) {
      do {
        bIndex = rand.nextInt(parameters.POOL_SIZE);
      } while (bIndex == aIndex || bIndex == otherThanIndex);
      
      aIndex = pool[aIndex].energy > pool[bIndex].energy ? aIndex : bIndex;
    }
    return aIndex;
  }

  private void swapPools() {
    AGSolution[] temp = pool;
    pool = newPool;
    newPool = temp;
  }

  private boolean thereIsStillTime() {
    return System.nanoTime() < timeLimit;
  }
}
