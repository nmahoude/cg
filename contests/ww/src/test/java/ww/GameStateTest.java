package ww;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import ww.sim.Simulation;

public class GameStateTest {
  GameState state;
  Simulation simulation;

  @Before
  public void setup() {
    state = new GameState();
    state.readInit(new Scanner("7 2"));

    simulation = new Simulation();

  }
  
  @Test
  public void test() {
    state.size = 6;
    TU.setAgent(state, 0,4,5);
    TU.setAgent(state, 1,4,4);
    TU.setAgent(state, 2,-1,-1);
    TU.setAgent(state, 3,-1,-1);
    TU.setHeights(state, 
      "013332",
      "334433",
      "433343",
      "344444",
      "34..24",
      "443433");
    
    assertThat(state.getReachableCells(1), is(2));
  }
  
}
