package pr;

import java.util.ArrayList;
import java.util.List;

public class Cell {
  private static final int NEIGHBORS = 6;
  public static final Cell invalidCell = new Cell(-1);
  static {
    invalidCell.platinum = -1;
  }
  
  // immutables
  public final int id;
  Cell neighbors[] = new Cell[6];
  int neighborsFE = 0;
  public int platinum;

  // turn variables
  public int ownerId;
  public int[] pods = new int[4];
  public boolean atWar;
  public int totalPods;
  public Continent continent;
  
  public Cell(int id) {
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
      if (neighbors[i].ownerId != ownerId && neighbors[i].totalPods == 0) {
        freeCells.add(neighbors[i]);
      }
    }
    return freeCells;
  }

  public void spread(Continent continent) {
    continent.addCell(this);
    for (int i=0;i<neighborsFE;i++) {
      if (neighbors[i].continent == null) {
        neighbors[i].spread(continent);
      }
    }
  }
}
