package stc2;

import org.junit.Test;

import stc2.BitBoard;

public class BitBoardTest {

  
  @Test
  public void prepareABlankBoard() throws Exception {
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
        "......");
  }

  static public void prepareBoard(BitBoard board, String... rows) {
    int index = 0;
    for (String row : rows) {
      board.updateRow(12-++index, row);
    }
    
  }
}
