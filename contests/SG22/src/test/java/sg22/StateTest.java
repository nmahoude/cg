package sg22;

import org.junit.jupiter.api.Test;

import fast.read.FastReader;


public class StateTest {

  
  @Test
  void dontUseMemory() throws Exception {
    State state = new State();
    
    state.read(FastReader.fromString(""" 
        ^ MOVE
        ^ 4
        ^ APPLICATION     13 0 0 4 4 0 0 0 0
        ^ APPLICATION     27 0 0 0 0 0 0 4 4
        ^ APPLICATION     12 0 4 0 0 0 0 0 4
        ^ APPLICATION      1 4 0 4 0 0 0 0 0
        ^ 4 4 1 3
        ^ 7 4 0 0
        ^ 6
        ^ HAND            2 1 0 0 0 1 1 0 2 0
        ^ DRAW            1 0 0 0 0 0 0 1 0 3
        ^ DISCARD         0 1 1 1 0 0 0 0 2 7
        ^ AUTOMATED       0 0 0 0 0 1 1 0 0 0
        ^ OPPONENT_CARDS  2 1 2 1 1 2 2 2 10 19
        ^ OPPONENT_AUTOMATED  0 0 0 0 1 0 0 0 0 0
        ^ 0 
        """.replace("^ ", "")));
    
    state.readRemember(FastReader.fromString(
        "^ REMEMBER 0 0 1 -1 "
        .replace("^ ", "")));
    
    
    
  }

}
