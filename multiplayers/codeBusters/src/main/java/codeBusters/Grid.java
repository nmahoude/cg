package codeBusters;

import java.util.ArrayList;
import java.util.List;

import cgcollections.arrays.FastArray;
import codeBusters.entities.Buster;

public class Grid {
	List<CheckPoint> checkpoints = new ArrayList<>();
	private int steps;
	private int width;
	private int height;
	
	
	public Grid(int steps, int width, int height) {
		this.steps = steps;
		this.width = width;
		this.height = height;

		for (int y=0;y<height / steps;y++) {
			for (int x=0;x<width / steps;x++) {
				CheckPoint cp = new CheckPoint(new P(x*steps, y*steps));
				checkpoints.add(cp);
				
			}
		}
	}
	
	public void update(FastArray<Buster> busters) {
		for (Buster buster : busters) {
			for (CheckPoint cp : checkpoints) {
				if (buster.isInRange2(cp.position, Player.FOG_DISTANCE_2)) {
					cp.lastSeenTurn = Player.turn;
				}
			}
		}
	}

	public CheckPoint findCheckpoint(Buster buster) {
		double bestScore = Double.NEGATIVE_INFINITY;
		CheckPoint bestCp = null;
		for (CheckPoint cp : checkpoints) {
			if (cp.lastSeenTurn == Player.turn) continue;
			
			double score = 0;
			if (cp.lastSeenTurn < 0) {
				score+=10_000_000;
			}
			score -= buster.position.dist(cp.position);
			score -= cp.lastSeenTurn * 1000;
			
			
			if (score > bestScore) {
				bestScore = score;
				bestCp = cp;
			}
		}
		
		
		return bestCp;
	}
	
	
	
	
}
