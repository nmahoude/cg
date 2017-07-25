package tge.paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import tge.Cell;
import tge.Grid;
import tge.Player;
import tge.Point;

public class AStar {

  public static List<Cell> astar(Cell from, int id) {
    return astar(from, id, Collections.EMPTY_LIST);
  }
  
  public static List<Cell> astar(Cell from, int id, List<Cell> forbidenCells) {

    int cameFrom[] = new int[9*9];
    for (int i=0;i<cameFrom.length;i++) {
      cameFrom[i] = -1;
    }
    for (int y=0;y<9;y++) {
      for (int x=0;x<9;x++) {
        Player.grid.cells[x+9*y].gScore = Double.POSITIVE_INFINITY;
        Player.grid.cells[x+9*y].fScore = Double.POSITIVE_INFINITY;
        Player.grid.cells[x+9*y].from = 0;
      }
    }
    List<Cell> closedSet = new ArrayList<>();
    closedSet.addAll(forbidenCells);
    PriorityQueue<Cell> openSet = new PriorityQueue<>();
    openSet.add(from);
    from.from = 1;
    
    from.gScore = 0.0;
    from.fScore = Grid.heuristicLength(from.position, id);
    
    while (!openSet.isEmpty()) {
      Cell current = openSet.poll();
      if (Grid.heuristicLength(current.position, id) == 0) {
        return constructPath(cameFrom, current);
      }
      closedSet.add(current);
      current.from = 2;
      
      for (int i=0;i<4;i++) {
        if (current.walls[i] != 0) continue;
        Cell neighbor = current.cells[i];
        if (neighbor.from == 2) {
          continue;
        }
        double gScore = current.gScore + 1;
        if (gScore > neighbor.gScore) {
          continue;
        }
        
        cameFrom[neighbor.position.x + 9*neighbor.position.y] = current.position.x + 9*current.position.y;
        neighbor.gScore = gScore;
        neighbor.fScore = gScore + Grid.heuristicLength(neighbor.position, id);
        if (neighbor.from != 1) {
          openSet.add(neighbor);
          neighbor.from = 1;
        }
      }
    }
    return new ArrayList<>();
  }
  
  private static List<Cell> constructPath(int[] cameFrom, Cell current) {
    List<Cell> path = new ArrayList<>();
    path.add(path.size(), current);
    int index = current.position.x + 9*current.position.y;
    while (cameFrom[index] != -1) {
      int y = cameFrom[index] / 9;
      int x = cameFrom[index] - y * 9;
      current = Player.grid.get(Point.get(x, y));
      index = current.position.x + 9*current.position.y;
      path.add(0, current);
    }
    return path;
  }

  // path between 2 cells
  public static List<Cell> astar(Cell from, Cell target) {
    return astar(from, target, Collections.EMPTY_LIST);
  }
  
  // path between 2 cells
  public static List<Cell> astar(Cell from, Cell target, List<Cell> forbidenCells) {
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
        return constructPath(cameFrom, current);
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
    return new ArrayList<>();
  }
}
