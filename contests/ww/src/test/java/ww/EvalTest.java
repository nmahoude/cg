package ww;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Ignore;
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
    
    state.restore();

    Move move2 = TU.getMove(1, Dir.SE, Dir.NE);
    simulation.simulate(move2, state);
    assertThat(move2.isValid(), is(true));

    double score2 = eval.calculateScore(state, move2);

    assertThat(score1-score2 > 0 , is(true));
  }
  
  @Test
  @Ignore
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
  
  @Test
  public void buildingAtHeight_2_isBetterThan_1() {
    state.size = 6;
    TU.setAgent(state, 0,2,4);
    TU.setAgent(state, 1,4,4);
    TU.setAgent(state, 2,5,5);
    TU.setAgent(state, 3,-1,-1);
    TU.setHeights(state, 
      "010000",
      "003001",
      ".3031.",
      "003311",
      "000010",
      "000000");
    
    Move move1 = TU.getMove(1, Dir.NE, Dir.W);
    simulation.simulate(move1, state);
    assertThat(move1.isValid(), is(true));
    double score1 = eval.calculateScore(state, move1);
    eval.debug("BUILD Next to lvl 3");

    state.restore();
    Move move2 = TU.getMove( 1, Dir.N, Dir.SE);
    simulation.simulate(move2, state);
    assertThat(move2.isValid(), is(true));
    double score2 = eval.calculateScore(state, move2);
    eval.debug("BUILD FAR");

    
    assertThat(score1 > score2, is(true));
  }
  
  /**
   * why ? agent 1 choose to go NE and trapped itself for 1 point
   */
  @Test
  public void dontBlockOurself() {
    state.size = 7;
    TU.setAgent(state, 0,5,3);
    TU.setAgent(state, 1,2,1);
    TU.setAgent(state, 2,-1,-1);
    TU.setAgent(state, 3,-1,-1);
    TU.setHeights(state, 
      "...3...",
      "..344..",
      ".24444.",
      "0004434",
      ".00344.",
      "..013..",
      "...2...");
    
    Move moveShouldBeBetter = TU.getMove(1, Dir.SW, Dir.NE);
    simulation.simulate(moveShouldBeBetter, state);
    assertThat(moveShouldBeBetter.isValid(), is(true));
    double score1 = eval.calculateScore(state, moveShouldBeBetter);
    eval.debug("Go back a lvl, but don't trapp");

    state.restore();
    Move initialMove = TU.getMove( 1, Dir.NE, Dir.SW);
    simulation.simulate(initialMove, state);
    assertThat(initialMove.isValid(), is(true));
    double score2 = eval.calculateScore(state, initialMove);
    eval.debug("it's a trap");
    
    assertThat(score1 > score2, is(true));
  }
  
  @Test
  public void prefereBuildingTowardCenter() {
    state.size = 7;
    TU.setAgent(state, 0,3,2);
    TU.setAgent(state, 1,4,3);
    TU.setAgent(state, 2,-1,-1);
    TU.setAgent(state, 3,-1,-1);
    TU.setHeights(state, 
      "4440444",
      "4401044",
      "4000004",
      "0000000",
      "4000004",
      "4410044",
      "4441444");
    
    Move moveShouldBeBetter = TU.getMove(1, Dir.N, Dir.S);
    simulation.simulate(moveShouldBeBetter, state);
    assertThat(moveShouldBeBetter.isValid(), is(true));
    double score1 = eval.calculateScore(state, moveShouldBeBetter);
    eval.debug("building south");

    state.restore();
    Move initialMove = TU.getMove( 1, Dir.N, Dir.N);
    simulation.simulate(initialMove, state);
    assertThat(initialMove.isValid(), is(true));
    double score2 = eval.calculateScore(state, initialMove);
    eval.debug("building north");
    
    assertThat(score1 > score2, is(true));
  }
}
