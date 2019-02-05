package uttt.mcts;

import org.junit.Before;
import org.junit.Test;

import uttt.Player;

public class Perf {
  @Before
  public void setup() {
    NodeCache.init();
  }
 
  public static void main(String[] args) {
    Player.DEBUG = false;
    
    for (int i=0;i<400;i++) {
      NodeCache.resetCache();
      MCTS mcts = new MCTS();
      Player.start = System.currentTimeMillis();
      mcts.think();
    }
//    mcts.output();
  }
}
