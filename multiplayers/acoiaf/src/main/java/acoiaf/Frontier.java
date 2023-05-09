package acoiaf;

import java.util.ArrayList;
import java.util.List;

public class Frontier {

	List<Pos> frontier = new ArrayList<>();
	
	
	public void calculate(State state, DiffusionMap myMap, DiffusionMap oppMap) {
		
		frontier.clear();
		
		for (Pos pos : Pos.allPositions) {
			if (!state.isWalkable(pos)) continue;
			
			if (myMap.grid[pos.offset] == oppMap.grid[pos.offset]) {
				frontier.add(pos);
				continue;
			}
			
			boolean hasBlue = false;
			boolean hasRed = false;
			for (Pos n : pos.neighbors4dirs) {
				
				if (myMap.grid[n.offset] > oppMap.grid[n.offset]) {
					hasRed = true;
				}
				if (myMap.grid[n.offset] < oppMap.grid[n.offset]) {
					hasBlue= true;
				}
			}
			
			if (hasRed && hasBlue) {
				frontier.add(pos);
			}
			
		}
	}

	public List<Pos> frontier() {
		return frontier;
	}
}
