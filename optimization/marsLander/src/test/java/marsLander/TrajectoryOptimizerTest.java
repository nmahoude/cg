package marsLander;

import java.util.Scanner;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class TrajectoryOptimizerTest {
  private Mars mars;
  private MarsLander lander;
  private TrajectoryOptimizer to;

  @Before
  public void setup() {
    mars = new Mars();
    lander = new MarsLander();

    to = new TrajectoryOptimizer();
  }
  
  @Test
  public void oneBigFlatSurface() throws Exception {
    mars.readInput (new Scanner("7 0 100 1000 500 1500 1500 3000 1000 4000 150 5500 150 6999 800 "));
    lander.readInput(new Scanner("0 150 0 0 550 0 0"));

    to.calculate(mars, lander);
    
    Assertions.assertThat(to.currentSegment).isEqualTo(0);
  }
  
  
  
}
