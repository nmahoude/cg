package ww.sim;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import ww.Dir;
import ww.GameState;
import ww.TU;

public class SimulationTest {
  GameState state ;
  Simulation simulation;
  
  @Before 
  public void setup() {
    state = new GameState();
    state.readInit(new Scanner("7 2"));
  
    simulation = new Simulation();
    
  }
  
  @Test
  public void canPushTowardAKnownAgent() {
    state.size = 5;
    TU.setAgent(state, 0,3,0);
    TU.setAgent(state, 1,4,2);
    TU.setAgent(state, 2,3,1);
    TU.setAgent(state, 3,-1,-1);
    TU.setHeights(state,
      "00303",
      "00233",
      "00003",
      "00003",
      "00000"
    );
    
    Move move = TU.getMove(1, Dir.NW, Dir.N);

    simulation.simulate(move, state);
    
    assertThat(move.isValid(), is(false));
  }
  
  @Test
  public void canGoDownstairs() {
    state.size = 6;
    TU.setAgent(state, 0,0,1);
    TU.setAgent(state, 1,3,0);
    TU.setAgent(state, 2,-1,-1);
    TU.setAgent(state, 3,-1,-1);
    TU.setHeights(state,
      "4.4303",
      "3.44.3",
      "444443",
      "44..34",
      ".4334.",
      "333433");
    
    Move move = TU.getMove(1, Dir.E, Dir.W);

    simulation.simulate(move, state);
    
    assertThat(move.isValid(), is(true));
  }
  
}
