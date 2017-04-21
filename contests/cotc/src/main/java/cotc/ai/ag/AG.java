package cotc.ai.ag;

import cotc.GameState;
import cotc.ai.AI;
import cotc.ai.AISolution;
import cotc.entities.Ship;
import cotc.game.Simulation;

public class AG implements AI {
  private static AGSolution fake = AGSolution.createFake();

  protected GameState state;
  protected Simulation simulation;
  public FeatureWeight weights = new FeatureWeight();
  
  public AISolution evolve(long stopTime) {
    updateChampions();
    int simulations = 0;
    simulation = new Simulation(state);
    // Pre analyse
    StateAnalyser analyser = new StateAnalyser();
    analyser.analyse(state);
    //analyser.debug();
    
    AGSolution best = fake;
    int bestGeneration = 0;
    while (System.currentTimeMillis() < stopTime) {
      AGSolution sol = new AGSolution(state, weights);
      sol.randomize(state, analyser);
      
      simulation.simulateNew(sol);
      simulations++;
      if (sol.energy > best.energy) {
        best = sol;
        bestGeneration = simulations;
      }
    }
    System.err.println("Simulations "+simulations+ " at "+bestGeneration+" with "+best.energy);
    return best;
  }

  private void updateChampions() {
    Ship best = null;
    int bestHealth = 0;
    for (int s=0;s<state.teams[0].ships.FE;s++) {
      Ship ship = state.teams[0].ships.elements[s];
      ship.champion = false;
      if (ship.health > bestHealth) {
        bestHealth = ship.health;
        best = ship;
      }
    }
    best.champion = true;
  }

  @Override
  public AISolution evolve() {
    return evolve(100);
  }

  public AISolution evolve(int iteration) {
    simulation = new Simulation(state);
    // Pre analyse
    StateAnalyser analyser = new StateAnalyser();
    analyser.analyse(state);
    
    AGSolution best = fake;
    for (int i =0;i<iteration;i++) {
      AGSolution sol = new AGSolution(state,weights);
      sol.randomize(state, analyser);
      
      simulation.simulateNew(sol);
      if (sol.energy > best.energy) {
        best = sol;
      }
    }
    return best;
  }
  
  @Override
  public void setState(GameState state) {
    this.state = state;
  }

}
