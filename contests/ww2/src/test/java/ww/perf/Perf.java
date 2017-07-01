package ww.perf;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import ww.GameState;
import ww.Player;
import ww.TU;
import ww.sim.Simulation;

public class Perf {
  
  GameState state;
  Simulation simulation;

  @Before
  public void setup() {
    state = new GameState();
    simulation = new Simulation(state);
  }
  
  @Test
  public void perf() {
    state.size = 6;
    state.readInit(new Scanner("" + state.size + " 2"));
    TU.setHeights(state, 
      "344444",
      "333434",
      "..34..",
      ".3..3.",
      ".0101.",
      "000002");
    TU.setAgent(state, 0,1,3);
    TU.setAgent(state, 1,1,1);
    TU.setAgent(state, 2,-1,-1);
    TU.setAgent(state, 3,-1,-1);
    
    Player.state = state;
    for (int i=0;i<10_000;i++) {
      Player.think(simulation);
    }
  }
}
