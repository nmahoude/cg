package cultistwars;

import java.util.ArrayList;
import java.util.List;

public class BFS {
	
	private static final int INFINITY = Integer.MAX_VALUE;
	public int[][] dists = new int[13][7];
	
	public void init(int[][] grid, Pos from) {
		for (int y=0;y<7;y++) {
			for (int x=0;x<13;x++) {
				dists[x][y] = INFINITY;
			}
		}
		
		
		List<Pos> toVisit = new ArrayList<>();
		toVisit.add(from);
		dists[from.x][from.y] = 0;
		
		int[] dx = { 1, -1, 0, 0};
		int[] dy = { 0, 0, 1, -1};
		
		while (!toVisit.isEmpty()) {
			Pos current = toVisit.remove(0);
			for (int i=0;i<4;i++) {
				Pos next = current.delta(dx[i], dy[i]);
				if (next == Pos.WALL || grid[next.x][next.y] == State.WALL)	continue;
				if (dists[next.x][next.y] != INFINITY) continue; // already seen
				
				dists[next.x][next.y] = dists[current.x][current.y]+1; 
				
				if (grid[next.x][next.y] != 0 ) continue; // can't walk over
				toVisit.add(next);
			}
		}
		
		
		debug();
	}

	private void debug() {
		System.err.println("BFS : ");
		for (int y=0;y<7;y++) {
			for (int x=0;x<13;x++) {
				System.err.print(String.format("%3d ", dists[x][y] == INFINITY ? 999 : dists[x][y]));
			}
			System.err.println();
		}
	}
	public int dist(Pos to) {
		return dists[to.x][to.y];
	}
}
