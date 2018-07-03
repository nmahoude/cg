package uttt.mcts;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uttt.Player;
import uttt.state.State;

public class Perf {
  @Before
  public void setup() {
    NodeCache.init();
  }
  @Test
  //@Ignore
  public void simple() throws Exception {
    Player.DEBUG = false;
    MCTS mcts = new MCTS();
    
    for (int i=0;i<100;i++) {
      Player.start = System.currentTimeMillis();
      mcts.think();
    }
    mcts.output();
  }
}
