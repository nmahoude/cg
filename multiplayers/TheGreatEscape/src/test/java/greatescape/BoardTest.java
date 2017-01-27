package greatescape;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class BoardTest {

  @Test
  public void addVerticalWallInMiddle() throws Exception {
    Board board = new Board();
    board.addWall(2, 2, "V");
    assertThat(board.cells[1][2].canGoRight, is(false));
  }

  @Test
  public void addVerticalWallInMiddle_wallAre2CellsLarge() throws Exception {
    Board board = new Board();
    board.addWall(2, 2, "V");
    assertThat(board.cells[1][3].canGoRight, is(false));
  }

  @Test
  public void addVerticalWallInMiddle_otherSideIsBlockedToo() throws Exception {
    Board board = new Board();
    board.addWall(2, 2, "V");
    
    assertThat(board.cells[2][2].canGoLeft, is(false));
    assertThat(board.cells[2][3].canGoLeft, is(false));
  }
  
  @Test
  public void addVertical_bewareOfLeftSide() throws Exception {
    Board board = new Board();
    board.addWall(0, 2, "V");
    
    assertThat(board.cells[0][2].canGoLeft, is(false));
    assertThat(board.cells[0][3].canGoLeft, is(false));
  }

  @Test
  public void addVertical_bewareOfRightSide() throws Exception {
    Board board = new Board();
    board.addWall(9, 2, "V");
    
    assertThat(board.cells[8][2].canGoRight, is(false));
    assertThat(board.cells[8][3].canGoRight, is(false));
  }

  @Test
  public void addVertical_bewareOfDownSide() throws Exception {
    Board board = new Board();
    board.addWall(2, 8, "V");
    
    assertThat(board.cells[2][8].canGoLeft, is(false));
  }
  
  @Test
  public void addHorizontal_middle() throws Exception {
    Board board = new Board();
    board.addWall(2, 2, "H");
    
    assertThat(board.cells[2][1].canGoDown, is(false));
    assertThat(board.cells[3][1].canGoDown, is(false));
    assertThat(board.cells[2][2].canGoUp, is(false));
    assertThat(board.cells[3][2].canGoUp, is(false));
  }
  
  @Test
  public void addHorizontal_bewareTop() throws Exception {
    Board board = new Board();
    board.addWall(2, 0, "H");
    
    assertThat(board.cells[2][0].canGoUp, is(false));
    assertThat(board.cells[3][0].canGoUp, is(false));
  }

  @Test
  public void addHorizontal_bewareBottom() throws Exception {
    Board board = new Board();
    board.addWall(2, 9, "H");
    
    assertThat(board.cells[2][8].canGoDown, is(false));
    assertThat(board.cells[3][8].canGoDown, is(false));
  }

  @Test
  public void addHorizontal_bewareRight() throws Exception {
    Board board = new Board();
    board.addWall(8, 2, "H");
    
    assertThat(board.cells[8][1].canGoDown, is(false));
    assertThat(board.cells[8][2].canGoUp, is(false));
  }
}
