package bttc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BoardTest {
  Board board;
  
  @Before
  public void setup() {
    board = new Board();
  }
  
  @Test
  public void boardScores() throws Exception {
    prepareBoard(
        "...................................",
        "...................................",
        "...................................",
        "...................................",
        "...................................",
        "...................................",
        "...................................",
        "...................................",
        "...................................",
        "...................................",
        "...................................",
        "...................................",
        "...................................",
        "...................................",
        "...................................",
        "...................................",
        "...................................",
        "...................................",
        "...................................",
        "...............................0123"
        );
    
    assertThat(board.free, is(700-4));
    assertThat(board.scores[Board.PLAYER1], is(1));
    assertThat(board.scores[Board.PLAYER2], is(1));
    assertThat(board.scores[Board.PLAYER3], is(1));
    assertThat(board.scores[Board.PLAYER4], is(1));
  }
  
  public void  prepareBoard(String... rows) {
    board.reinit();
    for (int y=0;y<rows.length;y++) {
      board.addRow(y, rows[y]);
    }
  }
  
}
