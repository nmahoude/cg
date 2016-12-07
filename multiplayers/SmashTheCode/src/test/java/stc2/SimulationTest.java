package stc2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import stc.P;

@RunWith(JUnitParamsRunner.class)
public class SimulationTest {
  Simulation simulation;
  BitBoard board;
  
  @Before
  public void setup() {
    simulation = new Simulation();
    board = new BitBoard();
    simulation.board = board;
  }
  
  @Test
  public void canPutBalls_invalid_1() {
    BitBoardTest.prepareEmptyBoard(board);

    assertThat(simulation.putBalls(1,1, 2,0), is(false));
  }

  @Test
  public void canPutBalls_invalid_2() {
    BitBoardTest.prepareEmptyBoard(board);

    assertThat(simulation.putBalls(1,1, 4,5), is(false));
  }
  
  @Test
  public void canPutBalls_emptyBoard() throws Exception {
    BitBoardTest.prepareEmptyBoard(board);

    assertThat(simulation.putBalls(1,1, 0,0), is(true));
  }

  @Test
  public void canPutBalls_limitCase_0_0() throws Exception {
    BitBoardTest.prepareBoard(board,
        "..1111",
        "111111", "111111", "111111", "111111", "111111", "111111","111111", "111111", "111111", "111111", "111111");

    assertThat(simulation.putBalls(2,2, 0,0), is(true));
  }

  @Test
  public void canPutBalls_limitCase_1_0() throws Exception {
    BitBoardTest.prepareBoard(board,
        ".11111",
        ".11111", "111111", "111111", "111111", "111111", "111111","111111", "111111", "111111", "111111", "111111");

    assertThat(simulation.putBalls(2,2, 1,0), is(true));
  }

  @Test
  public void canPutBalls_limitCase_2_1() throws Exception {
    BitBoardTest.prepareBoard(board,
        "..1111",
        "111111", "111111", "111111", "111111", "111111", "111111","111111", "111111", "111111", "111111", "111111");

    assertThat(simulation.putBalls(2,2, 2,1), is(true));
  }

  @Test
  public void canPutBalls_limitCase_3_0() throws Exception {
    BitBoardTest.prepareBoard(board,
        ".11111",
        ".11111", "111111", "111111", "111111", "111111", "111111","111111", "111111", "111111", "111111", "111111");

    assertThat(simulation.putBalls(2,2, 3,0), is(true));
  }

  @Test
  public void destroy_allInOneIteration() throws Exception {
    BitBoardTest.prepareBoard(board,
        ".11111",
        ".11111", 
        "111111", 
        "111111", 
        "111111", 
        "111111", 
        "111111",
        "111111", 
        "111111", 
        "111111", 
        "111111", 
        "111111");

    assertThat(simulation.putBalls(1,1, 3,0), is(true));
    assertThat(simulation.points, is((10*72)*Math.max(8+0+0,1)));
  }
  
  @Test
  public void destroy_OneBlockOneIteration() throws Exception {
    BitBoardTest.prepareBoard(board,
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        ".1....",
        ".1....");

    assertThat(simulation.putBalls(1,1, 3,0), is(true));
    assertThat(simulation.points, is((10*4)*Math.max(0+0+0, 1)));
  }

  @Test
  public void destroy_NoBlockOneIteration() throws Exception {
    BitBoardTest.prepareBoard(board,
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        ".1....");

    assertThat(simulation.putBalls(1,1, 3,0), is(true));
    assertThat(simulation.points, is(0));
  }

  @Test
  public void destroy_InTwoIteration() throws Exception {
    BitBoardTest.prepareBoard(board,
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        ".2....",
        ".2....",
        ".1....",
        ".122..");

    assertThat(simulation.putBalls(1,1, 3,0), is(true));
    assertThat(simulation.points, is((10*4)*Math.max(0+0+0,1)
        +(10*4)*Math.max(8+0+0, 1)));
  }
  
  @Test
  public void putBalls_twoIteration() throws Exception {
    BitBoardTest.prepareEmptyBoard(board);

    assertThat(simulation.putBalls(1,1, 3,0), is(true));
    assertThat(simulation.points, is(0));
    assertThat(simulation.putBalls(1,1, 3,1), is(true));
    assertThat(simulation.points, is(40));
  }
  
