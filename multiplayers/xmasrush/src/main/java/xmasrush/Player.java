package xmasrush;

import cgutils.test.TestOutputer;
import fast.read.FastReader;
import xmasrush.ai.move.MoveAI;
import xmasrush.ai.push.PushAI;

public class Player {
  public static State state;
  private MoveAI moveAI;
  private PushAI pushAI;
  
  public static void main(String args[]) {
    TestOutputer.ACTIVE = true;
    
    FastReader in = new FastReader();
    state = new State();
    
    new Player().play(in);
  }

  private void play(FastReader in) {
    moveAI = new MoveAI();
    pushAI = new PushAI();
    while (true) {
      state.read(in);

      if (state.turnType == 1) {
        moveAI.think(state);
        moveAI.output();
      } else {
        pushAI.think(state);
        pushAI.output();
      }
    }
  }
}