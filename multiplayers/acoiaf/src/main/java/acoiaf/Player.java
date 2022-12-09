package acoiaf;

import fast.read.FastReader;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
public class Player {
	State state = new State();

	public static void main(String args[]) {
		FastReader in = new FastReader(System.in);

		new Player().play(in);
	}

	private void play(FastReader in) {
		FloodFill ff = new FloodFill();

		State.readInit(in);

		// game loop
		while (true) {
			state.read(in);

			System.err.println("I can reach ");
			ff.calculate(state, Pos.get(0, 0), 0);
			System.err.println(ff.reachableFE);

			System.err.println("HE can reach ");
			ff.calculate(state, Pos.get(11, 11), 1);
			System.err.println(ff.reachableFE);

			System.out.println("WAIT");
		}
	}
}