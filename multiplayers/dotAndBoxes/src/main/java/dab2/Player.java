package dab2;

import dab.fast.FastReader;

public class Player {

	State state = new State();
	SimpleAI ai = new SimpleAI();
	
	public static void main(String args[]) {
	        FastReader in = new FastReader(System.in);
	        new Player().play(in);
	    }

	private void play(FastReader in) {
		int boardSize = in.nextInt(); // The size of the board.
		String playerId = in.nextString(); // The ID of the player. 'A'=first player, 'B'=second player.

		// game loop
		while (true) {
			
			state.read(in);
			state.print();
	
			ai.think(state);
			
		}
	}
}
