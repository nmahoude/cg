import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class PlayerTest {

  @Test
  public void notAFreeSpot_zero() throws Exception {
    Player.Board board = new Player().new Board(6, 3);
    board.updateRow(0,  ".33333");
    board.updateRow(1,  ".33333");
    board.updateRow(2,  "333333");
    
    int score = board.getScore(1);
    assertThat(score , is(0));
  }

  @Test
  public void freeSpot_MoreThanZero() throws Exception {
    Player.Board board = new Player().new Board(6, 3);
    board.updateRow(0,  ".33333");
    board.updateRow(1,  ".33333");
    board.updateRow(2,  "333333");
    
    int score = board.getScore(0);
    assertThat(score > 0 , is(true));
  }

}
