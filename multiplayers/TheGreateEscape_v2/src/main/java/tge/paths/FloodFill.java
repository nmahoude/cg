package tge.paths;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tge.Cell;
import tge.Player;
import tge.Point;

/**
 * Do  a flood fill with exit impassable
 *
 */
public class FloodFill {
  public static int floodFill(int id) {
    List<Cell> toVisit = new ArrayList<>();
    Set<Cell> visited = new HashSet<>();
        
    toVisit.add(Player.grid.get(Player.agents[id].position));
    int count = 0;
    while (!toVisit.isEmpty()) {
      Cell currentCell = toVisit.remove(0);
      if (visited.contains(currentCell)) continue;
      visited.add(currentCell);
      count++;
      
      for (int i=0;i<4;i++) {
        if (i == currentCell.from) continue;
        if (currentCell.walls[i] != 0) continue;
        if (currentCell.cells[i] == currentCell.invalid) continue;
        if (Cell.heuristicLength(currentCell.cells[i], id) == 0) continue;
        
        Cell nextCell = currentCell.cells[i];
        nextCell.from = (i + 2) % 4;
        toVisit.add(nextCell);
      }
    }
    
    return count;
  }
  
  public static int floodFillFromExit(int id) {
    List<Cell> toVisit = new ArrayList<>();
    Set<Cell> visited = new HashSet<>();
        
    int maxCount = -1;
    for (int exit=0;exit<9;exit++) {
      boolean foundDragon = false;
      Cell nextExit;
      if (id == 0) {
        nextExit = Player.grid.get(Point.get(8, exit));
      } else if (id == 1) {
        nextExit = Player.grid.get(Point.get(0, exit));
      } else {
        nextExit = Player.grid.get(Point.get(exit, 8));
      }
      if (visited.contains(nextExit)) continue;
      toVisit.clear();
      toVisit.add(nextExit);
      
      int count = 0;
      while (!toVisit.isEmpty()) {
        Cell currentCell = toVisit.remove(0);
        if (currentCell.position == Player.agents[id].position) {
          foundDragon = true;
          continue;
        }

        if (visited.contains(currentCell)) continue;
        visited.add(currentCell);
        count++;
        
        for (int i=0;i<4;i++) {
//          if (i == currentCell.from) continue;
          if (currentCell.walls[i] != 0) continue;
          if (currentCell.cells[i] == currentCell.invalid) continue;
          Cell nextCell = currentCell.cells[i];
          //if (Cell.heuristicLength(nextCell, id) == 0) continue;
          nextCell.from = (i + 2) % 4;
          toVisit.add(nextCell);
        }
      }
      if (foundDragon && maxCount < count) {
        maxCount = count;
      }
    }
    
    return maxCount;
  }
}
