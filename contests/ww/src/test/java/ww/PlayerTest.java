package ww;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import ww.sim.Move;
import ww.sim.Simulation;

public class PlayerTest {
  GameState state ;
  Simulation simulation;
  
  @Before 
  public void setup() {
    state = new GameState();
    state.readInit(new Scanner("7 2"));
  
    simulation = new Simulation();
    Player.state = state;
  }
  
  @Test
  public void blockedInTopRightIsNotAGoodIdea() {
    state.size = 5;
    TU.setAgent(state, 0,3,0);
    TU.setAgent(state, 1,1,1);
    TU.setAgent(state, 2,-1,-1);
    TU.setAgent(state, 3,1,0);
    TU.setHeights(state, 
      "20033",
      "00444",
      "00044",
      "00043",
      "00134");
    
    Move move = TU.getMove(0 , Dir.E, Dir.W);
    simulation.simulate(move, state);
    
    
    assertThat(move.isValid(), is (true));
    assertThat(state.agents[0].getPossibleActions(state), is(0));

    double score = Player.calculateScore(move);
    System.err.println(score);
    
  }
  
  
}
