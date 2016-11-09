package stc2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "54321☠",
        "54321☠");
  
    board.update();
    
    assertThat(board.getDebugString(), is(buildDebugString(
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "54321☠",
        "54321☠"
        )));
  }
  
  @Test
  public void updateBoard_lastRowEmpty() throws Exception {
    BitBoard board =new BitBoard();
    prepareBoard(board,
        "111111",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "54321☠",
        "......");
  
    board.update();
    
    assertThat(board.getDebugString(), is(buildDebugString(
        "......",
        "111111",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "54321☠"
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
        "☠1234.",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "☠12345",
        "51331☠"
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
        "☠12345",
        ".12345",
        "☠12345",
        ".12345",
        "☠12345",
        ".12345",
        "☠12345",
        ".12345",
        "☠12345",
        "54321.",
        "54321☠"
        )
        ));
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
