package marsLander;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import marsLander.ai.AG;
import marsLander.sim.Simulation;

public class PlayerTest {
  private Mars mars;
  private MarsLander lander;
  private Simulation sim;
  @Before
  public void setup() {
    mars = new Mars();
    mars.readInput (new Scanner("7 0 100 1000 500 1500 1500 3000 1000 4000 150 5500 150 6999 800"));

    lander = new MarsLander();
    sim = new Simulation(mars, lander);
  }
  
  @Test
  public void ai() throws Exception {
    lander.readInput(new Scanner("2500 2700 0 0 550 0 0"));
    
    AG ag = new AG(mars, lander);
    TrajectoryOptimizer to = new TrajectoryOptimizer();
    to.calculate(mars, lander);
    ag.think(to);
  }
}
