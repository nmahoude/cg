package ww;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import ww.sim.Simulation;

public class AgentTest {
  GameState state;
  Simulation simulation;

  @Before
  public void setup() {
    state = new GameState();
    state.readInit(new Scanner("7 2"));

    simulation = new Simulation();

  }
  
  @Test
  public void canPushUp() {
    state.size = 7;
    TU.setAgent(state, 0,4,1);
    TU.setAgent(state, 1,6,3);
    TU.setAgent(state, 2,5,4);
    TU.setAgent(state, 3,-1,-1);
    TU.setHeights(state, 
      "...0...",
      "..002..",
      ".00030.",
      "0000000",
      ".00001.",
      "..102..",
      "...0...");
    
    assertThat(state.agents[0].getPossibleActions(state), is(28));
    assertThat(state.agents[1].getPossibleActions(state), is(11));
  }
  
  @Test
  public void test() {
    state.size = 7;
    TU.setAgent(state, 0,4,4);
    TU.setAgent(state, 1,2,2);
    TU.setAgent(state, 2,-1,-1);
    TU.setAgent(state, 3,-1,-1);
    TU.setHeights(state, 
      "...0...",
      "..000..",
      ".31300.",
      "0444330",
      ".13431.",
      "..334..",
      "...1...");
    
    assertThat(state.agents[0].getPossibleActions(state), is(20));
    assertThat(state.agents[1].getPossibleActions(state), is(11));

  }
}
