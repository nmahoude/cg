package fall2022.minimax;

import java.util.ArrayList;
import java.util.List;

/** 
 * 
 * resolve a fight between 2 neighbors cells
 * 
 * @author nmahoude
 *
 */
public class MM2DFightDecider {
	
	public List<MM2DResult> resolve(MM2DEntry me, MM2DEntry opp) {
		List<MM2DResult> results = new ArrayList<>();

		int defense = me.total() - opp.movable;
		if (defense < 0) {
			return results; // no way to defend
		}

		int free;
		int maxWithNotWon = -1;
		for (free=0;free<=me.movable ;free++) {
			// check we still can defend
			if (me.total() - free < opp.movable) break;
			
			boolean won = free > opp.total(); // sur de gagner !
			if (!won) maxWithNotWon = Math.max(maxWithNotWon, free);
			if (won) {
				results.add(new MM2DResult(me.movable - free , free, Math.max(0, opp.movable - (me.blocked+me.movable-free)) ,true));
			}
			
		}
		if (maxWithNotWon != -1) {
			results.add(new MM2DResult(me.movable - maxWithNotWon, maxWithNotWon, Math.max(0, opp.movable - (me.blocked+me.movable-maxWithNotWon)) ,false));
		}		
		
		return results;
	}

	public List<MM2DNeutralResult> resolveNeutral(MM2DEntry me, MM2DEntry opp) {
		List<MM2DNeutralResult> results = new ArrayList<>();

		if (me.movable > opp.movable) {
			int neededToWin = opp.movable+1;
			results.add(new MM2DNeutralResult(neededToWin, me.movable-neededToWin, true));
		} else if (me.movable == opp.movable) {
			results.add(new MM2DNeutralResult(me.movable, 0, false));
		} else if (me.movable < opp.movable) {
			results.add(new MM2DNeutralResult(0, me.movable, false));
		}
		
		return results;
	}

	
	
	/*
	 * Objective is to catch the opp cell without losing ours
	 */
	public List<MM2DResult> resolve(int[] me, int[] opp) {
		List<MM2DResult> results = new ArrayList<>();
		
		int defense = (me[0] + me[1]) - opp[0];
		if (defense < 0) {
			return results; // no way to defend
		}
		
		// here we can defend, now can we attack with 'i' units (only with m[0]) ?
		int maxWithNotWon = -1;
		for (int i=0;i<=me[0] ;i++) {
			if (opp[0] - (me[0] +me[1] - i) > 0) break;
			
			boolean won = i > opp[0]+opp[1]; // sur de gagner !
			if (!won) maxWithNotWon = Math.max(maxWithNotWon, i);

			if (won) {
				MM2DResult result = new MM2DResult(me[0]-i, i, Math.max(0, opp[0] - (me[0] - i)), true);
				results.add(result);
			}
			
			if (won) break; // no more needed
		}
		if (maxWithNotWon != -1) {
			MM2DResult result = new MM2DResult(me[0]-maxWithNotWon, maxWithNotWon, Math.max(0, opp[0] - (me[0] - maxWithNotWon)), false);
			results.add(result);
		}
		return results;
	}

	public List<MM2DResult> resolve3D(int[] me, int[] opp) {
		List<MM2DResult> results = new ArrayList<>();
		
		int defense = (me[0] + me[1]) - opp[0];
		if (defense < 0) {
			return results; // no way to defend
		}
		
		// here we can defend, now can we attack (only with m[0] ?
		int maxWithNotWon = -1;
		for (int i=0;i<=me[1] ;i++) {
			if (opp[0] - (me[1] - i) > 0) break;
			
			boolean won = i > opp[0]+opp[1];
			if (!won) maxWithNotWon = Math.max(maxWithNotWon, i);

			if (won) {
				MM2DResult result = new MM2DResult(me[1]-i, 0, i, true);
				results.add(result);
			}
			
			if (won) break; // no more needed
		}
		if (maxWithNotWon != -1) {
			MM2DResult result = new MM2DResult(me[0]-maxWithNotWon, maxWithNotWon, Math.max(0, opp[0] - (me[0] - maxWithNotWon)), false);
			results.add(result);
		}
		return results;
	}
}
