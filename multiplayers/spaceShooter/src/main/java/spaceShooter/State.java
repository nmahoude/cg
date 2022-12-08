package spaceShooter;

import java.text.MessageFormat;

import fast.read.FastReader;

public class State {
	Unit units[] = new Unit[100];
	int unitsFE = 0;
	Unit me;
	Unit opp;
	int gunCooldown;
	int missileCount = 8;
	
	Unit myMissiles[] = new Unit[8];
	int myMissilesFE = 0;
	
	Unit oppMissiles[] = new Unit[8];
	int oppMissilesFE = 0;

	public State() {
		me = new Unit();
		opp = new Unit();
		for (int i=0;i<units.length;i++) {
			units[i] = new Unit();
		}
		for (int i=0;i<8;i++) {
			myMissiles[i] = new Unit();
			oppMissiles[i] = new Unit();
		}
	}
	
	public void read(FastReader in) {
		int unitsCount = in.nextInt(); // number of units on the map
		unitsFE = 0;
		myMissilesFE = 0;
		oppMissilesFE = 0;
		
		for (int i = 0; i < unitsCount; i++) {
			int unitId = in.nextInt(); // unit's unique ID
			int faction = in.nextInt(); // 1 if the unit belongs to the player, -1 if to opponent
			char[] unitType = in.nextChars(); // 'S' for ship, 'B' for bullet, 'M' for missile
			double health = in.nextDouble(); // remaining unit's health points
			double positionX = in.nextDouble(); // X coordinate of the unit's position
			double positionY = in.nextDouble(); // Y coordinate of the unit's position
			double velocityX = in.nextDouble(); // X coordinate of the unit's velocity
			double velocityY = in.nextDouble(); // Y coordinate of the unit's velocity
			double gunCooldown = in.nextDouble(); // number of rounds till the next bullet can be fired if this is a ship, -1 otherwise
			
			System.err.println(MessageFormat.format("^{0} {1} {2} {3} {4} {5} {6} {7} {8}", unitId, faction, unitType[0], health, positionX,  positionY, velocityX, velocityY, gunCooldown));
			
			Unit current;
			if (unitType[0] == 'S') {
				if (faction == 1) {
					current = me ;
					this.gunCooldown = (int)gunCooldown;
				} else {
					current=opp;
				}
			} else if (unitType[0] == 'M') {
				if (faction == 1) {
					current = myMissiles[myMissilesFE++];
				} else {
					current = oppMissiles[oppMissilesFE++];
				}
			} else {
				current = units[unitsFE++];
			}
			current.id = unitId;
			current.pos.set(positionX, positionY);
			current.vec.set(velocityX, velocityY);
		}
		
		System.err.println("My missiles FE "+myMissilesFE);
		System.err.println("Opp missiles FE "+oppMissilesFE);
	}

}
