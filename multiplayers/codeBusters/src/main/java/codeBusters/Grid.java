package codeBusters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    return findCheckpoint(buster, Collections.emptySet());
	}
	
	public CheckPoint findCheckpoint(Buster buster, Set<P> alreadyCheckpointed) {
	
	int minLastSeen = Integer.MAX_VALUE;
  double minDist2 = Double.POSITIVE_INFINITY;
  CheckPoint r = null;
  for (CheckPoint checkPoint : checkpoints) {
  	if (alreadyCheckpointed.contains(checkPoint.position)) continue;
      double dist2 = buster.position.dist2(checkPoint.position);
      if (checkPoint.lastSeenTurn < minLastSeen || checkPoint.lastSeenTurn == minLastSeen && dist2 < minDist2) {
          minLastSeen = checkPoint.lastSeenTurn;
          minDist2 = dist2;
          r = checkPoint;
      }
  }
  return r;
}

	public Set<P> around(CheckPoint cp) {
		Set<P> around = new HashSet<>();
	  for (CheckPoint checkPoint : checkpoints) {
	  	if (checkPoint.position.dist2(cp.position) < Player.BUSTER_RANGE_2) {
	  		around.add(checkPoint.position);
	  	}
	  }
		return around;
	}
	
	
}
