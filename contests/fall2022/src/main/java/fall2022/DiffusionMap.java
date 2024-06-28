package fall2022;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class DiffusionMap {
	public double grid[] = new double[Pos.MAX_OFFSET];
	State state;
	int targetOwner;
	
	public DiffusionMap(int targetOwner) {
		this.targetOwner = targetOwner;
	}

	public void calculate(State state) {
		calculate(state, Collections.emptyList());
	}
	
	public void calculate(State state, List<Pos> forbidenCells) {
		this.state = state;

		List<Pos> toVisit = new ArrayList<>();
		for(Pos p : Pos.allMapPositions) {
			grid[p.o] = initValue(p);
			if (isHearth(state, p)) {
				toVisit.add(p);
			}
		}

		List<Pos> newToVisit = new ArrayList<>();
		while (!toVisit.isEmpty()) {
			newToVisit.clear();
			for (Pos p : toVisit) {
				if ( forbidenCells.contains(p)) continue;
				
				for (Pos n : p.neighbors4dirs) {
					if (!state.canMove(n)) continue;
					
					double value = diffuse(grid[p.o]);
					if (needToDiffuse(state, p, n, grid[n.o], value)) {
						grid[n.o] = value;
						newToVisit.add(n);
					}
				}
			}
			
			List<Pos> swap = toVisit;
			toVisit = newToVisit;
			newToVisit = swap;
		}
	}

	abstract boolean isHearth(State state, Pos p);
	abstract double initValue(Pos p) ;
	abstract double diffuse(double d);
	abstract boolean needToDiffuse(State state, Pos from, Pos to, double oldValue, double newValue);

}
