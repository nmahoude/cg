import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PlayerTest {
  Player player = new Player();

  @Before 
  public void setup() {
    player = new Player();
  }
  
  @Test
  public void notAFreeSpot_zero() throws Exception {
    Player.Board board = new Player.Board(6, 3);
    board.updateRow(0,  ".33333");
    board.updateRow(1,  ".33333");
    board.updateRow(2,  "333333");
    
    int score = board.getScore(1, new Player.Block(4,4));
    assertThat(score , is(0));
  }

  @Test
  public void freeSpot_MoreThanZero() throws Exception {
    Player.Board board = new Player.Board(6, 3);
    board.updateRow(0,  ".33333");
    board.updateRow(1,  ".33333");
    board.updateRow(2,  "333333");
    
    int score = board.getScore(0, new Player.Block(4,4));
    assertThat(score > 0 , is(true));
  }

  @Test
  public void getFuturePosWhenEmpty() throws Exception {
    Player.Board board = new Player.Board(6, 4);
    board.updateRow(0,  ".43333");
    board.updateRow(1,  ".43333");
    board.updateRow(2,  ".33333");
    board.updateRow(3,  ".33333");
    
    int row = board.getFuturePos(0);
    assertThat(row, is(3));
  }

  @Test
  public void getFuturePosWhenNotEmpty() throws Exception {
    Player.Board board = new Player.Board(6, 4);
    board.updateRow(0,  ".43333");
    board.updateRow(1,  ".43333");
    board.updateRow(2,  "133333");
    board.updateRow(3,  "133333");
    
    int row = board.getFuturePos(0);
    assertThat(row, is(1));
  }

  @Test
  public void simpleSquare_MoreThan500() throws Exception {
    Player.Board board = new Player.Board(6, 3);
    board.updateRow(0,  ".43333");
    board.updateRow(1,  ".43333");
    board.updateRow(2,  "333333");
    
    int score = board.getScore(0, new Player.Block(4,4));
    assertThat(score > 500 , is(true));
  }

}
