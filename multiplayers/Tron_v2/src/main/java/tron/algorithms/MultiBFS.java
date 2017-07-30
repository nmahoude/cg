package tron.algorithms;

import cgcollections.arrays.FastArray;
import tron.Player;
import tron.common.Cell;

public class MultiBFS {
  static FastArray cellsToCheckByAgent[] = new FastArray[4];
  static FastArray nextCellsToCheckByAgent[] = new FastArray[4];

  static int[] total= new int[4];
  public static int[] totalRed= new int[4];
  public static int[] totalBlack= new int[4];
  public static boolean[] touching = new boolean[5];
  public static boolean[] fighting = new boolean[5];
  
  
  static {
    for (int i=0;i<4;i++) {
      cellsToCheckByAgent[i] = new FastArray<>(Cell.class, 600);
      nextCellsToCheckByAgent[i] = new FastArray<>(Cell.class, 600);
    }
  }
  
  public static int[] bfs() {
    boolean done[] = new boolean[4];
    return bfs(done);
  }    
  
  public static int[] bfs(boolean done[]) {
    FastArray swapArray;
    reinitBFS();
    Player.grid.resetVisited();
    
    boolean somethingDone = true;
    while (somethingDone) {
      somethingDone = false;
      for (int i=Player.playerFE-1;i>=0;i--) {
        if (done[i]) continue;
        done[i] = true;
        nextCellsToCheckByAgent[i].clear();
        for (int c=0;c<cellsToCheckByAgent[i].length;c++) {
          Cell cell = (Cell) cellsToCheckByAgent[i].elements[c];
          for (int dir=0;dir<4;dir++) {
            int owner = cell.neighbors[dir].owner;
            if (owner != -1) {
              touching[owner] |= (i == 0 && owner >= 0);
              continue;
            }

            int visited = cell.neighbors[dir].visited;
            if (visited != -1) {
              fighting[visited] |= (i == 0 && visited >= 0);
              continue;
            }
            if (((cell.neighbors[dir].position.x + cell.neighbors[dir].position.y) & 0b1) == 0) {
              totalRed[i]++;
            } else {
              totalBlack[i]++;
            }
            
            somethingDone = true;
            done[i] = false;
            cell.neighbors[dir].visited = i;
            nextCellsToCheckByAgent[i].add(cell.neighbors[dir]);
          }
        }
      }
      // swap arrays
      for (int i=0;i<Player.playerFE;i++) {
        swapArray = nextCellsToCheckByAgent[i];
        nextCellsToCheckByAgent[i] = cellsToCheckByAgent[i];
        cellsToCheckByAgent[i] = swapArray;
      }      
    }

    for (int i=0;i<Player.playerFE;i++) {
      total[i] = 2 * Math.min(totalRed[i], totalBlack[i]);
    }
    return total;
  }

  private static void reinitBFS() {
    for (int i=0;i<Player.playerFE;i++) {
      cellsToCheckByAgent[i].clear();
      cellsToCheckByAgent[i].add(Player.agents[i].currentCell);
      total[i] = 0;
      totalBlack[i] = 0;
      totalRed[i] = 0;
      touching[i] = false;
      fighting[i] = false;
    }
  }
}
