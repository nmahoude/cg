package stc2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

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

    assertThat(simulation.putBalls(1,1, 0,0), is(true));
  }

  @Test
  public void canPutBalls_limitCase_1_0() throws Exception {
    BitBoardTest.prepareBoard(board,
        ".11111",
        ".11111", "111111", "111111", "111111", "111111", "111111","111111", "111111", "111111", "111111", "111111");

    assertThat(simulation.putBalls(1,1, 1,0), is(true));
  }

  @Test
  public void canPutBalls_limitCase_2_1() throws Exception {
    BitBoardTest.prepareBoard(board,
        "..1111",
        "111111", "111111", "111111", "111111", "111111", "111111","111111", "111111", "111111", "111111", "111111");

    assertThat(simulation.putBalls(1,1, 2,1), is(true));
  }

  @Test
  public void canPutBalls_limitCase_3_0() throws Exception {
    BitBoardTest.prepareBoard(board,
        ".11111",
        ".11111", "111111", "111111", "111111", "111111", "111111","111111", "111111", "111111", "111111", "111111");

    assertThat(simulation.putBalls(1,1, 3,0), is(true));
  }

}
