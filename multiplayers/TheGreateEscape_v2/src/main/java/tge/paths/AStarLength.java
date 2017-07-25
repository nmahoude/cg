package tge.paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import tge.Cell;
import tge.Player;
import tge.Point;

public class AStarLength {

  public static double astar(Cell from, int id) {
    return astar(from, id, Collections.EMPTY_LIST);
  }
  
  public static double astar(Cell from, int id, List<Cell> forbidenCells) {

    for (int y=0;y<9;y++) {
      for (int x=0;x<9;x++) {
        Player.grid.cells[x+9*y].gScore = Double.POSITIVE_INFINITY;
        Player.grid.cells[x+9*y].fScore = Double.POSITIVE_INFINITY;
      }
    }
    List<Cell> closedSet = new ArrayList<>();
    closedSet.addAll(forbidenCells);
    List<Cell> openSet = new ArrayList<>();
    openSet.add(from);
    
    from.gScore = 0.0;
    from.fScore = Cell.heuristicLength(from, id);
    
    while (!openSet.isEmpty()) {
      Collections.sort(openSet);
      Cell current = openSet.remove(0);
      if (Cell.heuristicLength(current, id) == 0) {
        return current.gScore;
      }
      closedSet.add(current);
      
      for (int i=0;i<4;i++) {
        if (current.walls[i] != 0) continue;
        if (current.cells[i] == Cell.invalid) continue;
        
        Cell neighbor = current.cells[i];
        if (closedSet.contains(neighbor)) {
          continue;
        }
        double gScore = current.gScore + 1;
        if (gScore > neighbor.gScore) {
          continue;
        }
        
        neighbor.gScore = gScore;
        neighbor.fScore = gScore + Cell.heuristicLength(neighbor, id);
        if (!openSet.contains(neighbor)) {
          openSet.add(neighbor);
        }
      }
    }
    return -1;
  }
  
  // path between 2 cells
  public static double astar(Cell from, Cell target) {
    return astar(from, target, Collections.EMPTY_LIST);
  }
  
  // path between 2 cells
  public static double astar(Cell from, Cell target, List<Cell> forbidenCells) {
    int cameFrom[] = new int[9*9];
    for (int i=0;i<cameFrom.length;i++) {
      cameFrom[i] = -1;
    }
    for (int y=0;y<9;y++) {
      for (int x=0;x<9;x++) {
        Player.grid.cells[x+9*y].gScore = Double.POSITIVE_INFINITY;
        Player.grid.cells[x+9*y].fScore = Double.POSITIVE_INFINITY;
      }
    }
    List<Cell> closedSet = new ArrayList<>();
    closedSet.addAll(forbidenCells);
    PriorityQueue<Cell> openSet = new PriorityQueue<>();
    openSet.add(from);
    
    from.gScore = 0.0;
    from.fScore = Cell.heuristicLength(from, target);
    
    while (!openSet.isEmpty()) {
      Cell current = openSet.poll();
      if (Cell.heuristicLength(current, target) == 0) {
        return current.gScore;
      }
      closedSet.add(current);
      
      for (int i=0;i<4;i++) {
        if (current.walls[i] != 0) continue;
        if (current.cells[i] == Cell.invalid) continue;
        
        Cell neighbor = current.cells[i];
        if (closedSet.contains(neighbor)) {
          continue;
        }
        double gScore = current.gScore + 1;
        if (gScore > neighbor.gScore) continue;
        
        cameFrom[neighbor.position.x + 9*neighbor.position.y] = current.position.x + 9*current.position.y;
        neighbor.gScore = gScore;
        neighbor.fScore = gScore + Cell.heuristicLength(neighbor, target);
        if (!openSet.contains(neighbor)) {
          openSet.add(neighbor);
        }
      }
    }
    return -1;
  }
}
