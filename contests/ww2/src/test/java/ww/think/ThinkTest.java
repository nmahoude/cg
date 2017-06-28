package ww.think;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import ww.GameState;
import ww.Player;
import ww.TU;
import ww.sim.Simulation;

public class ThinkTest {
  GameState state;
  Simulation simulation;

  @Before
  public void setup() {
    state = new GameState();
    simulation = new Simulation();

  }
  
  @Test
  public void perf() {
    state.size = 5;
    state.readInit(new Scanner("" + state.size + " 2"));
    TU.setHeights(state, 
        "00000",
        "00000",
        "00000",
        "00000",
        "00000");
      TU.setAgent(state, 0,1,1);
      TU.setAgent(state, 1,4,0);
      TU.setAgent(state, 2,1,0);
      TU.setAgent(state, 3,2,1);

    new Think(state).think(2);
  }
}
