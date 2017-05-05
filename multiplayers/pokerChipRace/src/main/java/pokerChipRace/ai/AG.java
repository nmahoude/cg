package pokerChipRace.ai;

import pokerChipRace.GameState;
import pokerChipRace.Player;
import pokerChipRace.entities.Entity;
import pokerChipRace.simulate.Simulation;

public class AG {
  Simulation sim = new Simulation();

  static final int POP_SIZE = 100;
  AGSolution oldPopulation[] = new AGSolution[POP_SIZE];
  AGSolution population[] = new AGSolution[POP_SIZE];
  
  int bestGeneration = 0;
  AGSolution best = null;
  
  private GameState state;
  
  public AGSolution getSolution(GameState state, long stop) {
    System.err.println("Current seed : "+Player.rand.debugSeed());
    
    this.state = state;
    sim.setGameState(state);
    
    state.backup();
    //createPopulations();
    //initFirstPopulation();
    int generation = 0;
    while (System.currentTimeMillis() < stop) {
      //System.err.println("Generation "+generation);
      AGSolution sol = new AGSolution(state.myChips.length);
      sol.randomize();
      play(sol);
      if (best == null || sol.energy > best.energy) {
        best = sol;
        bestGeneration = generation;
      }
      generation++;
    }
    System.err.println("Generations : "+generation+ ", best energy is "+best.energy+" bestGen = "+bestGeneration);
    return best;
  }
  
  private void initFirstPopulation() {
    for (int i=0;i<POP_SIZE;i++) {
      oldPopulation[i].randomize();
      play(oldPopulation[i]);
      if (best == null || oldPopulation[i].energy > best.energy) {
        best = oldPopulation[i];
      }
    }
  }

  private void play(AGSolution solution) {
    for (int turn=0;turn<AGSolution.DEPTH;turn++) {
      solution.applyActions(state, turn);
      sim.playTurn();
      solution.calculateSubEnergy(state, turn);
    }

    solution.calculateFinalEnergy(state);
    state.restore();
  }

  private void createPopulations() {
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
