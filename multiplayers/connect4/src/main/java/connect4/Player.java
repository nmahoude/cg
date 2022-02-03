package connect4;

import fast.read.FastReader;

/**
 * Drop chips in the columns. Connect at least 4 of your chips in any direction
 * to win.
 **/
public class Player {

	public static boolean inverse;
  State state = State.emptyState();

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
			
			Minimax max = new Minimax();
			int col = max.think(state);
			if (col != -1) {
			  System.err.println("Found on minimax ! "+col);
			} else {
  			if (max.forbidenColsFE != 0) {
  			  System.err.println("Minimax found forbiden cols :");
  			  for (int i=0;i<max.forbidenColsFE;i++) {
  			    System.err.println(max.forbidenCols[i]);
  			  }
  			}
  			
  			col = state.findCol(max.forbidenCols, max.forbidenColsFE);
			}
			
			System.out.println(col);
			
		}
	}
}
