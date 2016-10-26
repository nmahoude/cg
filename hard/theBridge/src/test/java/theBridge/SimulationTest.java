package theBridge;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class SimulationTest {

  @Test
  public void checkJump() throws Exception {
    Road road = new Road();
    road.init(
        ".000.",
        ".000.",
        ".000.",
        ".000."
        );
    Simulation sim = new Simulation();
    sim.road = road;
    sim.motoX = 0;
    sim.motosSpeed = 4;
    for (int y=0;y<4;y++) sim.motosX[y] = 0;
    
    sim.simulate(Move.Jump);
    
    assertThat(sim.aliveMoto(), is(4));
  }

  @Test
  public void checkJump2() throws Exception {
    Road road = new Road();
    road.init(
        ".....00",
        ".....00",
        ".....00",
        ".....00"
        );
    Simulation sim = new Simulation();
    sim.road = road;
    sim.motoX = 0;
    sim.motosSpeed = 3;
    for (int y=0;y<4;y++) sim.motosX[y] = 0;
    
    sim.simulate(Move.Jump);
    
    assertThat(sim.aliveMoto(), is(4));
  }

  
  @Test
  public void checkSlow() throws Exception {
    Road road = new Road();
    road.init(
        ".....",
        ".....",
        ".....",
        "....."
        );
    Simulation sim = new Simulation();
    sim.road = road;
    sim.motoX = 0;
    sim.motosSpeed = 4;
    for (int y=0;y<4;y++) sim.motosX[y] = 0;
    
    sim.simulate(Move.Slow);
    
    assertThat(sim.aliveMoto(), is(4));
  }
}
