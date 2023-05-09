package acoiaf;

public class FloodFill {
	Pos reachable[] = new Pos[144];
	int reachableFE = 0;
	
	int reachableUnits = 0;
	
	static int simIncrement;
	static int grid[] = new int[12*12];
	
	public void calculate(State state, Pos start, int owner) {
		simIncrement++;	
		reachableFE= 0;
		reachableUnits = 0;
		
		int currentIndex = 0;
		reachable[reachableFE++] = start;
		grid[start.offset] = simIncrement; // to check if we already get there
		
		while (currentIndex != reachableFE) {
			Pos toExpand = reachable[currentIndex++];
			
			for (Pos p : toExpand.neighbors) {
				if (p == Pos.VOID) continue;
				if (grid[p.offset] == simIncrement) continue;
				grid[p.offset] = simIncrement;

				if (state.owner[p.offset] != owner ) continue;
				reachable[reachableFE++] = p;
				if (state.unitId[p.offset] >= 0) reachableUnits++; 
			}
		}
	}
}
