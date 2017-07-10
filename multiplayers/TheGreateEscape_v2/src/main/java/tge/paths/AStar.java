package tge.paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import tge.Cell;
import tge.Player;
import tge.Point;

public class AStar {
  public static List<Cell> astar(Cell from, int id) {
    int cameFrom[] = new int[9*9];
    for (int i=0;i<cameFrom.length;i++) {
      cameFrom[i] = -1;
    }
    for (int y=0;y<9;y++) {
      for (int x=0;x<9;x++) {
        Player.grid.cells[x][y].gScore = Double.POSITIVE_INFINITY;
        Player.grid.cells[x][y].fScore = Double.POSITIVE_INFINITY;
      }
    }
    List<Cell> closedSet = new ArrayList<>();
    List<Cell> openSet = new ArrayList<>();
    openSet.add(from);
    
    from.gScore = 0.0;
    from.fScore = Cell.heuristicLength(from, id);
    
    while (!openSet.isEmpty()) {
      Collections.sort(openSet);
      Cell current = openSet.remove(0);
      if (Cell.heuristicLength(current, id) == 0) {
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
        if (!openSet.contains(neighbor)) {
          openSet.add(neighbor);
        }
        double gScore = current.gScore + 1;
        if (gScore > neighbor.gScore) continue;
        
        cameFrom[neighbor.position.x + 9*neighbor.position.y] = current.position.x + 9*current.position.y;
        neighbor.gScore = gScore;
        neighbor.fScore = gScore + Cell.heuristicLength(neighbor, id);
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
}
