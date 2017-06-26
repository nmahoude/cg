package ww;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import ww.sim.Simulation;

public class NodeV2Test {
  GameState state;
  Simulation simulation;

  @Before
  public void setup() {
    state = new GameState();
    state.readInit(new Scanner("7 2"));
    state.startTime = System.currentTimeMillis();
    
    simulation = new Simulation();

  }
  
  @Test
  public void test() {
    state.size = 5;
    TU.setAgent(state, 0,1,1);
    TU.setAgent(state, 1,3,0);
    TU.setAgent(state, 2,2,2);
    TU.setAgent(state, 3,1,2);
    TU.setHeights(state, 
      "00000",
      "00000",
      "00000",
      "00000",
      "00000");
    
    NodeV2 node = new NodeV2();
    node.calculateChilds(0, state);
    assertThat(node.bestAction, is(not(nullValue())));
  }
  
  @Test
  public void dontAcceptDefeat() {
    state.size = 5;
    TU.setAgent(state, 0,0,0);
    TU.setAgent(state, 1,4,1);
    TU.setAgent(state, 2,-1,-1);
    TU.setAgent(state, 3,0,1);
    TU.setHeights(state, 
      "01143",
      "03243",
      "12213",
      "10000",
      "00000");
    
    NodeV2 node = new NodeV2();
    node.calculateChilds(0, state);
    assertThat(node.bestAction, is(not(nullValue())));
  }
}
