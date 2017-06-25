package ww;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import ww.sim.Move;
import ww.sim.Simulation;

public class EvalTest {
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
  public void moveUpIsBetter() {
    state.size = 5;
    TU.setAgent(state, 0, 2, 2);
    TU.setAgent(state, 1, 2, 1);
    TU.setAgent(state, 2, 2, 3);
    TU.setAgent(state, 3, -1, -1);
    TU.setHeights(state,
        "10100",
        "02010",
        "00110",
        "00110",
        "00000");
    
    Move move = TU.getMove(0, Dir.NW, Dir.SE);
    simulation.simulate(move, state);
    assertThat(move.isValid(), is(true));
    
    double score1 = eval.calculateScore(state, move);
    eval.debug("MOVE UP");
    
    state.restore();

    Move move2 = TU.getMove(1, Dir.SE, Dir.NE);
    simulation.simulate(move2, state);
    assertThat(move2.isValid(), is(true));

    double score2 = eval.calculateScore(state, move2);
    eval.debug("BUILD LVL 1");

    assertThat(score1-score2 > 0 , is(true));
  }
  
  @Test
  public void gettingOurselfIsBadBad() {
    state.size = 5;
    TU.setAgent(state, 0,4,3);
    TU.setAgent(state, 1,4,1);
    TU.setAgent(state, 2,-1,-1);
    TU.setAgent(state, 3,-1,-1);
    TU.setHeights(state, 
      "00044",
      "00143",
      "11243",
      "03343",
      "00101");
    
    Move move = TU.getMove(0, Dir.N, Dir.S);
    simulation.simulate(move, state);
    assertThat(move.isValid(), is(true));
    
    double score = eval.calculateScore(state, move);
    
    assertThat(score < 1000, is(true));
  }
  
  @Test
  public void blockEnemyIsGood() {
    state.size = 6;
    TU.setAgent(state, 0,0,4);
    TU.setAgent(state, 1,5,4);
    TU.setAgent(state, 2,2,4);
    TU.setAgent(state, 3,-1,-1);
    TU.setHeights(state, 
      "343441",
      "444440",
      "2.34.4",
      "344443",
      "3.34.3",
      "334440");

    
    Move move = TU.getMove(0, Dir.S, Dir.E);
    simulation.simulate(move, state);
    assertThat(move.isValid(), is(true));
    
    double score = eval.calculateScore(state, move);
    
    assertThat(score > 5000, is(true));
  }
}
