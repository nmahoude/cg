package stc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

public class AiTest {

  @Test
  public void minimizeHeight() throws Exception {
    // preload
    //DFSNode.add(500_000);
    
    Game game = new Game();
    GameTest.setNextBlocks(game, 
        "11",
        "23",
        "44"
        );
    BoardTest.prepareBoard(game.myBoard,
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
        "....11");
    
    long time1 = System.currentTimeMillis();
    Ai ai = new Ai(game);
    ai.think();
    long time2 = System.currentTimeMillis();
    System.err.println("T2/T1: "+time1+","+time2);
    assertThat((time2-time1 < 150), is(true));
  }
  @Test
  public void oneRounds() throws Exception {
    Game game = new Game();
    GameTest.setNextBlocks(game, 
        "12",
        "21",
        "44"
        );
    BoardTest.prepareBoard(game.myBoard,
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
        "....11");
    
    Ai ai = new Ai(game);
    ai.think();
  }
  
  @Test
  @Ignore
  public void twoRounds() throws Exception {
    Game game = new Game();
    GameTest.setNextBlocks(game, 
        "12",
        "21",
        "44"
        );
    BoardTest.prepareBoard(game.myBoard,
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
        "....11");
    
    Ai ai = new Ai(game);
    ai.think();
    ai.think();
  }
  
  @Test
  public void debug_points() throws Exception {
    Game game = new Game();
    GameTest.setNextBlocks(game,
        "23",
        "34",
        "51",
        "43",
        "21",
        "24",
        "45",
        "11"
        );
        BoardTest.prepareBoard(game.myBoard,
        "......",
        "......",
        "..4...",
        "..1...",
        "..1...",
        ".24...",
        ".42...",
        ".15...",
        "214...",
        "2445..",
        "4153..",
        "4252..");
    
    Ai ai = new Ai(game);
    ai.think();
    
    assertThat(ai.command, is(not("1 0")));
  }
  
  @Test
  public void shortThinking() throws Exception {
    Game game = new Game();
    GameTest.setNextBlocks(game,
        "51",
        "13",
        "11",
        "11",
        "11",
        "13",
        "31",
        "25"
        );
        BoardTest.prepareBoard(game.myBoard,
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
        "11....");

    Ai ai = new Ai(game);
    ai.think();

  }
  @Test
  public void longThinking() throws Exception {
    Game game = new Game();
    GameTest.setNextBlocks(game,
        "44",
        "21",
        "53",
        "23",
        "53",
        "14",
        "51",
        "31"
        );
        BoardTest.prepareBoard(game.myBoard,
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "...54.",
        "...52.",
        "13.42.",
        "13☠☠52",
        "☠☠35☠☠");

    Ai ai = new Ai(game);
    ai.think();

  }
}
