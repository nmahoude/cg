package stc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

public class BoardTest {

  @Test
  @Ignore
  public void place2RedBlocks() throws Exception {
    Board board =new Board();
    
    for (int i=0;i<1_000_000;i++) {
      board.putBlocks(0, 0, 0, 3);
    }
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
    assertThat(board.cells[0][4], is(0));
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
        "114510",
        "141114");
    
    int count = board.destroyNeighbours(1,0,0,0);
    
    assertThat(count, is(0));
    assertThat(board.cells[0][0], is(1));
    assertThat(board.cells[0][1], is(1));
    assertThat(board.cells[1][1], is(1));
  }
  private void prepareBoard(Board board, String... rows) {
    int index = 0;
    for (String row : rows) {
      board.updateRow(12-++index, row);
    }
  }
}
