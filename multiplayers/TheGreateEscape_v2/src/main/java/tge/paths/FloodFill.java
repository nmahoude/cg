package tge.paths;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import tge.Cell;
import tge.Player;
import tge.Point;

/**
 * Do  a flood fill with exit impassable
 *
 */
public class FloodFill {
  private boolean foundDragon;
  private int id;
  private Point playerPos;

  public static int floodFill(int id) {
    List<Cell> toVisit = new ArrayList<>();
    Player.grid.resetVisited();
        
    toVisit.add(Player.grid.get(Player.agents[id].position));
    int count = 0;
    while (!toVisit.isEmpty()) {
      Cell currentCell = toVisit.remove(0);
      if (currentCell.visited()) continue;
      currentCell.visit();
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
    Deque<Cell> toVisit = new ArrayDeque<>();
    
    Player.grid.resetVisited();
    Cell nextExit = null;

    int maxCount = -1;
    while (nextExit != Cell.invalid) {
    if (nextExit == null) {
      if (id == 0) {
        nextExit = Player.grid.get(Point.get(8, 0));
      } else if (id == 1) {
        nextExit = Player.grid.get(Point.get(0, 0));
      } else {
        nextExit = Player.grid.get(Point.get(0, 8));
      }
      
    } else {
      if (id == 0) {
        nextExit = nextExit.cells[Cell.DOWN];
      } else if (id == 1) {
        nextExit = nextExit.cells[Cell.DOWN];
      } else {
        nextExit = nextExit.cells[Cell.RIGHT];
      }
    }
      boolean foundDragon = false;
      if (nextExit.from != 0) continue;
      toVisit.clear();
      toVisit.add(nextExit);
      
      int count = 0;
      while (!toVisit.isEmpty()) {
        Cell currentCell = toVisit.pop();
        if (currentCell.position == Player.agents[id].position) {
          foundDragon = true;
          continue;
        }

        if (currentCell.from != 0) continue;
        currentCell.from = 1;
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
  
  public int floodFillFromExit_dfs(int id) {
    this.id = id;
    playerPos = Player.agents[id].position;

    Player.grid.resetVisited();
    Cell nextExit = null;
    if (id == 0) {
      nextExit = Player.grid.get(Point.get(8, 0));
    } else if (id == 1) {
      nextExit = Player.grid.get(Point.get(0, 0));
    } else {
      nextExit = Player.grid.get(Point.get(0, 8));
    }

    int minCount = -1;
    do {
      foundDragon = false;
      int count = floodfill_dfs(nextExit);
      if (foundDragon && minCount < count) {
        minCount = count;
      }
      
      if (id == 2) {
        nextExit = nextExit.cells[Cell.RIGHT];
      } else {
        nextExit = nextExit.cells[Cell.DOWN];
      }
      
    } while (nextExit != Cell.invalid);
    
    return minCount;
  }
  
  private int floodfill_dfs(Cell currentCell) {
    if (currentCell.position == playerPos) {
      foundDragon = true;
      return 0;
    }
    if (currentCell.from != 0) return 0;
    currentCell.from = 1;
    int count = 1;
    
    for (int i=0;i<4;i++) {
      if (currentCell.walls[i] != 0) continue;
      Cell nextCell = currentCell.cells[i];
      if (nextCell == currentCell.invalid) continue;
      if (nextCell.from != 0) continue;
      count += floodfill_dfs(nextCell);
    }
    return count;
  }
}
