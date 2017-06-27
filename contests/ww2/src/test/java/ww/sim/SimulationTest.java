package ww.sim;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
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
    
  
    simulation = new Simulation();
    
  }
  
  @Test
  public void test() {
      state.size = 5;
      state.readInit(new Scanner(""+state.size+" 2"));
      TU.setHeights(state, 
        "00000",
        "00000",
        "00000",
        "00000",
        "00000");
      TU.setAgent(state, 0,4,4);
      TU.setAgent(state, 1,3,2);
      TU.setAgent(state, 2,4,2);
      TU.setAgent(state, 3,-1,-1);
    state.backup();
    
    int t0 = simulation.getPossibleActionsCount(state, state.agents[0]);
    int t1 = simulation.getPossibleActionsCount(state, state.agents[1]);
    assertThat(t0+t1, is(58));
    assertThat(t0, is(14));
    assertThat(t1, is(44));
  }
  
  @Test
  public void AgentCellCantBeNull() {
    state.size = 5;
    state.readInit(new Scanner(""+state.size+" 2"));
    TU.setHeights(state, 
        "00000",
        "00000",
        "00000",
        "00000",
        "00000");
    TU.setAgent(state, 0,0,0);
    TU.setAgent(state, 1,3,4);
    TU.setAgent(state, 2,2,3);
    TU.setAgent(state, 3,-1,-1);
    state.backup();
  
    simulation.simulate(TU.getMove(state.agents[0], Dir.E, Dir.E), true);
    
    assertThat(state.agents[0].cell, is(not(nullValue())));
  }
}
