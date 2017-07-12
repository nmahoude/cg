package tge.paths;

import java.util.HashSet;
import java.util.Set;

import tge.Cell;
import tge.Player;

/*
 * Existe-t-il un chemin du dragon vers la sortie qui passe par le point ?
 */
public class AccessibleCells {
  Set<Cell> accessibleCells = new HashSet<>();
  int id;
  
  public int go(int id) {
    this.id = id;
    
    for (int y=0;y<9;y++) {
      for (int x=0;x<9;x++) {
        Player.grid.cells[x][y].counter = 0;
      }
    }
    
    Set<Cell> toVisit = new HashSet<>();
    Set<Cell> exploredCells = new HashSet<>();
    dfs(Player.grid.get(Player.agents[id].position), exploredCells);
    
    int count = 0;
    for (int y=0;y<9;y++) {
      for (int x=0;x<9;x++) {
        count+= Player.grid.cells[x][y].counter > 0 ? 1 : 0;
        if (Player.grid.cells[x][y].counter > 0) {
          System.err.println(" in possible : "+x+","+y);
        }
      }
    }
    return count;
  }
  
  public boolean dfs(Cell currentCell, Set<Cell> exploredCells) {
    
    if (exploredCells.contains(currentCell)) return currentCell.counter > 0;
    exploredCells.add(currentCell);
    if (Cell.heuristicLength(currentCell, id) == 0) {
      return true;
    }

    boolean foundExit = false;
    for (int i=0;i<4;i++) {
      if (currentCell.walls[i] == 0 && currentCell.cells[i] != currentCell.invalid) {
        foundExit |= dfs(currentCell.cells[i], exploredCells);
      }
    }
    if (foundExit)  {
      currentCell.counter+=1;
      return true;
    } else {
      return false;
    }
  }
}
