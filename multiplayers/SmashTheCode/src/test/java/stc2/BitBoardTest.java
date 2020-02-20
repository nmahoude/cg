package stc2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static stc2.BitBoard.BLUE_LAYER;
import static stc2.BitBoard.COMPLETE_LAYER_MASK;
import static stc2.BitBoard.RED_LAYER;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import stc.P;

@RunWith(JUnitParamsRunner.class)
public class BitBoardTest {

  @Test
  public void prepareABlankBoard() throws Exception {
    BitBoard board =new BitBoard();
    prepareEmptyBoard(board);

    assertThat(board.layers[BLUE_LAYER].isEmpty(), is(true));
    assertThat(board.layers[COMPLETE_LAYER_MASK].isEmpty(), is(true));
  }


  @Test
  @Parameters({
      // color, column
      "1, 0", "2, 0", "3, 0", "4, 0", "5, 0",
      "1, 1", "2, 1", "3, 1", "4, 1", "5, 1",
      "1, 2", "2, 2", "3, 2", "4, 2", "5, 2",
      "1, 3", "2, 3", "3, 3", "4, 3", "5, 3",
      "1, 4", "2, 4", "3, 4", "4, 4", "5, 4",
      "1, 5", "2, 5", "3, 5", "4, 5", "5, 5",
      })
  public void pushBalls_onFloor(int color, int column) throws Exception {
    BitBoard board =new BitBoard();
    prepareEmptyBoard(board);
    
    P position = board.pushBall(color, column);
    
    assertThat(board.layers[color].isCellSetAt(column, 0), is(true));
    assertThat(board.layers[COMPLETE_LAYER_MASK].isCellSetAt(column, 0), is(true));
    assertThat(position.x, is (column));
    assertThat(position.y, is (0));
  }

  @Test
  public void pushBalls_onTop() throws Exception {
    BitBoard board =new BitBoard();
    prepareEmptyBoard(board);
    
    for (int y=0;y<12;y++) {
      P position = board.pushBall(1, 0);
      assertThat(position.x, is (0));
      assertThat(position.y, is (y));
    }
    
    assertThat(board.layers[1].isCellSetAt(0, 11), is(true));
    assertThat(board.layers[COMPLETE_LAYER_MASK].isCellSetAt(0, 11), is(true));
  }
  
  @Test
  public void getColHeight_testFilledColumn() throws Exception {
    BitBoard board =new BitBoard();
    prepareBoard(board,
        "111111",
        "111111",
        "111111",
        "111111",
        "111111",
        "111111",
        "111111",
        "111111",
        "111111",
        "111111",
        "111111",
        "111111");

    assertThat(board.getColHeight(0), is(12));
  }

  @Test
  public void getColHeight_testEmptyColumn() throws Exception {
    BitBoard board =new BitBoard();
    prepareBoard(board,
        ".11111",
        ".11111",
        ".11111",
        ".11111",
        ".11111",
        ".11111",
        ".11111",
        ".11111",
        ".11111",
        ".11111",
        ".11111",
        ".11111");

    assertThat(board.getColHeight(0), is(0));
  }
  
  @Test
  public void getColHeight_testSemiFilledColumn() throws Exception {
    BitBoard board =new BitBoard();
    prepareBoard(board,
        ".11111",
        ".11111",
        ".11111",
        ".11111",
        ".11111",
        ".11111",
        "011111",
        "111111",
        "211111",
        "311111",
        "411111",
        "511111");

    assertThat(board.getColHeight(0), is(6));
  }
  
  @Test
  public void getTotalHeight_testRandomColumns() throws Exception {
    BitBoard board =new BitBoard();
    prepareBoard(board,
        ".1.1.1",
        ".1.1.1",
        ".1.1.1",
        ".1.1.1",
        ".1.1.1",
        ".1.1.1",
        "01.1.1",
        "11.111",
        "21.111",
        "31.111",
        "41.111",
        "51.111");

    assertThat(board.getTotalColumnHeight(), is(47));
  }
  
  @Test
  public void getMinimalColumnHeight_testRandomColumns() throws Exception {
    BitBoard board =new BitBoard();
    prepareBoard(board,
        ".1.1.1",
        ".1.1.1",
        ".1.1.1",
        ".1.1.1",
        ".1.1.1",
        ".1.1.1",
        "01.1.1",
        "11.111",
        "21.111",
        "311111",
        "411111",
        "511111");

    assertThat(board.getMinimalColumnHeight(), is(3));
  }
    
