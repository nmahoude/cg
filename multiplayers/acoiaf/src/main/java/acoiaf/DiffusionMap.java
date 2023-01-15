package acoiaf;

import java.util.ArrayList;
import java.util.List;

public abstract class DiffusionMap {
	public static final int MAX_OFFSET = 144;
	public double grid[] = new double[MAX_OFFSET];
	
	public void calculate(State state) {

		List<Pos> toVisit = new ArrayList<>();
		for(Pos p : Pos.allPositions) {
			grid[p.offset] = initValue(p);
			if (isHearth(state, p)) {
				toVisit.add(p);
			}
		}

		List<Pos> newToVisit = new ArrayList<>();
		while (!toVisit.isEmpty()) {
			newToVisit.clear();
			for (Pos p : toVisit) {

				for (Pos n : p.neighbors4dirs) {
					if (!state.canMove(n)) continue;
					
					double value = diffuse(grid[p.offset]);
					if (needToDiffuse(state, p, n, grid[n.offset], value)) {
						grid[n.offset] = value;
						newToVisit.add(n);
					}
				}
			}
			
			List<Pos> swap = toVisit;
			toVisit = newToVisit;
			newToVisit = swap;
		}
	}

	abstract boolean isHearth(State state, Pos pos);
	abstract double initValue(Pos pos) ;
	abstract double diffuse(double currentValue);
	abstract boolean needToDiffuse(State state, Pos from, Pos to, double oldValue, double newValue);

	public void sub(DiffusionMap toSubstract) {
		for (int i=0;i<MAX_OFFSET;i++) {
			this.grid[i]-=toSubstract.grid[i];
		}
	}

}
