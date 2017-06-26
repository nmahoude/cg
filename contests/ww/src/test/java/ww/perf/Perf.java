package ww.perf;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import ww.Eval;
import ww.GameState;
import ww.Node;
import ww.TU;
import ww.sim.Simulation;

public class Perf {
  GameState state ;
  Simulation simulation;
  Eval eval = new Eval();

  @Before 
  public void setup() {
    state = new GameState();
    state.readInit(new Scanner("7 2"));
  
    simulation = new Simulation();
    
  }
  
  @Test
  public void test() {
    state.size = 6;
    TU.setAgent(state, 0,3,4);
    TU.setAgent(state, 1,3,2);
    TU.setAgent(state, 2,3,1);
    TU.setAgent(state, 3,-1,-1);
    TU.setHeights(state, 
      ".0030.",
      ".0003.",
      ".0330.",
      "0.03.0",
      "000120",
      "0.00.0");
    
    for (int i=0;i<1_000;i++) {
      Node node = new Node();
      node.calculateChilds(0, state);
    }
  }
}
