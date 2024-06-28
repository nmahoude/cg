package spring2021;

import java.util.HashSet;
import java.util.Set;

public class Cell {
	public static final Cell WALL = new Cell(37);
	
	public static final Cell[] cells  = new Cell[38];
	public static final int shadowIndexes[][][] = new int[64][8][4]; // cell index,sun dir,  cellsIndex 

	public static final int distanceIndexes[][] = new int[37][37]; // cell index, cellIndex  => distance from in order
	public static final int distanceIndexesFE[][] = new int[37][8];
	public int distances[] = new int[38];
	
	static {
		init();
	}
	
	final Cell[] neighbors = new Cell[6];
	public final int index;
	
	Cell(int index) {
		this.index = index;
		for (int i=0;i<6;i++) {
			neighbors[i] = WALL;
		}
	}
	
	@Override
	public String toString() {
		return "C-"+index;
	}

	public static void init() {
		for (int i=0;i<37;i++) {
			cells[i] = new Cell(i);
		}
		cells[37] = WALL;
		for (int i=0;i<6;i++) {
			WALL.neighbors[i] = WALL;
		}
	}
	
	public static void precalculate() {
		// shadows
		for (int dir = 0;dir<6;dir++) {
			for (int index=0;index<37;index++) {
				Cell current = cells[index];
				for (int dx = 0;dx<3;dx++) {
					current = current.neighbors[dir];
					shadowIndexes[index][dir][dx] = current.index;
				}
			}
		}

		// distances
		for (int index=0;index<37;index++) {
			Set<Cell> visited = new HashSet<>();
			Set<Cell> toVisit = new HashSet<>();
			Set<Cell> toVisit2 = new HashSet<>();
			toVisit.add(cells[index]);

			cells[index].distances[37] = Integer.MAX_VALUE;
			for (int d=0;d<8;d++) {
				distanceIndexesFE[index][d] = 0;
			}
			
			int distance = 0;
			while (!toVisit.isEmpty()) {
				for (Cell current : toVisit) {
					visited.add(current);
					distanceIndexes[index][distanceIndexesFE[index][distance]++] = current.index;
					cells[index].distances[current.index] = distance;
					for (Cell n : current.neighbors ) {
						if (n == WALL) continue;
						if (visited.contains(n)) continue;
						if (toVisit.contains(n)) continue;
						
						toVisit2.add(n);
					}					
				}
				distance++;
				distanceIndexesFE[index][distance] = distanceIndexesFE[index][distance-1]; 
				visited.addAll(toVisit);
				toVisit.clear();
				toVisit.addAll(toVisit2);
				toVisit2.clear();
			}
		}
	}
}
