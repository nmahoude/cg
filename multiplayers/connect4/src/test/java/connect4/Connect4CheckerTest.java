package connect4;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class Connect4CheckerTest {

	
	@Test
	void centerhas16WayToMakeALine() throws Exception {
		int index = 4 * 7 + 3;
		for (int i=0;i<Connect4Checker.connect4MasksFE[index];i++) {
			debugMask(Connect4Checker.connect4Masks[32 * index + i]);
		}
		assertThat(Connect4Checker.connect4MasksFE[index]).isEqualTo(16);
	}
	
	
	private void debugMask(long cells) {
		System.err.println("****** mask "+cells);
	    for (int y=6;y>=0;y--) {
	      for (int x=0;x<9;x++) {
	        long mask = 1L << (7*x+y);
	        if ((cells & mask) != 0 ) {
	          System.err.print("O");
	        } else {
	          System.err.print(".");
	        }
	      }
	      System.err.println();
	    }
	}
}
