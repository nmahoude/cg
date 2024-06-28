package fall2022;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fall2022.sim.Sim;

/**
 * 
 * @author nmahoude
 *
 */
public class TimeTraveler {
	public static final int MAX_DEPTH = 11;
	private  TTSlice slices[] = new TTSlice[MAX_DEPTH];
	public Map<Integer, List<Pos>> positions = new HashMap<>();
	private State original;
	
	public int myMatterBonus[] = new int[MAX_DEPTH];
	public int myTotalMatterBonus = 0;
	public int oppMatterBonus[] = new int[MAX_DEPTH];
	public int oppTotalMatterBonus = 0;
	
	
	public TimeTraveler() {
		for (int i=0;i<MAX_DEPTH;i++) {
			slices[i] = new TTSlice();
			positions.put(i, new ArrayList<>());
		}
	}
	
	public void init(State original) {
		this.original = original;
		// initialize all slices of time
		slices[0].copyFrom(original);

		myTotalMatterBonus = 0;
		oppTotalMatterBonus = 0;
		myMatterBonus[0] = 0;
		oppMatterBonus[0] = 0;
		
		for (int i=1;i<MAX_DEPTH;i++) {
			slices[i].copyFrom(slices[i-1]);
			Sim.oneTurn(slices[i].state);
			
			int delta;
			delta = slices[i].state.myMatter - slices[i-1].state.myMatter;
			myMatterBonus[i] = delta;
			myTotalMatterBonus+=delta;
			
			delta = slices[i].state.oppMatter - slices[i-1].state.oppMatter;
			oppMatterBonus[i] = delta;
			oppTotalMatterBonus+=delta;
		}
	}
	
	private static int[] matrix = new int[Pos.MAX_OFFSET*MAX_DEPTH];
	private static int MATRIX_INDEX;
	public void bfsInTime(Pos arriveAtT0) {
		MATRIX_INDEX++;
		
		for (int i=0;i<MAX_DEPTH;i++) {
			positions.get(i).clear();
		}

		positions.get(0).add(arriveAtT0);
		
		for (int time=1;time<MAX_DEPTH;time++) {
			List<Pos> nextPositionsAtSlice = positions.get(time);
			
			for (Pos p : positions.get(time-1)) {
				if (!slices[time].state.canMove(p)) continue;
		
				for (Pos n : p.neighbors4dirs) {
					if (matrix[n.o + Pos.MAX_OFFSET * time] == MATRIX_INDEX+1) continue; // already got there at the same time
					
					matrix[n.o + Pos.MAX_OFFSET * time] = MATRIX_INDEX+1; // won't go back another time
					nextPositionsAtSlice.add(n);
				}
			}
			positions.get(time).addAll(nextPositionsAtSlice);
		}
	}
	
	public Set<Pos> forbidenCells() {
		Set<Pos> forbidenCells = new HashSet<>();
		
		// find all potential forbiden cells (the one that will disappear)
		Set<Pos> toCheck = new HashSet<>();
		for (Pos current : Pos.allMapPositions) {
			if (original.rec[current.o] == 0) continue;
			for (Pos n : current.neighbors4dirs) {
				if (original.canMove(n)) {
					toCheck.add(n);
				}
			}
		}
		
		for (Pos current : toCheck) {
			if (!original.canMove(current)) continue;
			
			this.bfsInTime(current);
			if (this.positions.get(TimeTraveler.MAX_DEPTH-1).isEmpty()) {
				forbidenCells.add(current);
			}
		}
		return forbidenCells;
	}

	public TTSlice sliceAt(int time) {
		if (time >= MAX_DEPTH) return slices[MAX_DEPTH-1];
		return slices[time];
	}
}
