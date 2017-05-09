package pokerChipRace.ai;

import pokerChipRace.GameState;
import pokerChipRace.Player;
import pokerChipRace.entities.Entity;
import pokerChipRace.simulate.Simulation;

public class AG {
  Simulation sim = new Simulation();

  public static final int POP_SIZE = 100;
  AGSolution oldPopulation[] = new AGSolution[POP_SIZE];
  public AGSolution population[] = new AGSolution[POP_SIZE];
  
  int bestGeneration = 0;
  public AGSolution best = new AGSolution(0);
  
  GameState state;

  private long stop;
  
  public void setState(GameState state) {
    this.state = state;
    sim.setGameState(state);
  }

  public AGSolution getSolutionAG(GameState state, long stop) {
    this.stop = stop;
    //System.err.println("Current seed : "+Player.rand.debugSeed());
    
    setState(state);
    
    state.backup();
    createPopulations();

    initFirstPopulation();
    
    while (System.currentTimeMillis() < stop) {
      nextPopulation();
      swapPopulations();
    }
    
    return best;
  }

  public void swapPopulations() {
    AGSolution[] swap = oldPopulation;
    oldPopulation = population;
    population = swap;
  }

  public void nextPopulation() {
    int pop=1;
    
    population[0].copy(best);
    
    AGSolution localBest = best;
    while(pop < POP_SIZE && System.currentTimeMillis() < stop) {
      AGSolution solution = population[pop];
      int firstIndex = findIndex(oldPopulation, -1);
      int secondIndex = findIndex(oldPopulation, firstIndex);
      solution.clear();
      solution.crossOver(oldPopulation[firstIndex], oldPopulation[secondIndex]);
      solution.mutate();
      play(solution);
      if (solution.energy > localBest.energy) {
        localBest = solution;
      }
      pop++;
    }
    if (localBest.energy > best.energy) {
      best.copy(localBest);
    }
  }

  private static int findIndex(AGSolution[] pool, int otherThanIndex) {
    int aIndex, bIndex;
    do {
      aIndex = Player.rand.nextInt(POP_SIZE);
    } while (aIndex == otherThanIndex);

    do {
      bIndex = Player.rand.nextInt(POP_SIZE);
    } while (bIndex == aIndex && bIndex != otherThanIndex);

    return pool[aIndex].energy > pool[bIndex].energy ? aIndex : bIndex;
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
  
  public void initFirstPopulation() {
    AGSolution localBest = null;
    
    for (int i=0;i<POP_SIZE;i++) {
      oldPopulation[i].randomize();
      play(oldPopulation[i]);
      if (localBest == null || oldPopulation[i].energy > localBest.energy) {
        localBest = oldPopulation[i];
      }
    }
    best.copy(localBest);
  }

  public void play(AGSolution solution) {
    for (int turn=0;turn<AGSolution.DEPTH;turn++) {
      solution.applyActions(state, turn);
      sim.playTurn();
      solution.calculateIntermediateEnergy(state, turn);
    }

    solution.calculateFinalEnergy(state);
    state.restore();
  }

  public void createPopulations() {
    for (int i=0;i<POP_SIZE;i++) {
      oldPopulation[i] = new AGSolution(state.myChips.length);
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
