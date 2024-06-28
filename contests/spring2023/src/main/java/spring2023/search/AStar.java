package spring2023.search;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import spring2023.State;
import spring2023.map.Cell;
import spring2023.map.Map;
import spring2023.map.MapData;
import spring2023.map.Path;

public class AStar {
  
  MapData cameFrom[] = new MapData[Map.MAX_CELLS];
  double gScore[] = new double[Map.MAX_CELLS];
  double fScore[] = new double[Map.MAX_CELLS];
  
  public Path path = new Path(); 
  private double bestScore;
  
  Set<Cell> starts = new HashSet<>(5);
  
  public double search(State state,Cell start, Cell target) {
    return search(state, new SimpleCost(), start, target);
  }

  
  public double search(State state,AStarCost cost, Cell start, Cell target) {
    starts.clear();
    starts.add(start);
    return search(state, cost, starts, target);
  }

  
  public double search(State state, Set<Cell> starts, Cell target) {
    return search(state, new SimpleCost(), starts, target);
  }
  
  public double search(State state, AStarCost costFunction, Set<Cell> starts, Cell target) {
    reset();
    
    PriorityQueue<MapData> openSet = new PriorityQueue<>((o1, o2) -> Double.compare(fScore[o1.index], fScore[o2.index]));
    for (Cell start : starts) {
      gScore[start.index] = costFunction.ofStart(start);
      fScore[start.index] = 2*Map.distances[start.index][target.index];
      openSet.add(start.data);
    }
    
    while(!openSet.isEmpty()) {
      MapData current = openSet.poll();
      
      if (current == target.data) {
        rebuildPath(state, target);
      }

      if (gScore[current.index] > gScore[target.index]) continue;
      
      for (MapData next : current.neighbors) {
        double tentativeGScore = gScore[current.index] + costFunction.costOf(state.cells[getSource(current).index], state.cells[next.index]);
        
        if (tentativeGScore < gScore[next.index]) {
          gScore[next.index] = tentativeGScore;
          fScore[next.index] = gScore[next.index] + 2*Map.distances[current.index][target.index];
          
          
          cameFrom[next.index] = current;
          
          // force reinsertion with correct order
          openSet.remove(next);
          openSet.add(next);
        }
      }
    }
    
    
    return bestScore;
  }

  private MapData getSource(MapData current) {
    MapData previous = current;
    while (cameFrom[current.index] != null) {
      previous = current;
      current = cameFrom[current.index];
    }
    return previous;
  }
  
  private void rebuildPath(State state, Cell target) {
    path.clear();
    path.target = target.data;
    
    MapData current = target.data;
    path.path.add(current);
    
    while (cameFrom[current.index] != null) {
      
      path.path.add(0,cameFrom[current.index]);
      current = cameFrom[current.index];
    }
    path.origin = current;
    
    bestScore = gScore[target.index];
    
    // System.err.println("Path : "+path+" with score "+bestScore);
  }


  private void reset() {
    bestScore = Double.MAX_VALUE;
    path.clear();

    for (int i=0;i<Map.MAX_CELLS;i++) {
      cameFrom[i] = null;
      gScore[i] = Double.MAX_VALUE;
      fScore[i] = Double.MAX_VALUE; 
    }
  }
}
