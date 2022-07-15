package csb;

import csb.ai.AG;
import fast.read.FastReader;

public class Player {
  private static final AG ai = new AG();
  public static long start = 0L;
  
  public final State state = new State();

  public static void main(String args[]) {
    FastReader in = new FastReader(System.in);
    Player player = new Player();
    player.play(in);
  }

  private void play(FastReader in) {
    readGlobal(in);
    // game loop
    while (true) {
    	
      readTurn(in);
      start = System.currentTimeMillis();

      ai.think(state);
    }
  }

  public void readGlobal(FastReader in) {
    state.readInit(in);
  }

  public void readTurn(FastReader in) {
    state.readTurn(in);
  }

	public void readState(FastReader in) {
		state.readState(in);
	}

}
