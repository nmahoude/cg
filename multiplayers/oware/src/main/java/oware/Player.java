package oware;

import fast.read.FastReader;

public class Player {
	static final State state = new State();
	public static void main(String args[]) {
		FastReader in = new FastReader(System.in);

		// game loop
		while (true) {
			state.read(in);

			
			
//			int bestHouse = directAI();
			int bestHouse = new Minimax().think(state);
			state.playHouse(0, bestHouse); // score the points
			
			// TODO keep track of state & compare with next state to calculate opp's score
			
			System.out.println(""+bestHouse);
		}
	}
	private static int directAI() {
		int bestScore = -1;
		int bestHouse = -1;
		for (int i=0;i<6;i++) {
			State tentative = new State();
			tentative.copy(state);
			tentative.playHouse(0, i);
			System.err.println("state after house "+i);
			tentative.debug();
			if (tentative.score[0]> bestScore) {
				bestScore = tentative.score[0];
				bestHouse = i;
			}
		}
		return bestHouse;
	}
}
