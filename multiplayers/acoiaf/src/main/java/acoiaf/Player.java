package acoiaf;

import java.util.List;

import fast.read.FastReader;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
public class Player {
	State state = new State();
	public static AI ai = new AI();
	
	public static void main(String args[]) {
		FastReader in = new FastReader(System.in);

		new Player().play(in);
	}

	private void play(FastReader in) {

		State.readInit(in);

		// game loop
		while (true) {
			state.read(in);

			List<Action> actions = ai.think(state);

			for (Action a : actions) {
				System.out.print(a+";");
			}
			System.out.println("MSG T:"+State.turn);
		}
	}
}