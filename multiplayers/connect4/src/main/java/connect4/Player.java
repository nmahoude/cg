package connect4;

import fast.read.FastReader;

/**
 * Drop chips in the columns. Connect at least 4 of your chips in any direction
 * to win.
 **/
public class Player {

	public static boolean inverse;
  State state = State.emptyState();
  AI ai = new AI();
  
	public static void main(String args[]) {
		FastReader in = new FastReader(System.in);
		StateCache.reset();
		new Player().play(in);
	}

	private void play(FastReader in) {
		int myId = in.nextInt(); // 0 or 1 (Player 0 plays first)
		int oppId = in.nextInt(); // if your index is 0, this will be 1, and vice versa

		inverse = myId == 1;
		
		// game loop
		while (true) {
			state.read(in);
			
			int col = ai.think(state);
			System.out.println(col);
			
		}
	}
}
