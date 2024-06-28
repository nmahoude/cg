package spring2023.map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Map {
  public static final int CELL_EMPTY = 0;
  public static final int CELL_EGGS = 1;
  public static final int CELL_CRYSTAL = 2;

  public static final int MAX_CELLS = 125;
  public static final int MAX_DIST = 125;
  public static MapData cells[];
  
  public static MapData[] myBases;
  public static MapData[] oppBases;

  
  public static int distances[/*source*/][/*target*/] = new int[MAX_CELLS][MAX_CELLS];
  public static Path paths[/*source*/][/*target*/] = new Path[MAX_CELLS][MAX_CELLS];
  
  public static OrderedByDistance cellsByDistances[] = new OrderedByDistance[MAX_CELLS]; 
  
  public static void init(int numberOfCells) {
    cells = new MapData[numberOfCells];
    for (int i = 0; i < numberOfCells; i++) {
      cells[i] = new MapData(i);
    }
  }
  
  public static void calculateBaseDistances() {
    for (MapData source : cells) {
      
      for (MapData cell : cells) {
        distances[source.index][cell.index] = Integer.MAX_VALUE;
        paths[source.index][cell.index] = new Path(source, cell);
      }
      
      List<MapData> toVisit = new ArrayList<>();
      List<MapData> visited = new ArrayList<>();

      toVisit.add(source);
      distances[source.index][source.index] = 0;
      
      int length = 0;

      while (!toVisit.isEmpty()) {
        Set<MapData> nextLayerToVisit = new HashSet<>();

        length++;
        while (!toVisit.isEmpty()) {
          MapData current = toVisit.remove(0);
          visited.add(current);
          
          for (MapData neighbor : current.neighbors) {
            if (toVisit.contains(neighbor)) continue;
            if (visited.contains(neighbor)) continue;
            if (nextLayerToVisit.contains(neighbor)) continue;
            
            nextLayerToVisit.add(neighbor);
            distances[source.index][neighbor.index] = length;
            reconstructPath(source, neighbor, length, paths[source.index][neighbor.index].path);
          }
        }
        toVisit.clear();
        toVisit.addAll(nextLayerToVisit);
      }
    }
    
    // all distances are calculated
    // now for each cell get a list of all cells by distance then index
    for (MapData origin : cells) {
      cellsByDistances[origin.index] = new OrderedByDistance(origin);
      for (MapData c: cells) {
        cellsByDistances[origin.index].allCellsByDistance.add(c);
      }
      
      cellsByDistances[origin.index].allCellsByDistance.sort((c1, c2) 
          -> Integer.compare(
              distances[origin.index][c1.index] * 1000 + c1.index, 
              distances[origin.index][c2.index] * 1000 + c2.index));
      
    }
    
  }

  private static void reconstructPath(MapData source, MapData end, int length, List<MapData> path) {
    
    MapData current = end;
    path.add(end);
    int l = distances[source.index][current.index];
    
    while (l != 0) {
      for (MapData n : current.neighbors) {
        if (distances[source.index][n.index] == l-1) {
          l--;
          current = n;
          path.add(0, n);
          break;
        }
      }
    }
  }

  public static List<MapData> bestPath(MapData source, MapData target) {
    return paths[source.index][target.index].path;
  }
  
  
  
  
}
