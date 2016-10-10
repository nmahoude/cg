package stc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

import org.junit.Test;

public class AiTest {

  @Test
  public void minimizeHeight() throws Exception {
    Game game = new Game();
    GameTest.setNextBlocks(game, 
        "12",
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
        "11....");
    
    Ai ai = new Ai(game);
    ai.think();
    
    assertThat(ai.command, is(not("0 0")));
  }
}
