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
    simulation = new Simulation(state);
    
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
  
    simulation.simulate(TU.getMove(state.agents[0], Dir.E, Dir.E));
    
    assertThat(state.agents[0].cell, is(not(nullValue())));
  }
  
  @Test
  public void canDoOnePush() {
    state.size = 5;
    state.readInit(new Scanner("" + state.size + " 2"));
    TU.setHeights(state, 
      "11040",
      "14443",
      "34443",
      "34443",
      "44113");
    TU.setAgent(state, 0,0,1);
    TU.setAgent(state, 1,0,0);
    TU.setAgent(state, 2,1,0);
    TU.setAgent(state, 3,-1,-1);
  
    Move move = TU.getMove(state.agents[1], Dir.E, Dir.E);
    simulation.simulate(move);
    
    assertThat(move.isValid(), is(true));
  }
  
  
  @Test
  public void undoMove_emptyCells() {
    state.size = 5;
    state.readInit(new Scanner(""+state.size+" 2"));
    TU.setHeights(state, 
        "00000",
        "00000",
        "00000",
        "00000",
        "00000");
    TU.setAgent(state, 0,0,0);
    TU.setAgent(state, 1,0,4);
    TU.setAgent(state, 2,4,0);
    TU.setAgent(state, 3,4,4);
  
    Move move = TU.getMove(state.agents[0], Dir.E, Dir.E);
    simulation.simulate(move);
    simulation.undo(move);
    
    assertThat(state.grid.get(0, 0).agent, is(state.agents[0]));
    assertThat(state.grid.get(1, 0).agent, is(nullValue()));
    assertThat(state.grid.get(2, 0).height, is(0));
    assertThat(state.agents[0].cell, is (state.grid.get(0, 0)));
  }

  @Test
  public void undoMove_moveToHeight_3_checkScore() {
    state.size = 5;
    state.readInit(new Scanner(""+state.size+" 2"));
    TU.setHeights(state, 
        "33300",
        "00000",
        "00000",
        "00000",
        "00000");
    TU.setAgent(state, 0,0,0);
    TU.setAgent(state, 1,0,4);
    TU.setAgent(state, 2,4,0);
    TU.setAgent(state, 3,4,4);
  
    Move move = TU.getMove(state.agents[0], Dir.E, Dir.E);
    simulation.simulate(move);
    assertThat(state.agents[0].score, is(1.0));
    
    simulation.undo(move);
    
    assertThat(state.agents[0].score, is(0.0));
  }
  
  @Test
  public void undoMove_moveToHeight_4() {
    state.size = 5;
    state.readInit(new Scanner(""+state.size+" 2"));
    TU.setHeights(state, 
        "33300",
        "00000",
        "00000",
        "00000",
        "00000");
    TU.setAgent(state, 0,0,0);
    TU.setAgent(state, 1,0,4);
    TU.setAgent(state, 2,4,0);
    TU.setAgent(state, 3,4,4);
  
    Move move = TU.getMove(state.agents[0], Dir.E, Dir.E);
    simulation.simulate(move);
    simulation.undo(move);
    
    assertThat(state.grid.get(0, 0).agent, is(state.agents[0]));
    assertThat(state.grid.get(1, 0).agent, is(nullValue()));
    assertThat(state.grid.get(2, 0).height, is(3));
    assertThat(state.agents[0].cell, is (state.grid.get(0, 0)));
  }

  @Test
  public void undoMove_pushToLower() {
    state.size = 5;
    state.readInit(new Scanner(""+state.size+" 2"));
    TU.setHeights(state, 
        "33000",
        "00000",
        "00000",
        "00000",
        "00000");
    TU.setAgent(state, 0,0,0);
    TU.setAgent(state, 1,0,4);
    TU.setAgent(state, 2,1,0);
    TU.setAgent(state, 3,4,4);
  
    Move move = TU.getMove(state.agents[0], Dir.E, Dir.E);
    simulation.simulate(move);
    simulation.undo(move);
    
    assertThat(state.grid.get(0, 0).agent, is(state.agents[0]));
    assertThat(state.grid.get(1, 0).agent, is(state.agents[2]));
    
    assertThat(state.grid.get(0, 0).agent, is(state.agents[0]));
    assertThat(state.grid.get(1, 0).agent, is(state.agents[2]));
    assertThat(state.grid.get(1, 0).height, is(3));
    assertThat(state.agents[0].cell, is (state.grid.get(0, 0)));
    assertThat(state.agents[2].cell, is (state.grid.get(1, 0)));
  }
  
  @Test
  public void undoMove_thatThrowException() {
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

    Move move = TU.getMove(state.agents[0], Dir.E, Dir.E);
    simulation.simulate(move);
    simulation.undo(move);
  }
  
  @Test
  public void simulateOpponent() {
    state.size = 5;
    state.readInit(new Scanner("" + state.size + " 2"));
    TU.setHeights(state, 
      "00000",
      "00220",
      "00000",
      "00000",
      "00000");
    TU.setAgent(state, 0,1,1);
    TU.setAgent(state, 1,4,1);
    TU.setAgent(state, 2,1,0);
    TU.setAgent(state, 3,3,1);

    Move move = TU.getMove(state.agents[1], Dir.W, Dir.S);
    simulation.simulate(move);
    simulation.undo(move);
    state.toTDD();
    
    move = TU.getMove(state.agents[1], Dir.W, Dir.SW);
    simulation.simulate(move);
    simulation.undo(move);
    state.toTDD();
  }
}
