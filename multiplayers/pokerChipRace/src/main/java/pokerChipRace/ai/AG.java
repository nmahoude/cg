package pokerChipRace.ai;

import java.util.Arrays;
import java.util.Comparator;

import pokerChipRace.GameState;
import pokerChipRace.Player;
import pokerChipRace.entities.Entity;
import pokerChipRace.simulate.Simulation;

public class AG {
  Simulation sim = new Simulation();

  public static final int POP_SIZE = 50;
  public static int SURVIVOR_POP_SIZE = 20;
  
  public AGSolution population[] = new AGSolution[POP_SIZE];
  
  int simulations = 0;
  int bestGeneration = 0;
  public AGSolution best = new AGSolution(0);
  
  GameState state;

  public long stop;
  
  public void setState(GameState state) {
    this.state = state;
    sim.setGameState(state);
  }

  public AGSolution getSolutionRandom(GameState state, long stop) {
    System.err.println("Current seed : "+Player.rand.debugSeed());
    
    setState(state);
    
    state.backup();
    int generation = 0;
    while (System.currentTimeMillis() < stop) {
      AGSolution sol = new AGSolution(state.myChips.length);
      sol.randomize();
      play(sol);
      if (best == null || sol.energy > best.energy) {
        best = sol;
        bestGeneration = generation;
      }
      generation++;
    }

    return best;
  }

  public AGSolution getSolutionAG(GameState state, long stop) {
    this.stop = stop;
    //System.err.println("Current seed : "+Player.rand.debugSeed());

    setState(state);
    
    state.backup();
    createPopulation();

    initFirstPopulation();
    
    while (System.currentTimeMillis() < stop) {
      nextPopulation();
    }
    
    System.err.printf("simulations %d with depth %d\n",simulations,AGSolution.DEPTH);
    return best;
  }

  public void nextPopulation() {
    int pop=SURVIVOR_POP_SIZE;
    
    sortPopulation(population);
    
    AGSolution localBest = best;
    while(pop < POP_SIZE && System.currentTimeMillis() < stop) {
      int firstIndex = findIndex(population, SURVIVOR_POP_SIZE, -1);
      int secondIndex = findIndex(population, SURVIVOR_POP_SIZE, firstIndex);
      
      AGSolution solution1 = population[pop++];
      AGSolution solution2 = population[pop++];
      solution1.clear();
      solution2.clear();
      
      AGSolution.crossOver(solution1, solution2, population[firstIndex], population[secondIndex]);
      solution1.mutate();
      solution2.mutate();
      play(solution1);
      if (solution1.energy > localBest.energy) {
        localBest = solution1;
      }
      play(solution2);
      if (solution2.energy > localBest.energy) {
        localBest = solution2;
      }
    }
    if (localBest.energy > best.energy) {
      best.copy(localBest);
    }
  }

  static void sortPopulation(AGSolution[] population) {
    Arrays.sort(population, new Comparator<AGSolution>() {
      @Override
      public int compare(AGSolution o2, AGSolution o1) {
        return Double.compare(o1.energy, o2.energy);
      }
    });
  }

  private static int findIndex(AGSolution[] pool, int max, int otherThanIndex) {
    int aIndex, bIndex;
    do {
      aIndex = Player.rand.nextInt(max);
    } while (aIndex == otherThanIndex);

    do {
      bIndex = Player.rand.nextInt(max);
    } while (bIndex == aIndex || bIndex == otherThanIndex);

    return pool[aIndex].energy > pool[bIndex].energy ? aIndex : bIndex;
  }

  public void initFirstPopulation() {
    AGSolution localBest = null;
    
    for (int i=0;i<POP_SIZE;i++) {
      population[i].randomize();
      play(population[i]);
      if (localBest == null || population[i].energy > localBest.energy) {
        localBest = population[i];
      }
    }
    best.copy(localBest);
  }

  public void play(AGSolution solution) {
    boolean gameLost = false;
    for (int turn=0;turn<AGSolution.DEPTH;turn++) {
      solution.applyActions(state, turn);
      sim.playTurn();
      if (state.isGameLost()) {
        gameLost = true;
        break;
      }
      solution.calculateIntermediateEnergy(state, turn);
    }
    
    if (!gameLost) {
      solution.calculateFinalEnergy(state);
    } else {
      solution.gameLost();
    }
    simulations++;
    state.restore();
  }

  public void createPopulation() {
    for (int i=0;i<POP_SIZE;i++) {
      population[i] = new AGSolution(state.myChips.length);
    }
  }

  void doOneTurn(GameState state) {
    sim.setGameState(state);
    
    for (Entity entity : state.myChips) {
      entity.targetx = -100;
      entity.targety = -100;
    }
    
    
    
  }
}
