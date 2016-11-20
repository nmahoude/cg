package stc3.mcts;

import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import stc2.BitBoard;
import stc2.BitBoardTest;

public class NodeTest {
  @Test
  public void expandNodes_only2possibilities() throws Exception {
    BitBoard board = new BitBoard();
    BitBoardTest.prepareBoard(board, 
        "......",
        "....2.",
        "....3.",
        "....4.",
        "....5.",
        "....1.",
        "....3.",
        "....4.",
        "....4.",
        "...45.",
        "...43.",
        "...33."       );
    Node root = new Node(-1,-1,-1,-1);
    root.board = board;
    root.expand(1, 1);
    
    assertThat(root.unvisited.size(), CoreMatchers.is(20));
    assertThat(root.visited.size(), CoreMatchers.is(0));
  }
}
