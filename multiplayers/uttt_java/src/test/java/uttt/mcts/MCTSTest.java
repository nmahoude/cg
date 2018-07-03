package uttt.mcts;

import org.junit.Before;
import org.junit.Test;

import uttt.Player;
import uttt.state.State;

public class MCTSTest {

  @Before
  public void setup() {
    NodeCache.init();
  }
  @Test
  public void simple() throws Exception {
    MCTS mcts = new MCTS();
    
    Player.start = System.currentTimeMillis();
    
    mcts.think();
    
    mcts.output();
  }

  @Test
  public void twoPlys() throws Exception {
    MCTS mcts = new MCTS();
    
    State state = mcts.getCurrentState();
    state.set(true, 0, 1);
    state.set(false, 2, 3);
    
    Player.start = System.currentTimeMillis();
    
    mcts.think();
    
    mcts.output();
  }


}
