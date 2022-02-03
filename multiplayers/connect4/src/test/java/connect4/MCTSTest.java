package connect4;

import org.junit.jupiter.api.Test;

public class MCTSTest {

  
  @Test
  void debug() throws Exception {
    State state = State.emptyState();
    StateCache.reset();
    
    
    MCTS mcts = new MCTS();
    int col = mcts.think(state);
    
    System.err.println(col);
  }
}
