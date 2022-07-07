package connect4;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import fast.read.FastReader;

public class MinimaxTest {

  @Test
void shouldNotFindWin() throws Exception {
	String input = "0 "
			+ "...OX....\r\n"
			+ "..XOO....\r\n"
			+ "..OXX....\r\n"
			+ "..OOX.O..\r\n"
			+ "..XOO.X..\r\n"
			+ "..OXX.O..\r\n"
			+ "..XOX.X.."
	        + "\r\n"
	        + "0 0 ";

    FastReader in = new FastReader(input.getBytes());
    Player.inverse = true;
    
    State state = State.emptyState();
    state.read(in);
    
    
    Minimax max = new Minimax();
    int col = -1;
    
    col = max.think(state);
	
	Assertions.assertThat(col).isEqualTo(-1);
  }
	
  public static void main(String[] args) {
	
    String input = "10 "
        + ".........\r\n"
        + ".........\r\n"
        + ".........\r\n"
        + ".....X.X.\r\n"
        + ".X.O...O.\r\n"
        + ".O.X.XXX.\r\n"
        + "OOOX.OXX."
        + "\r\n"
        + "0 0 ";
    
    FastReader in = new FastReader(input.getBytes());
    
    State state = State.emptyState();
    state.read(in);
    
    
    Minimax max = new Minimax();
    int col = -1;
    
    col = max.think(state);
    
    // WARM UP
    for (int i=0;i<500;i++) {
    	col = max.think(state);
    }
    
    System.err.println("Start .... ");
    
    for (int i=0;i<1000;i++) {
		col = max.think(state);
    }

    
    Assertions.assertThat(col).isEqualTo(-1);
  }
}
