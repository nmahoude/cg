package tge.paths;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tge.Cell;
import tge.Player;
import tge.Point;

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
        Player.grid.cells[x][y].registered.clear();
        Player.grid.cells[x][y].from = -1;
      }
    }

    List<Cell> toVisit = new ArrayList<>();
    Set<Cell> exploredCells = new HashSet<>();

    for (int y=0;y<9;y++) {
      Cell cell;
      if (id == 0) cell = Player.grid.get(Point.get(8, y));
      else if (id == 1) cell = Player.grid.get(Point.get(0, y));
      else cell = Player.grid.get(Point.get(y, 8));
      
      cell.counter = 1;
      toVisit.add(cell);
    }
    
    while (!toVisit.isEmpty()) {
      Cell currentCell = toVisit.remove(0);
      if (exploredCells.contains(currentCell)) continue;
      if (currentCell.position == Player.agents[id].position) {
        continue;
      }
      exploredCells.add(currentCell);
      for (int i=0;i<4;i++) {
        if (i == currentCell.from) continue;
        if (currentCell.walls[i] == 0 && currentCell.cells[i] != currentCell.invalid) {
          Cell nextCell = currentCell.cells[i];
          nextCell.registered.add(currentCell);
          if (nextCell.from == -1) nextCell.from = (i + 2) % 4;
          toVisit.add(nextCell);
        }
      }
    }
    Player.grid.get(Player.agents[id].position).propagate();
    
    int count = 0;
    for (int y=0;y<9;y++) {
      for (int x=0;x<9;x++) {
        count+= Player.grid.cells[x][y].counter > 0 ? 1 : 0;
//        if (Player.grid.cells[x][y].counter > 0) {
//          System.err.println(" in possible : "+x+","+y);
//        }
      }
    }
    return count;
  }
  
  public boolean dfs(Cell currentCell, Set<Cell> exploredCells) {
    exploredCells.add(currentCell);
    
    if (currentCell.position == Player.agents[Player.myId].position) {
      currentCell.counter++;
      return true;
    }

    boolean foundDragon = false;
    for (int i=0;i<4;i++) {
      if (currentCell.walls[i] == 0 && currentCell.cells[i] != currentCell.invalid) {
        if (exploredCells.contains(currentCell.cells[i])) {
          currentCell.cells[i].registered.add(currentCell);
          return currentCell.counter > 0;
        }
        foundDragon |= dfs(currentCell.cells[i], exploredCells);
      }
    }
    if (foundDragon)  {
      currentCell.counter+=1;
      for (Cell cell : currentCell.registered) {
        cell.counter++;
      }
      return true;
    } else {
      return false;
    }
  }
}
