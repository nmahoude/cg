package sg22;

import fast.read.FastReader;
import sg22.ais.SmartAI;

public class Perf {

  public static void main(String[] args) {
    
State state = new State();
    
    state.read(FastReader.fromString(""" 
        ^ PLAY_CARD
        ^ 4
        ^ APPLICATION     14 0 0 4 0 4 0 0 0
        ^ APPLICATION      2 4 0 0 4 0 0 0 0
        ^ APPLICATION      1 4 0 4 0 0 0 0 0
        ^ APPLICATION      7 0 4 4 0 0 0 0 0
        ^ 7 4 1 0
        ^ 5 4 0 0
        ^ 4
        ^ HAND            0 0 1 0 0 0 0 3 0 1
        ^ DISCARD         0 0 2 3 0 0 0 1 0 5
        ^ OPPONENT_CARDS  0 4 0 0 0 2 0 0 19 43
        ^ OPPONENT_AUTOMATED  0 1 0 0 0 3 0 0 0 0
        ^ 0 

        """.replace("^ ", "")));
    
    state.readRemember(FastReader.fromString(
        "^ REMEMBER 0 0 1 -1 "
        .replace("^ ", "")));
    
    Player.turn = 1;
    SmartAI ai = new SmartAI();
    ai.think(state);
    
    
    for (int i=0;i<100;i++) {
      Player.start = System.currentTimeMillis();
      ai.think(state);
    }
    
    System.err.println("Warmup finished");
    
    for (int i=0;i<10_000;i++) {
      Player.start = System.currentTimeMillis();
      ai.think(state);
    }
    
  }
}
