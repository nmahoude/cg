package cotc.ai.ag.ref3;

import cotc.GameState;
import cotc.ai.AI;
import cotc.ai.AISolution;
import cotc.game.Simulation;

public class AG implements AI {
  private static AGSolution fake = AGSolution.createFake();

  protected GameState state;
  protected Simulation simulation;
  public FeatureWeight weights = new FeatureWeight();
  
  public AISolution evolve(long stopTime) {
    int simulations = 0;
    simulation = new Simulation(state);
    // Pre analyse
    StateAnalyser analyser = new StateAnalyser();
    analyser.analyse(state);
    analyser.debug();
    
    AGSolution best = fake;
    while (System.currentTimeMillis() < stopTime) {
      AGSolution sol = new AGSolution(state, weights);
      sol.randomize(state, analyser);
      
      simulation.simulate(sol);
      simulations++;
      if (sol.energy > best.energy) {
        best = sol;
      }
    }
    System.err.println("Simulations "+simulations);
    return best;
  }

  @Override
  public AISolution evolve() {
    simulation = new Simulation(state);
    // Pre analyse
    StateAnalyser analyser = new StateAnalyser();
    analyser.analyse(state);
    
    AGSolution best = fake;
    for (int i =0;i<100;i++) {
      AGSolution sol = new AGSolution(state,weights);
      sol.randomize(state, analyser);
      
      simulation.simulate(sol);
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
