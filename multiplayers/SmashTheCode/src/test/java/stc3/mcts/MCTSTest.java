package stc3.mcts;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import stc2.BitBoard;
import stc2.BitBoardTest;
import stc3.game.GameState;
import stc3.game.Pair;
import stc3.game.PlayerInfo;

public class MCTSTest {

  @Test
  public void fourSingleColors() throws Exception {
    
    BitBoard board = new BitBoard();
    BitBoardTest.prepareBoard(board, 
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
        "1.....",
        "2....."
        );
    PlayerInfo pi = new PlayerInfo();
    pi.board = board;
    pi.pairs = new Pair[8];
    pi.pairs[0] = new Pair(3, 4);
    
    MCTS mcts = new MCTS();
    mcts.maxDepth = 1;
    Node root = mcts.run(pi, 22);
    
    assertThat(root.findBestChild().score, is(-4.0));
  }

  
  @Test
  public void destruction() throws Exception {
    
    BitBoard board = new BitBoard();
    BitBoardTest.prepareBoard(board, 
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
        "2.....",
        "22...."
        );
    PlayerInfo pi = new PlayerInfo();
    pi.board = board;
    pi.pairs = new Pair[8];
    pi.pairs[0] = new Pair(2,1);
    
    MCTS mcts = new MCTS();
    mcts.maxDepth = 1;
    Node root = mcts.run(pi, 22);
    
    assertThat(root.findBestChild().score, is(-1.0));
  }
}
