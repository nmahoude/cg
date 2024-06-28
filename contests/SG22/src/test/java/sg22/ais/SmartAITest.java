package sg22.ais;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import fast.read.FastReader;
import sg22.Player;
import sg22.State;
import sg22.nodes.NodeCache;

public class SmartAITest {

  @BeforeAll
  static void setup() {
    NodeCache.reset();
  }
  
  @Test
  void shouldFinishInTime() throws Exception {
    State state = new State();
    
    state.read(FastReader.fromString(""" 
        ^ RELEASE
        ^ 12
        ^ APPLICATION     23 0 0 0 0 4 0 4 0
        ^ APPLICATION      5 4 0 0 0 0 0 4 0
        ^ APPLICATION      8 0 4 0 4 0 0 0 0
        ^ APPLICATION     15 0 0 4 0 0 4 0 0
        ^ APPLICATION     13 0 0 4 4 0 0 0 0
        ^ APPLICATION     24 0 0 0 0 4 0 0 4
        ^ APPLICATION      4 4 0 0 0 0 4 0 0
        ^ APPLICATION      7 0 4 4 0 0 0 0 0
        ^ APPLICATION      1 4 0 4 0 0 0 0 0
        ^ APPLICATION      2 4 0 0 4 0 0 0 0
        ^ APPLICATION     12 0 4 0 0 0 0 0 4
        ^ APPLICATION     25 0 0 0 0 0 4 4 0
        ^ 3 0 1 0
        ^ 5 0 1 1
        ^ 7
        ^ HAND            0 0 1 0 0 0 1 0 0 1
        ^ DRAW            0 0 0 0 0 0 0 0 2 1
        ^ DISCARD         0 0 0 0 0 0 0 0 0 2
        ^ PLAYED_CARDS    0 0 0 0 0 1 0 0 0 0
        ^ AUTOMATED       0 0 0 0 0 0 0 0 2 0
        ^ OPPONENT_CARDS  0 0 0 0 0 2 0 0 2 4
        ^ OPPONENT_AUTOMATED  0 0 0 0 0 0 0 0 2 0
        ^ 0 
        """.replace("^ ", "")));
    
    state.readRemember(FastReader.fromString(
        "^ REMEMBER 0 0 1 -1 "
        .replace("^ ", "")));
    
    Player.DEBUG_RELEASES = true;
    Player.turn = 9;
    Player.start = System.currentTimeMillis()+1_000_000;
    SmartAI ai = new SmartAI();
    ai.think(state);
    
    System.err.println(NodeCache.getCurrentUse());
    
  }
}