  @Test
  public void debugACase() throws Exception {
    BitBoardTest.prepareBoard(board,
    "......",
    "......",
    "......",
    "......",
    "......",
    "..3...",
    "..1...",
    ".55...",
    ".225..",
    ".251..",
    "4513..",
    "33113.");
    assertThat(simulation.putBalls(4, 2, 1, 0), is(true));
    assertThat(board.layers[BitBoard.COMPLETE_LAYER_MASK].bitCount(), is (2));
  }
  
  
  @Test
  public void debug() throws Exception {
    BitBoardTest.prepareBoard(board,
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "15....",
        "52....",
        "22....");

    simulation.putBalls(2,4, 0, 2);
    assertThat(simulation.points, is(40));
  }
  
  @Test
  public void score_simple2SpotsOneColor() throws Exception {
    BitBoardTest.prepareBoard(board,
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "11..11",
        "11..11");
    
    simulation.board = board;
    simulation.destroyBlocks(null);
    
    assertThat(simulation.points, is(80));
  }
  
  @Test
  public void score_simple1SpotsOneColor12blocks() throws Exception {
    BitBoardTest.prepareBoard(board,
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "111111",
        "111111");
    
    simulation.board = board;
    simulation.destroyBlocks(null);
    
    assertThat(simulation.points, is((10*12)*Math.max(8+0+0,1)));
  }
  
  @Test
  public void score_double() throws Exception {
    BitBoardTest.prepareBoard(board,
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "11....",
        "33....",
        "33....",
        "11....");
    
    simulation.board = board;
    simulation.destroyBlocks(null);
    
    assertThat(simulation.points, is(
        /*3*/(10*4)*Math.max(0+0+0,1)+
        /*1*/(10*4)*(8+0+0)
        ));
  }
  
  @Test
  public void score_simpleBonus2colors() throws Exception {
    BitBoardTest.prepareBoard(board,
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "11..44",
        "11..44");
    
    simulation.board = board;
    simulation.destroyBlocks(null);
    
    assertThat(simulation.points, is(160));
  }

  @Test
  public void groupsCount_2_of_1() throws Exception {
    BitBoardTest.prepareBoard(board,
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "1....1");
    
    simulation.board = board;
    simulation.destroyBlocks(null);
    
    assertThat(simulation.groupsCount[0], is(0));
    assertThat(simulation.groupsCount[1], is(2));
    assertThat(simulation.groupsCount[2], is(0));
    assertThat(simulation.groupsCount[3], is(0));
  }

  @Test
  public void groupsCount_after_destruction() throws Exception {
    BitBoardTest.prepareBoard(board,
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "1....1");
    
    simulation.board = board;
    simulation.destroyBlocks(Arrays.asList(P.get(3, 0)));
    
    assertThat(simulation.groupsCount[0], is(0));
    assertThat(simulation.groupsCount[1], is(2));
    assertThat(simulation.groupsCount[2], is(0));
    assertThat(simulation.groupsCount[3], is(0));
  }

  
  @Test
  public void groupsCount_2_of_2() throws Exception {
    BitBoardTest.prepareBoard(board,
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        ".....1",
        "11...1");
    
    simulation.board = board;
    simulation.destroyBlocks(null);
    
    assertThat(simulation.groupsCount[0], is(0));
    assertThat(simulation.groupsCount[1], is(0));
    assertThat(simulation.groupsCount[2], is(2));
    assertThat(simulation.groupsCount[3], is(0));
  }

  @Test
  public void groupsCount_2_of_3() throws Exception {
    BitBoardTest.prepareBoard(board,
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "1.....",
        "11.111");
    
    simulation.board = board;
    simulation.destroyBlocks(null);
    
    assertThat(simulation.groupsCount[0], is(0));
    assertThat(simulation.groupsCount[1], is(0));
    assertThat(simulation.groupsCount[2], is(0));
    assertThat(simulation.groupsCount[3], is(2));
  }
  
}
