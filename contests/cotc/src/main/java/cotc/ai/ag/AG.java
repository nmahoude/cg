package cotc.ai.ag;

import cotc.GameState;
import cotc.ai.AI;
import cotc.ai.AISolution;
import cotc.game.Simulation;

public class AG implements AI {
  private static AGSolution fake = AGSolution.createFake();

  private GameState state;
  private Simulation simulation;
  
  public AISolution evolve(long stopTime) {
    int simulations = 0;
    simulation = new Simulation(state);
    
    AGSolution best = fake;
    System.err.println("Start ag");
    while (System.currentTimeMillis() < stopTime) {
      AGSolution sol = new AGSolution(state);
      sol.randomize(state);
      
      simulation.simulate(sol);
      simulations++;
      if (simulations <= 2) {
        System.err.println("Simulation done : "+simulations);
      }
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
    
    AGSolution best = fake;
    for (int i =0;i<50;i++) {
      AGSolution sol = new AGSolution(state);
      sol.randomize(state);
      
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
