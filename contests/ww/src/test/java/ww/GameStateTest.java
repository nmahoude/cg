package ww;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import ww.sim.Simulation;

public class GameStateTest {
  public static class reachableCells {
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
      TU.setAgent(state, 0, 4, 5);
      TU.setAgent(state, 1, 4, 4);
      TU.setAgent(state, 2, -1, -1);
      TU.setAgent(state, 3, -1, -1);
      TU.setHeights(state,
          "013332",
          "334433",
          "433343",
          "344444",
          "34..24",
          "443433");

      assertThat(state.getReachableCells(1), is(2));
    }
    
    @Test
    public void emptyGrid() {
      state.size = 5;
      TU.setAgent(state, 0,2,1);
      TU.setAgent(state, 1,1,3);
      TU.setAgent(state, 2,-1,-1);
      TU.setAgent(state, 3,3,1);
      TU.setHeights(state, 
        "00000",
        "00000",
        "00000",
        "00000",
        "00000");
  
      assertThat(state.getReachableCells(1), is(24));
    }

  }

  public static class possibleActions {
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
      TU.setAgent(state, 0, 4, 5);
      TU.setAgent(state, 1, 4, 4);
      TU.setAgent(state, 2, -1, -1);
      TU.setAgent(state, 3, -1, -1);
      TU.setHeights(state,
          "013332",
          "334433",
          "433343",
          "344444",
          "34..24",
          "443433");

      assertThat(state.getPossibleActions(1), is(2));
    }
    
    
  }
}
