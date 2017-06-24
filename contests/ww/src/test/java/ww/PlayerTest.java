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
  public void test() {
    state.size = 5;
    TU.setAgent(state, 0,3,4);
    TU.setAgent(state, 1,1,0);
    TU.setAgent(state, 2,-1,-1);
    TU.setAgent(state, 3,0,1);
    TU.setHeights(state, 
      "00110",
      "00200",
      "00000",
      "00000",
      "00000");
    
    Move move = TU.getMove(1 , Dir.E, Dir.W);
    boolean valid = simulation.simulate(move, state);
    assertThat(valid, is (true));
    double score = Player.calculateMoveScore(move);
    System.err.println(score);
    
    state.restore();
    Move move2 = TU.getMove(0 , Dir.N, Dir.N);
    boolean valid2 = simulation.simulate(move2, state);
    assertThat(valid2, is (true));
    double score2 = Player.calculateMoveScore(move2);
    System.err.println(score2);
    
  }
}
