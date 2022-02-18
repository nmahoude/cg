package connect4;

import fast.read.FastReader;

/**
 * Drop chips in the columns. Connect at least 4 of your chips in any direction
 * to win.
 **/
public class Player {

	public static boolean inverse;
	public static boolean attack;
	public static int turn = 0;

	State state = State.emptyState();
	AI ai = new AI();

	public static void main(String args[]) {
		FastReader in = new FastReader(System.in);
		StateCache.reset();
		new Player().play(in);
	}

	static int forcedPlay[] = new int[] {};
	int forcedPlayFE = 0;

	private void play(FastReader in) {
		int myId = in.nextInt(); // 0 or 1 (Player 0 plays first)
		int oppId = in.nextInt(); // if your index is 0, this will be 1, and vice versa

		inverse = myId == 1;
		attack = !inverse;

		// game loop
		while (true) {
			turn++;
			state.read(in);
			checkDoubleThread(state);

			if (turn == 1) {
				for (int i = 0; i < 4; i++) {
					ai.think(state);
				}
			}
			int col = ai.think(state);
			
			if (turn == 3 && state.mine == 0L) {
				// there was a steal, now we are inversed
				attack = false;
			}
			
			if (turn == 1 && !inverse) {
				System.out.println("1"); // force a bad steal (the same as Royale ...)
			} else if (turn == 1 && inverse && (false
//					|| state.firstEmptyCell(0) == 1 || state.firstEmptyCell(8) == 1 
					|| state.firstEmptyCell(1) == 1 || state.firstEmptyCell(7) == 1 
					|| state.firstEmptyCell(2) == 1	|| state.firstEmptyCell(6) == 1 
					|| state.firstEmptyCell(3) == 1 || state.firstEmptyCell(5) == 1
					|| state.firstEmptyCell(4) == 1)) {
				System.out.println("STEAL");
				attack = true;
			} else {
				System.out.println(col);
			}

		}
	}

	private void checkDoubleThread(State state2) {
		ThreatAnalyser threatAnalyser = new ThreatAnalyser();
		threatAnalyser.analyse(state2.mine, state2.opp);

		System.err.println("Threats of connect 4");
		State.printGrid(threatAnalyser.myThreatMask[3], threatAnalyser.oppThreatMask[3]);

		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 6; y++) {
				long mask = 0b11L << (7 * x + y);
				if ((threatAnalyser.myThreatMask[3] & mask) == mask) {
					System.err.println("My threat @ " + x + "," + y);
				}
				if ((threatAnalyser.oppThreatMask[3] & mask) == mask) {
					System.err.println("Opp threat @ " + x + "," + y);
				}
			}
		}
	}
}
