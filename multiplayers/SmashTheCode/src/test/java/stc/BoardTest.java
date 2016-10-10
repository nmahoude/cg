package stc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

public class BoardTest {

  @Test
  public void checkPreCalculatedHeights() throws Exception {
    Board board =new Board();
    prepareBoard(board,
        "....4.",
        "....4.",
        "....4.",
        "....4.",
        "....4.",
        "....4.",
        "....4.",
        "....4.",
        "....4.",
        "..3.4.",
        ".23.4.",
        "123145");
    
    assertThat(board.heights[0], is(1));
    assertThat(board.heights[1], is(2));
    assertThat(board.heights[2], is(3));
    assertThat(board.heights[3], is(1));
    assertThat(board.heights[4], is(12));
    assertThat(board.heights[5], is(1));
  }
  
  
  @Test
  public void place2RedBlocks() throws Exception {
    Board board =new Board();
    prepareBoard(board,
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
        "......");
    
    for (int i=0;i<1_000_000;i++) {
      board.putBlocks(1, 0, 0, 3);
      //board.destroyBlocks();
    }
  }
  
  @Test
  public void updateFullBoardWithNonCombo() throws Exception {
    Board board =new Board();
    prepareBoard(board,
        "111222",
        "111222",
        "777777",
        "123456",
        "123456",
        "123456",
        "777777",
        "111222",
        "333444",
        "111222",
        "333444",
        "111222");
    
    for (int i=0;i<1_000;i++) {
      for (int x=0;x<3;x++) {
        board.cells[x][11] = 1;
        board.cells[x][10] = 1;
      }
      for (int x=3;x<6;x++) {
        board.cells[x][11] = 2;
        board.cells[x][10] = 2;
      }
      board.destroyBlocks();
    }
  }

  @Test
  public void updateBoard() throws Exception {
    Board board =new Board();
    prepareBoard(board,
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "12451.",
        ".24.1.",
        "..151.",
        ".2..14");
    
    board.updateBoard();
  
    // 1st col
    assertThat(board.cells[0][0], is(1));
    assertThat(board.cells[0][1], is(0));
    assertThat(board.heights[0],  is(1));
    
    // 2nd col
    assertThat(board.cells[1][0], is(2));
    assertThat(board.cells[1][1], is(2));
    assertThat(board.cells[1][2], is(2));
    assertThat(board.cells[1][3], is(0));
    assertThat(board.heights[1],  is(3));
    
  }
  
  
  
  @Test
  public void destroyNeighbours_whenOk() throws Exception {
    Board board =new Board();
    prepareBoard(board,
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "11111.",
        "34251.",
        "111510",
        "141114");
    
    int count = board.destroyNeighbours(1,0,0,0);
    
    assertThat(count, is(14));
    assertThat(board.cells[0][0], is(0));
    assertThat(board.cells[0][1], is(0));
    assertThat(board.cells[1][1], is(0));
    assertThat(board.cells[0][3], is(0));
  }
  
  @Test
  public void destroyNeighbours_whenNotEnough() throws Exception {
    Board board =new Board();
    prepareBoard(board,
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "11111.",
        "34251.",
        "113510",
        "1@1114");
    
    int count = board.destroyNeighbours(1,0,0,0);
    
    assertThat(count, is(0));
    assertThat(board.cells[0][0], is(1));
    assertThat(board.cells[0][1], is(1));
    assertThat(board.cells[1][1], is(1));
    assertThat(board.cells[0][3], is(1));

    // skull
    assertThat(board.cells[1][0], is(9));
  }

  @Test
  public void destroyNeighbours_withSkulls() throws Exception {
    Board board =new Board();
    prepareBoard(board,
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "11111.",
        "@42@1.",
        "11151@",
        "141114");
    
    int count = board.destroyNeighbours(1,0,0,0);
    
    assertThat(count, is(14));
    assertThat(board.cells[0][2], is(0));
    assertThat(board.cells[3][2], is(0));
    assertThat(board.cells[5][1], is(0));
  }
  static void prepareBoard(Board board, String... rows) {
    board.prepare();
    
    int index = 0;
    for (String row : rows) {
      board.updateRow(12-++index, row);
    }
  }
}
