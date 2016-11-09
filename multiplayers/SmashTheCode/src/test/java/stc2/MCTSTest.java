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
    sim.init();
    sim.putBalls(1, 1, 0, 0);
    
    int points = sim.points
        +sim.groupsCount[2]
        +2*sim.groupsCount[3]
        -sim.groupsCount[1];
    
    assertThat(points, is(CoreMatchers.not(26)));
  }

  @Test
  @Ignore
  public void debug() throws Exception {
    Game game = new Game();
    game.nextBalls[0] = 2;
    game.nextBalls2[0] = 4;

    BitBoardTest.prepareBoard(game.myBoard,
        "☠☠....",
        "☠2....",
        "☠3....",
        "54....",
        "55....",
        "23....",
        "5☠☠..☠",
        "43☠☠☠☠",
        "34☠☠☠☠",
        "35☠☠☠☠",
        "344☠☠3",
        "452213");

    MCTS mtcs = new MCTS();
    mtcs.game = game;

    long time1 = System.currentTimeMillis();
    mtcs.simulate();
    long time2 = System.currentTimeMillis();
    System.out.println("time : " + (time2 - time1));

    assertThat(mtcs.bestPoints[0], is(CoreMatchers.not(-1.0)));
    assertThat(mtcs.bestPoints[0], is(0.0));
    assertThat(mtcs.bestCombos[0], is(CoreMatchers.not(0)));
    assertThat(mtcs.bestCombos[1], is(CoreMatchers.not(0)));

  }
}
