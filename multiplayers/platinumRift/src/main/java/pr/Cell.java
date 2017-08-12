package pr;

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
}
