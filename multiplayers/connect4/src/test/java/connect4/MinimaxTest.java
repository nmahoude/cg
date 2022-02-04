package connect4;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import fast.read.FastReader;

public class MinimaxTest {

  
  @Test
  void debug() throws Exception {
    StateCache.reset();
    
    String input = "0 "
        + ".........\r\n"
        + ".........\r\n"
        + ".........\r\n"
        + "O........\r\n"
        + "X...X....\r\n"
        + "X..OO....\r\n"
        + "XXXOO...."
        + "\r\n"
        + "0 0 ";
    
    FastReader in = new FastReader(input.getBytes());
    
    State state = State.emptyState();
    state.read(in);
    
    
    Minimax max = new Minimax();
    int col = -1;
    
    col = max.think(state);
    
    // WARM UP
    for (int i=0;i<1000;i++) {
    	col = max.think(state);
    }
    
    System.err.println("Start .... ");
    
    for (int i=0;i<100_000;i++) {
		col = max.think(state);
    }

    
    Assertions.assertThat(col).isEqualTo(-1);
    Assertions.assertThat(max.forbidenColsFE).isNotEqualTo(0);
    for (int i=0;i<max.forbidenColsFE;i++) {
      System.err.println("Forbiden : "+max.forbidenCols[i]+" score : "+max.forbidenScore[i]);
    }
  }
}
