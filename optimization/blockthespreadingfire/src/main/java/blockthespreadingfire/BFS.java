package blockthespreadingfire;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class BFS {
	private static final int dirs[][] = { {1,0}, {-1, 0}, {0, 1}, {0, -1} };
	int dist[];

	private int[] grid;
	private int houseBurnDuration;
	private int treeBurnDuration;
	
	public boolean fireInBoundary = false;
	public int valueInBoundary = 0;
	public int valueOnBoundary = 0;
	
	List<Pos> boundary = new ArrayList<>();
	
	public void calculate(int[] grid, Pos start, int houseBurnDuration, int treeBurnDuration, Pos fireStart, int distThreshold) {
		this.grid = grid;
		this.houseBurnDuration = houseBurnDuration;
		this.treeBurnDuration = treeBurnDuration;

		fireInBoundary = false;
		valueInBoundary = 0;
		valueOnBoundary = 0;
		boundary.clear();
		
		dist = new int[51*51];
		
		Deque<Pos> toVisit = new ArrayDeque<>();

		toVisit.add(start);
		valueInBoundary += (grid[start.offset] == State.HOUSE ? State.houseValue : State.treeValue); 
		
		while (!toVisit.isEmpty()) {
			Pos current = toVisit.pop();
			if (current == fireStart) fireInBoundary = true;
			
			for (int d=0;d<4;d++) {
				Pos dPos = Pos.from(current.x+dirs[d][0],  current.y+dirs[d][1]);
				
				if (grid[dPos.offset] == State.SAFE) continue; // safe
				if (dPos == start) continue;
				if (dist[dPos.offset] != 0) continue; // already visited;
			
				dist[dPos.offset] = dist[current.offset] + value(dPos);
				if (dist[dPos.offset] == distThreshold ) {
					boundary.add(dPos);
					valueOnBoundary += (grid[dPos.offset] == State.HOUSE ? State.houseValue : State.treeValue); 
					if (dPos == fireStart) {
						// fire can't be in boundary
						valueInBoundary = -1;
						boundary.clear();
						fireInBoundary = false;
						return;
					}
				} else if (dist[dPos.offset] < distThreshold) {
					valueInBoundary += (grid[dPos.offset] == State.HOUSE ? State.houseValue : State.treeValue); 
					toVisit.add(dPos);
				}
			}
		}
	}

	private int value(Pos pos) {
		if (grid[pos.offset] == State.HOUSE) return houseBurnDuration;
		if (grid[pos.offset] == State.TREE) return treeBurnDuration;
		return 0;
	}

	public void debug() {
		for (int y=0;y<State.height;y++) {
			for (int x=0;x<State.width;x++) {
				Pos pos = Pos.from(x, y);
				if (grid[pos.offset] == State.SAFE) 
					System.err.print(String.format("%3s ", "#"));
				else 
					System.err.print(String.format("%3d ", dist[pos.offset]));
			}
			System.err.println();
		}
	}
}
