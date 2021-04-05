package weightsOptimizer.optimizer;

import org.junit.jupiter.api.Test;

public class PopulationTest {

  
  @Test
  void matches() throws Exception {
    Population pop = new Population(3, 10);
    pop.start();
  }
}
