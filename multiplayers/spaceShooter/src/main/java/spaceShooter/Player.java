package spaceShooter;

import fast.read.FastReader;

public class Player {
	private static final double MISSILE_ACC = 30;
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

			if (state.gunCooldown == 0) {
				// check opp missiles before shooting at enemy
				double dx = 1.0;
				double dy = 0.0;

				boolean foundMissile = false;
				double bestDist = Double.MAX_VALUE;
				Unit bestMissile = null;
				for (int i=0;i<state.oppMissilesFE;i++) {
					Unit missile = state.oppMissiles[i];
					
					if (missile.pos.inRadius(state.me.pos, 1500)) {
						System.err.println("Found missile at "+missile.pos);
						foundMissile = true;
						dx = missile.pos.x - state.me.pos.x;
						dy = missile.pos.y - state.me.pos.y;
						if (dx*dx+dy*dy < bestDist) {
							bestDist = dx*dx+dy*dy;
							bestMissile = missile;
						}
					}
				}
				
				if (bestMissile != null) {
					System.err.println("Best missile is at "+bestMissile.pos);
					dx = bestMissile.pos.x - state.me.pos.x;
					dy = bestMissile.pos.y - state.me.pos.y;
				} else {
					// target enemy ship
					dx = state.opp.pos.x - state.me.pos.x;
					dy = state.opp.pos.y - state.me.pos.y;
				}
				command = ""+state.me.id+" | F "+dx+" "+dy;
			} else {
				command = "S | W";
			}
			
			
			if (state.missileCount > 0 ) {
				double dx = state.opp.pos.x - state.me.pos.x;
				double dy = state.opp.pos.y - state.me.pos.y;
				double length = Math.sqrt(dx*dx+dy*dy);
				dx = MISSILE_ACC * dx / length;
				dy = MISSILE_ACC * dy / length;
				
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
	
				double aLength = Math.sqrt(ax*ax+ay*ay);
				ax = MISSILE_ACC * ax / aLength;
				ay = MISSILE_ACC * ay / aLength;
				
				
				System.out.println(state.myMissiles[m].id+" | A "+ax+" "+ay);
			}
		}
	}
}
