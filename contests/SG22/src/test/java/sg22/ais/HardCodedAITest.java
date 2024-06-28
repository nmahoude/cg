package sg22.ais;

import sg22.Actions.Action;

public class HardCodedAITest {

  
  @org.junit.jupiter.api.Test
  void readActions() throws Exception {
    
    HardCodedAI ai = new HardCodedAI();
    for (Action action : ai.actions ) {
      System.err.println(action);
    }
  }
}
