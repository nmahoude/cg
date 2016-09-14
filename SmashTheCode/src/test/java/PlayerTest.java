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
  public void notAFreeSpot_Minus1() throws Exception {
    Player.Board board = new Player.Board(6, 3);
    board.updateRow(0,  ".33333");
    board.updateRow(1,  ".33333");
    board.updateRow(2,  "333333");
    
    int score = board.simulate(1, new Player.Block(4,4));
    assertThat(score , is(-1));
  }

  @Test
  public void freeSpot_Zero() throws Exception {
    Player.Board board = new Player.Board(6, 3);
    board.updateRow(0,  ".34343");
    board.updateRow(1,  ".34343");
    board.updateRow(2,  "134343");
    
    int score = board.simulate(0, new Player.Block(4,4));
    assertThat(score , is(0));
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
  public void countNeighbours_square() {
    Player.Board board = new Player.Board(6, 4);
    board.updateRow(0,  "44....");
    board.updateRow(1,  "44....");
    board.updateRow(2,  "......");
    board.updateRow(3,  "......");
    
    int nCount = board.countNeighbours(0, 0);
    assertThat(nCount, is(4));
  }
  
  @Test
  public void countNeighbours_randomSquare() {
    Player.Board board = new Player.Board(6, 4);
    board.updateRow(0,  "......");
    board.updateRow(1,  "...44.");
    board.updateRow(2,  "...44.");
    board.updateRow(3,  "......");
    
    int nCount = board.countNeighbours(3, 2);
    assertThat(nCount, is(4));
  }

  @Test
  public void countNeighbours_rectangle() {
    Player.Board board = new Player.Board(6, 4);
    board.updateRow(0,  "......");
    board.updateRow(1,  ".4444.");
    board.updateRow(2,  ".4444.");
    board.updateRow(3,  "......");
    
    int nCount = board.countNeighbours(3, 2);
    assertThat(nCount, is(8));
  }

  @Test
  public void countNeighbours_convulated() {
    Player.Board board = new Player.Board(6, 4);
    board.updateRow(0,  ".4..44");
    board.updateRow(1,  ".44.4.");
    board.updateRow(2,  "..444.");
    board.updateRow(3,  "444.4.");
    
    int nCount = board.countNeighbours(3, 2);
    assertThat(nCount, is(13));
  }
  
  @Test
  public void countNeighbours_full() {
    Player.Board board = new Player.Board(6, 4);
    board.updateRow(0,  "444444");
    board.updateRow(1,  "444444");
    board.updateRow(2,  "444444");
    board.updateRow(3,  "444444");
    
    int nCount = board.countNeighbours(3, 2);
    assertThat(nCount, is(24));
  }
  
  @Test
  public void destroy_simpleLineH() {
    Player.Board board = new Player.Board(6, 4);
    board.updateRow(0,  "4444..");
    board.updateRow(1,  "......");
    board.updateRow(2,  "......");
    board.updateRow(3,  "......");
    
    int score = board.destroyNeighbours(4, 0,0);
    assertThat(board.row(0), is("......"));
    assertThat(board.row(1), is("......"));
    assertThat(board.row(2), is("......"));
    assertThat(board.row(3), is("......"));
  }
  
  @Test
  public void destroy_simpleLineV() {
    Player.Board board = new Player.Board(6, 4);
    board.updateRow(0,  "4.....");
    board.updateRow(1,  "4.....");
    board.updateRow(2,  "4.....");
    board.updateRow(3,  "4.....");
    
    int score = board.destroyNeighbours(4, 0,0);
    assertThat(board.row(0), is("......"));
    assertThat(board.row(1), is("......"));
    assertThat(board.row(2), is("......"));
    assertThat(board.row(3), is("......"));
  }
  
  @Test
  public void destroy_square() {
    Player.Board board = new Player.Board(6, 4);
    board.updateRow(0,  "44....");
    board.updateRow(1,  "44....");
    board.updateRow(2,  "......");
    board.updateRow(3,  "......");
    
    int score = board.destroyNeighbours(4, 0,0);
    assertThat(board.row(0), is("......"));
    assertThat(board.row(1), is("......"));
    assertThat(board.row(2), is("......"));
    assertThat(board.row(3), is("......"));
  }
  
  @Test
  public void updateBoard_easy() {
    Player.Board board = new Player.Board(6, 4);
    board.updateRow(0,  "......");
    board.updateRow(1,  "33....");
    board.updateRow(2,  "......");
    board.updateRow(3,  "......");
    
    board.update();
    assertThat(board.row(0), is("......"));
    assertThat(board.row(1), is("......"));
    assertThat(board.row(2), is("......"));
    assertThat(board.row(3), is("33...."));
  }

  @Test
  public void updateBoard_sparse() {
    Player.Board board = new Player.Board(6, 4);
    board.updateRow(0,  "..3...");
    board.updateRow(1,  "3...3.");
    board.updateRow(2,  "...3..");
    board.updateRow(3,  ".3...3");
    
    board.update();
    assertThat(board.row(0), is("......"));
    assertThat(board.row(1), is("......"));
    assertThat(board.row(2), is("......"));
    assertThat(board.row(3), is("333333"));
  }

  @Test
  public void updateBoard_different() {
    Player.Board board = new Player.Board(6, 4);
    board.updateRow(0,  "2.3.3.");
    board.updateRow(1,  "351...");
    board.updateRow(2,  "...341");
    board.updateRow(3,  ".3...3");
    
    board.update();
    assertThat(board.row(0), is("......"));
    assertThat(board.row(1), is("......"));
    assertThat(board.row(2), is("253.31"));
    assertThat(board.row(3), is("331343"));
  }
  
  @Test
  public void score_simpleSquareGive40Points() throws Exception {
    Player.Board board = new Player.Board(6, 3);
    board.updateRow(0,  ".43434");
    board.updateRow(1,  ".43434");
    board.updateRow(2,  "313434");
    
    int score = board.simulate(0, new Player.Block(4,4));
    assertThat(score, is(40));
  }

  @Test
  public void bestCol_easy() {
    Player.Board board = new Player.Board(6, 4);
    board.updateRow(0,  "......");
    board.updateRow(1,  "......");
    board.updateRow(2,  "3.....");
    board.updateRow(3,  "3.....");
    
    int bestCol = board.getBestCol(new Player.Block(3,3));
    
    assertThat(bestCol, is(0));
  }

  @Test
  public void bestCol_ifNoBlockPossibleChooseAMinimumColumn() {
    Player.Board board = new Player.Board(6, 6);
    board.updateRow(0,  "......");
    board.updateRow(1,  "......");
    board.updateRow(2,  "1..111");
    board.updateRow(3,  "2.4222");
    board.updateRow(2,  "3.4333");
    board.updateRow(3,  "3.4222");
    
    int bestCol = board.getBestCol(new Player.Block(5,5));
    
    assertThat(bestCol, is(1));
  }
}