  @Test
  public void getMinimalColumnHeight_testRandomColumns2() throws Exception {
    BitBoard board =new BitBoard();
    prepareBoard(board,
        ".1.1.1",
        ".1.1.1",
        ".1.1.1",
        ".151.1",
        ".151.1",
        ".151.1",
        "015101",
        "115111",
        "215111",
        "311111",
        "411111",
        "511111");

    assertThat(board.getMinimalColumnHeight(), is(6));
  }
  @Test
  public void prepareABlueBoard() throws Exception {
    BitBoard board =new BitBoard();
    prepareBoard(board,
        "111111",
        "111111",
        "111111",
        "111111",
        "111111",
        "111111",
        "111111",
        "111111",
        "111111",
        "111111",
        "111111",
        "111111");

    assertThat(board.layers[RED_LAYER].isEmpty(), is(true));
    
    assertThat(board.layers[BLUE_LAYER].bitCount(), is(72));
    assertThat(board.layers[COMPLETE_LAYER_MASK].bitCount(), is(72));
  }

  @Test
  public void updateBoard_empty() throws Exception {
    BitBoard board =new BitBoard();
    prepareEmptyBoard(board);
  
    board.update();
    
    assertThat(board.getDebugString(), is(buildDebugString(
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
        "......"
        )));
  }
   
  @Test
  public void updateBoard_fullWithoutHoles() throws Exception {
    BitBoard board =new BitBoard();
    prepareBoard(board,
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "54321X",
        "54321X");
  
    board.update();
    
    assertThat(board.getDebugString(), is(buildDebugString(
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "54321X",
        "54321X"
        )));
  }
  
  @Test
  public void updateBoard_lastRowEmpty() throws Exception {
    BitBoard board =new BitBoard();
    prepareBoard(board,
        "111111",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "54321X",
        "......");
  
    board.update();
    
    assertThat(board.getDebugString(), is(buildDebugString(
        "......",
        "111111",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "54321X"
        )));
  }
  
  @Test
  public void updateBoard_Cheesed() throws Exception {
    BitBoard board =new BitBoard();
    prepareBoard(board,
        "0.2.45",
        ".1.34.",
        "0.2.4.",
        "012345",
        ".1234.",
        "..2345",
        "0123..",
        "012345",
        ".123..",
        "012.45",
        "5.3.10",
        "......");
  
    board.update();
    
    assertThat(board.getDebugString(), is(buildDebugString(
        "......",
        "......",
        "..2...",
        "..2.4.",
        "..2.4.",
        "X1234.",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "X12345",
        "51331X"
        )));
  }
  
  @Test
  public void debugString() throws Exception {
    BitBoard board =new BitBoard();
    prepareBoard(board,
        ".12345",
        "012345",
        ".12345",
        "012345",
        ".12345",
        "012345",
        ".12345",
        "012345",
        ".12345",
        "012345",
        "54321.",
        "543210");

    assertThat(board.getDebugString(), is(buildDebugString(
        ".12345",
        "X12345",
        ".12345",
        "X12345",
        ".12345",
        "X12345",
        ".12345",
        "X12345",
        ".12345",
        "X12345",
        "54321.",
        "54321X"
        )
        ));
  }

  @Test
  public void getFreeColorNeighbors_nothing() throws Exception {
    BitBoard board = new BitBoard();
    prepareEmptyBoard(board);
    
    assertThat(board.getFreeColorsNeighbors(), is(0));
    
  }

  @Test
  public void freeNeighboard_OneLine() throws Exception {
    BitBoard board =new BitBoard();
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
        "111111");

    assertThat(board.getFreeColorsNeighbors(), is(6));
  }
  
  
  // -------------- utils ------------------------
  static public void prepareEmptyBoard(BitBoard board) {
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
  }
  

  static public void prepareBoard(BitBoard board, String... rows) {
    if (rows.length != 12) {
      throw new UnsupportedOperationException("12 rows for board");
    }
    int index = 0;
    for (String row : rows) {
      board.updateRow(index++, row);
    }
    board.buildCompleteLayerMask();
  }

  private String buildDebugString(String...rows) {
    String result = "";
    for (int y=0;y<12;y++) {
      result+=rows[y]+"\n";
    }
    return result;
  }
}
