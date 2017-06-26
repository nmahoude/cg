package ww;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import ww.sim.Move;
import ww.sim.Simulation;

public class NodeTest {
  GameState state;
  Simulation simulation;

  @Before
  public void setup() {
    state = new GameState();
    state.readInit(new Scanner("7 2"));

    simulation = new Simulation();

  }

  @Test
  public void findValidAction() {
      state.size = 5;
      TU.setAgent(state, 0,4,0);
      TU.setAgent(state, 1,1,0);
      TU.setAgent(state, 2,-1,-1);
      TU.setAgent(state, 3,-1,-1);
      TU.setHeights(state, 
        "11430",
        "44444",
        "12434",
        "00303",
        "00110");

    Node node = new Node();
    node.calculateChilds(0, state);

    assertThat(node.bestAction, is(not(nullValue())));
  }
  
  @Test
  public void findValidAction_2() {
    state.size = 7;
    TU.setAgent(state, 0,3,4);
    TU.setAgent(state, 1,2,5);
    TU.setAgent(state, 2,-1,-1);
    TU.setAgent(state, 3,-1,-1);
    TU.setHeights(state, 
      "...4...",
      "..444..",
      ".33443.",
      "4444443",
      ".43344.",
      "..344..",
      "...4...");
    
    Node node = new Node();
    node.calculateChilds(0, state);

    assertThat(node.bestAction, is(not(nullValue())));
  }
}
