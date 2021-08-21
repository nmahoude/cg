package pr;

import java.util.ArrayList;
import java.util.List;

public class Cell {
  private static final int NEIGHBORS = 6;
  public static final Cell invalidCell = new Cell(null,-1);
  
  static {
    invalidCell.platinum = -1;
  }
  
  // immutables
  public final int id;
  public Cell neighbors[] = new Cell[6];
  int neighborsFE = 0;
  public int platinum = -1;
  public double attractivness;

  // turn variables
  public int ownerId = -1;
  public int[] pods = new int[4];
  public boolean atWar;
  public int podCount;
  public Cluster cluster;
  boolean clusterFrontier;
  public List<Cell> threats = new ArrayList<>();
  public List<Cell> threathen = new ArrayList<>();
  public int threatCount = 0;
  private final Grid grid;
  
  public Cell(Grid grid, int id) {
    this.grid = grid;
    this.id = id;
    for (int i=0;i<NEIGHBORS;i++) {
      neighbors[i] = invalidCell;
    }
  }
  
  public void addNeighbors(Cell cell) {
    neighbors[neighborsFE++] = cell;
  }

  public int neededTroopsFor(int myId) {
    if (isNeutral()) return 1;
    if (!atWar) {
      return ownerId == myId ? 0 : pods[ownerId];
    } else {
      int myTroops = pods[myId];
      int worstCase = 1;
      for (int i=0;i<4;i++) {
        if (i == myId) continue;
        int delta = Math.max(0, pods[i]-myTroops);
        if (delta > worstCase) {
          worstCase = delta;
        }
      }
      return worstCase;
    }
  }

  public boolean isNeutral() {
    return ownerId == -1;
  }

  public boolean isSafe() {
    for (int c =0;c<neighborsFE;c++) {
      if (neighbors[c].ownerId != ownerId) return false;
    }
    return true;
  }
  
  public int unsafeNeededBots() {
    int needed[] = new int[4];
    for (int c =0;c<neighborsFE;c++) {
      if (neighbors[c].ownerId == -1) continue;
      if (neighbors[c].ownerId != ownerId) needed[neighbors[c].ownerId]+=neighbors[c].pods[neighbors[c].ownerId];
    }
    return Math.max(needed[0], Math.max(needed[1], Math.max(needed[2], needed[3])));
  }
  
  /**
   *  return true if all neighbors are mine
   */
  public boolean neighborsAreMine() {
    for (int i=0;i<neighborsFE;i++) {
      if (neighbors[i].ownerId != ownerId) return false;
    }
    return true;
  }

  public boolean neighborsToEnemy() {
    for (int i=0;i<neighborsFE;i++) {
      if (neighbors[i].ownerId != ownerId && neighbors[i].ownerId >= 0) return true;
    }
    return false;
  }

  public List<Cell> getFreeNeighbors() {
    List<Cell> freeCells = new ArrayList<>();
    for (int i=0;i<neighborsFE;i++) {
      if (neighbors[i].ownerId != ownerId && neighbors[i].podCount == 0) {
        freeCells.add(neighbors[i]);
      }
    }
    return freeCells;
  }

  /** return true if this cell is mine */
  public boolean isMine() {
    return ownerId == Player.myId;
  }

  public boolean haveMyPods() {
  	return myPods() > 0;
  }
  
  public int myPods() {
    return pods[Player.myId];
  }

  public void clusterDFS(Cluster cluster) {
    this.clusterFrontier = false;
    for (int i=0;i<neighborsFE;i++) {
      Cell c =  neighbors[i];
      if (c.cluster != null) continue;
      if (c.ownerId == this.ownerId) {
        c.cluster = cluster;
        cluster.cells.add(c);
        c.clusterDFS(cluster);
      } else {
        this.clusterFrontier = true;
        cluster.frontier.add(c);
      }
    }
  }

  public void calculateAttractivnessByBFS() {
    attractivness = platinum;
    List<Cell> visited = new ArrayList<>(); 
    List<Cell> toVisit = new ArrayList<>();
    toVisit.add(this);
    
    while (!toVisit.isEmpty()) {
      Cell next = toVisit.remove(0);
      visited.add(next);
      
      int distance = next.distanceTo(this);
      if( distance != 0) {
        attractivness += 0.1 * next.platinum / Math.pow(distance, 2);
      }
      
      for (int i=0;i<next.neighborsFE;i++) {
        Cell toAdd = next.neighbors[i];
        if (visited.contains(toAdd) || toVisit.contains(toAdd)) continue;
        toVisit.add(toAdd);
      }
    }
  }
  
  public int distanceTo(Cell cell) {
    if (this.id == cell.id) return 0;
    return this.grid.distances[this.id][cell.id];
  }

  public boolean isEnnemy() {
    return ! (isNeutral() || isMine());
  }

  public List<Cell> neighbors() {
    List<Cell> result = new ArrayList<>();
    for (int i=0;i<neighborsFE;i++) {
      result.add(neighbors[i]);
    }
    return result;
  }

  public boolean isEmpty() {
    return podCount == 0;
  }

  public boolean canDrop() {
    return isNeutral() || isMine();
  }

  @Override
  public String toString() {
  	return ""+id;
  }

	public int countEnemiesAround() {
		return neighbors().stream()
        .filter(Cell::isEnnemy)
        .mapToInt(v -> v.podCount)
        .sum();
	}

	// find the best cell to get to destination
	public Cell findRouteTo(Cell target) {
		if (target == null) {
			throw new RuntimeException("Target is null ...");
		}
		int bestDist = Integer.MAX_VALUE;
		Cell bestCell = null;
		for (Cell n : neighbors) {
			if (n == invalidCell) continue;
			int dist = n.distanceTo(target);
			if (dist < bestDist) {
				bestDist = dist;
				bestCell = n;
			}
		}
		return bestCell;
		
//		Cell next = Arrays.stream(neighbors).filter(n -> n != null).sorted((c1, c2) -> Integer.compare(c1.distanceTo(target), c2.distanceTo(target))).findFirst().orElse(null);
//		return next;
	}

	/**
	 * dead end with no platinum
	 * @return
	 */
	public boolean isForbiden() {
		return this == invalidCell || (neighborsFE == 1 && platinum == 0); 
	}
}
