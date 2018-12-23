package bttc;

import org.junit.Test;

public class AITestPerf {

  
  
  @Test
  public void perf() throws Exception {
    Map map = new Map();
    P currentPos = new P (17, 10);
    
    AI ai = new AI();
    
    for (int i=0;i<1000;i++) {
      ai.think(currentPos, map);
    }
  }
}
