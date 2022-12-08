package spaceShooter;

import fast.read.FastReader;

public class Player {
	static State state = new State();

	public static void main(String args[]) {
		FastReader in = new FastReader(System.in);

		new Player().play(in);
	}

	private void play(FastReader in) {
		// game loop
		while (true) {
			state.read(in);

			
			if (state.gunCooldown == 0) {
				double dx = state.opp.pos.x - state.me.pos.x;
				double dy = state.opp.pos.y - state.me.pos.y;
				System.out.println(""+state.me.id+" | F "+dx+" "+dy);
			} else {
				System.out.println("S | W");
			}
		}
	}
}
