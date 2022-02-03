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
        + "O....O...\r\n"
        + "X...XXOO.\r\n"
        + "O...OXXX.\r\n"
        + "OXX.XXXO.\r\n"
        + "OXO.XOXOO"
        + "\r\n"
        + "0 0 ";
    
    FastReader in = new FastReader(input.getBytes());
    
    State state = State.emptyState();
    state.read(in);
    
    
    Minimax max = new Minimax();
    int col = max.think(state);
    
    Assertions.assertThat(col).isEqualTo(-1);
    Assertions.assertThat(max.forbidenColsFE).isNotEqualTo(0);
    for (int i=0;i<max.forbidenColsFE;i++) {
      System.err.println("Forbiden : "+max.forbidenCols[i]);
    }
  }
}
