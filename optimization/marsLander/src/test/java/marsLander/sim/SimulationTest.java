package marsLander.sim;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import marsLander.Mars;
import marsLander.MarsLander;

public class SimulationTest {

  private Mars mars;
  private MarsLander lander;
  private Simulation sim;
  @Before
  public void setup() {
    mars = new Mars();
    mars.readInput (new Scanner("2 "
        + "0 0 "
        + "6999 0"));

    lander = new MarsLander();
    sim = new Simulation(mars, lander);
  }
  
  @Test
  @Ignore
  public void freeFall() throws Exception {
    lander.readInput(new Scanner("2500 2700 0 0 550 0 0"));
    
    sim.update(new int[]{0, 0});
    
    assertThat(lander.x, is(2500.0));
    assertThat(lander.getYAsInt(), is(2698));
    assertThat(lander.getVyAsInt(), is(-4));
    
    sim.update(new int[]{0, 0});
    
    assertThat(lander.getXAsInt(), is(2500));
    assertThat(lander.getYAsInt(), is(2693));
    assertThat(lander.getVyAsInt(), is(-7));
    
    sim.update(new int[]{0, 0});
    
    assertThat(lander.getXAsInt(), is(2500));
    assertThat(lander.getVyAsInt(), is(-11));
    assertThat(lander.getYAsInt(), is(2683));
  }
}
