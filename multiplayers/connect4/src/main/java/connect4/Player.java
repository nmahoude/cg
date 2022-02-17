package connect4;

import fast.read.FastReader;

/**
 * Drop chips in the columns. Connect at least 4 of your chips in any direction
 * to win.
 **/
public class Player {

	public static boolean inverse;
	public static int turn = 0;
	
  State state = State.emptyState();
  AI ai = new AI();
  
	public static void main(String args[]) {
		FastReader in = new FastReader(System.in);
		StateCache.reset();
		new Player().play(in);
	}

	static int forcedPlay[] = new int [] {};
	int forcedPlayFE = 0;
	private void play(FastReader in) {
		int myId = in.nextInt(); // 0 or 1 (Player 0 plays first)
		int oppId = in.nextInt(); // if your index is 0, this will be 1, and vice versa

		inverse = myId == 1;
		
		// game loop
		while (true) {
		  turn++;
			state.read(in);
			
			if (turn == 1) {
			  for (int i=0;i<4 ;i++) {
			    ai.think(state);
			  }
			}
			int col = ai.think(state);

			if (turn == 1 && !inverse) {
			  System.out.println("2"); // force a bad steal (the same as Royale ...)
			} else if (turn == 1 && inverse && (
//					state.firstEmptyCell(0) == 1 || state.firstEmptyCell(8) == 1 ||
//					state.firstEmptyCell(1) == 1 || state.firstEmptyCell(7) == 1 ||
					state.firstEmptyCell(2) == 1 || state.firstEmptyCell(6) == 1 ||
					state.firstEmptyCell(3) == 1 || state.firstEmptyCell(5) == 1 ||
					state.firstEmptyCell(4) == 1 
					)
					) {
        System.out.println("STEAL"); 
      } else {
        if (col == -1) {
          col = forcedPlay[forcedPlayFE++];
        }
        System.out.println(col);
      }
      
			
		}
	}
}
