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

			String command = "S | W";
			double dx = state.opp.pos.x - state.me.pos.x;
			double dy = state.opp.pos.y - state.me.pos.y;

			if (state.gunCooldown == 0) {
				command = ""+state.me.id+" | F "+dx+" "+dy;
			} else {
				command = "S | W";
			}
			
			
			if (state.missileCount > 0 ) {
				// fire one Missile
				if (state.missileCount % 2 == 0) {
					command += "| M "+dy+" "+(-dx);
				} else {
					command += "| M "+(-dy)+" "+(+dx);
				}
				state.missileCount--;
			}
			System.out.println(command);

			
			
			missileCommands();
			

		}
	}

	private void missileCommands() {
		// missile commands
		for (int m=0;m<state.myMissilesFE;m++) {
			Unit missile = state.myMissiles[m];
			
			if (missile.pos.inRadius(state.opp.pos, 100)) {
				// detonate
				System.out.println(state.myMissiles[m].id+" | D");
			} else {
				// move
				Pos nextMPos = new Pos();
				nextMPos.set(missile.pos);
				nextMPos.add(missile.vec);
				
				
				double vx = missile.vec.vx;
				double vy = missile.vec.vy;
				
				double mdx = state.opp.pos.x - missile.pos.x;
				double mdy = state.opp.pos.y - missile.pos.y;
				
				double ax = mdx - 3 * vx;
				double ay = mdy - 3 * vy;
	
				
				System.out.println(state.myMissiles[m].id+" | A "+ax+" "+ay);
			}
		}
	}
}
