package greatescape;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class BoardTest {

  @Test
  public void addVerticalWallInMiddle() throws Exception {
    Board board = new Board();
    board.addWall(19, 2, 2, WallOrientation.V);
    assertThat(board.cells[1][2].wallRight, is(19));
  }

  @Test
  public void addVerticalWallInMiddle_wallAre2CellsLarge() throws Exception {
    Board board = new Board();
    board.addWall(18, 2, 2, WallOrientation.V);
    assertThat(board.cells[1][3].wallRight, is(18));
  }

  @Test
  public void addVerticalWallInMiddle_otherSideIsBlockedToo() throws Exception {
    Board board = new Board();
    board.addWall(15, 2, 2, WallOrientation.V);
    
    assertThat(board.cells[2][2].wallLeft, is(15));
    assertThat(board.cells[2][3].wallLeft, is(15));
  }
  
  @Test
  public void addVertical_bewareOfLeftSide() throws Exception {
    Board board = new Board();
    board.addWall(15, 0, 2, WallOrientation.V);
    
    assertThat(board.cells[0][2].wallLeft, is(15));
    assertThat(board.cells[0][3].wallLeft, is(15));
  }

  @Test
  public void addVertical_bewareOfRightSide() throws Exception {
    Board board = new Board();
    board.addWall(17, 9, 2, WallOrientation.V);
    
    assertThat(board.cells[8][2].wallRight, is(17));
    assertThat(board.cells[8][3].wallRight, is(17));
  }

  @Test
  public void addVertical_bewareOfDownSide() throws Exception {
    Board board = new Board();
    board.addWall(19, 2, 8, WallOrientation.V);
    
    assertThat(board.cells[2][8].wallLeft, is(19));
  }
  
  @Test
  public void addHorizontal_middle() throws Exception {
    Board board = new Board();
    board.addWall(1, 2, 2, WallOrientation.H);
    
    assertThat(board.cells[2][1].wallDown, is(1));
    assertThat(board.cells[3][1].wallDown, is(1));
    assertThat(board.cells[2][2].wallUp, is(1));
    assertThat(board.cells[3][2].wallUp, is(1));
  }
  
  @Test
  public void addHorizontal_bewareTop() throws Exception {
    Board board = new Board();
    board.addWall(1, 2, 1, WallOrientation.H);
    
    assertThat(board.cells[2][0].wallDown, is(1));
    assertThat(board.cells[3][0].wallDown, is(1));
    assertThat(board.cells[2][1].wallUp, is(1));
    assertThat(board.cells[3][1].wallUp, is(1));
  }

  @Test
  public void addHorizontal_bewareBottom() throws Exception {
    Board board = new Board();
    board.addWall(1, 2, 8, WallOrientation.H);
    
    assertThat(board.cells[2][7].wallDown, is(1));
    assertThat(board.cells[3][7].wallDown, is(1));
    assertThat(board.cells[2][8].wallUp, is(1));
    assertThat(board.cells[3][8].wallUp, is(1));
  }

  @Test
  public void addHorizontal_bewareRight() throws Exception {
    Board board = new Board();
    board.addWall(17, 7, 2, WallOrientation.H);
    
    assertThat(board.cells[7][1].wallDown, is(17));
    assertThat(board.cells[7][2].wallUp, is(17));
  }
  
  @Test
  public void cantcrossWalls() throws Exception {
    Board board = new Board();
    board.addWall(1, 1, 1, WallOrientation.V);
    boolean result = board.addWall(2, 0, 2, WallOrientation.H);
    
    assertThat(result, is(false));
  }

  @Test
  public void cantcrossWallsVertical() throws Exception {
    Board board = new Board();
    board.addWall(1, 0, 2, WallOrientation.H);
    boolean result = board.addWall(2, 1, 1, WallOrientation.V);
    
    assertThat(result, is(false));
  }
}
