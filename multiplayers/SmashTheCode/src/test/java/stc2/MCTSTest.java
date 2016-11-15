package stc2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MCTSTest {

  private Game game;

  @Before
  public void setup() {
    game = new Game();

  }
  @Test
  public void debugPoints() throws Exception {
    Simulation sim = new Simulation();
    sim.board = game.myBoard;
    
    BitBoardTest.prepareBoard(game.myBoard,
        "......",
        "......",
        "......",
        "...2..",
        "..14..",
        "..32..",
        "..32..",
        "..355.",
        "..525.",
        ".5545.",
        ".3111.",
        "332421");
    sim.clear();
    sim.putBalls(1, 1, 0, 0);
    
    int points = sim.points
        +sim.groupsCount[2]
        +2*sim.groupsCount[3]
        -sim.groupsCount[1];
    
    assertThat(points, is(CoreMatchers.not(26)));
  }
  
  @Test
  public void getSkullsCountAfterMove() throws Exception {
    Simulation sim = new Simulation();
    sim.board = game.myBoard;
    
    BitBoardTest.prepareBoard(game.myBoard,
        "......",
        "......",
        "......",
        "...0..",
        "..10..",
        "..02..",
        "..02..",
        "..315.",
        "..525.",
        ".5545.",
        ".3111.",
        "332421");
    sim.clear();
    sim.putBalls(1, 1, 0, 0);

    assertThat(game.myBoard.layers[BitBoard.SKULL_LAYER].bitCount(), is(4));
  }
  

  @Test
  @Ignore
  public void debug() throws Exception {
    Game game = new Game();
    game.nextBalls = new int[] { 2, 2, 2, 2, 2, 2, 2, 2};
    game.nextBalls2 = new int[] { 4, 4, 4, 4, 4, 4, 2, 2};

    BitBoardTest.prepareBoard(game.myBoard,
        "......",
        "......",
        "......",
        "......",
        "......",
        "23....",
        "5☠☠..☠",
        "43☠☠☠☠",
        "34☠☠☠☠",
        "35☠☠☠☠",
        "344☠☠3",
        "452213");

    MCTS mcts = new MCTS();
    mcts.attachGame(game);
    MCTS.MAX_PLY = 100_000;
    long time1 = System.currentTimeMillis();
    mcts.simulate(false);
    long time2 = System.currentTimeMillis();
    System.out.println("time : " + (time2 - time1));

  }
}
