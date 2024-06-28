package spring2023.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import spring2023.Player;
import spring2023.State;
import spring2023.map.Cell;
import spring2023.map.Map;
import spring2023.map.MapData;

public class Simulation {
  static AntAlloc[][] antAllocs = new AntAlloc[Map.MAX_CELLS][Map.MAX_DIST];

  static public void init() {
    /*
     * for each cell, make a list of all cells at distance d
     */
    for (MapData cell : Map.cells) {
      for (int d = 0; d < Map.MAX_DIST; d++) {
        antAllocs[cell.index][d] = new AntAlloc();
        antAllocs[cell.index][d].origin = cell;
      }
    }

    for (MapData origin : Map.cells) {
      for (MapData target : Map.cells) {
        AntAlloc current = antAllocs[origin.index][Map.distances[origin.index][target.index]];
        current.atDistance.add(target);
      }
    }
  }

  public final java.util.Map<MapData, List<Allocation>> allocations = new HashMap<>();
  
  int antsToDispatch[] = new int[Map.MAX_CELLS];
  int beaconsStrength[] = new int[Map.MAX_CELLS];
  int dispatchedAnts[] = new int[Map.MAX_CELLS];

  public void simulate(State state) {

    List<Cell> ants = new ArrayList<>();
    List<Cell> beacons = new ArrayList<>();

    allocations.clear();
    
    
    int totalAnts = 0;
    int totalBeacons = 0;
    for (Cell cell : state.cells) {
      // TODO precompute !
      if (cell.myAnts > 0) {
        antsToDispatch[cell.index] = cell.myAnts;
        dispatchedAnts[cell.index] = 0;

        totalAnts += cell.myAnts;
        ants.add(cell);
      }

      if (cell.beacon > 0) {
        totalBeacons += cell.beacon;
        beacons.add(cell);
      }
    }

    double scale = 1.0 * totalAnts / totalBeacons;

    for (Cell cell : state.cells) {
      beaconsStrength[cell.index] = (int)(cell.beacon * scale);
      if (cell.beacon > 0 && beaconsStrength[cell.index] == 0) beaconsStrength[cell.index] = 1;
    }
    
    
    
  int distance = 0;
  while (true) {
    boolean noChange = true;
    for (Cell antCell : ants) {
      if (antsToDispatch[antCell.index] == 0) continue; // no more ants to dispatch

      for (MapData md : Map.cells) {
        if (Map.distances[antCell.index][md.index] != distance) continue; // TODO can be optimized if need ?
        noChange = false;
        
        Cell atDist = state.cells[md.index];
        if (beaconsStrength[atDist.index] > 0) {
          // go to this beacon !
          int antsToSend = (int) Math.min(beaconsStrength[atDist.index], antsToDispatch[antCell.index]);
          if (antsToSend == 0) continue;

          // now send to closest cell of the path to reach the atDist Cell!
          MapData next = nextForDispatch(state, antCell.data, atDist.data);

          if (Player.DEBUG_SIMULATION) System.err.println("Send "+antsToSend+" from "+antCell+" to "+atDist);
          antsToDispatch[antCell.index] -= antsToSend;
          dispatchedAnts[next.index] += antsToSend;
          beaconsStrength[atDist.index] -= antsToSend;
          
          allocations.putIfAbsent(antCell.data, new ArrayList<>());
          allocations.get(antCell.data).add(new Allocation(antCell.data, atDist.data, antsToSend));

          
        }
      }
    }
    if (noChange) {
      break;
    }

    distance++;
  }
    
//    for (MapData atDist: Map.cells) {
//      if (beaconsStrength[atDist.index] <= 0) continue;
//      
//      for (MapData antFrom : Map.cellsByDistances[atDist.index].allCellsByDistance) {
//        if (antsToDispatch[antFrom.index] <= 0 ) continue;
//        
//        int antsToSend = (int) Math.min(beaconsStrength[atDist.index], antsToDispatch[antFrom.index]);
//        MapData next;
//        if (antFrom == atDist) {
//          next = antFrom;
//        } else {
//          int currentDist = Map.distances[antFrom.index][atDist.index];
//          MapData bestNext = null;
//          int bestAnts = Integer.MIN_VALUE;
//          for (MapData n : antFrom.neighbors) {
//            if (Map.distances[n.index][atDist.index] != currentDist -1) continue;
//            
//            if (state.cells[n.index].myAnts >= bestAnts) {
//              bestAnts = state.cells[n.index].myAnts;
//              bestNext = n;
//            }
//          }
//          next = bestNext;
//        }
//        // System.err.println("Send "+antsToSend+" from "+antFrom+" to "+atDist);
//        antsToDispatch[antFrom.index] -= antsToSend;
//        dispatchedAnts[next.index] += antsToSend;
//        beaconsStrength[atDist.index] -= antsToSend;
//        
//        allocations.putIfAbsent(antFrom, new ArrayList<>());
//        allocations.get(antFrom).add(new Allocation(antFrom, atDist, next, antsToSend));
//        
//        if (beaconsStrength[atDist.index] == 0) break;
//      }
//    }
    
    
    for (MapData mapData : Map.cells) {
      Cell cell = state.cells[mapData.index];
      cell.myAnts = dispatchedAnts[cell.index] + antsToDispatch[cell.index];
    }

  }
  
  public static MapData nextForDispatch(State state, MapData current, MapData target) {
    if (current == target) return target;
    
    double bestScore = Double.NEGATIVE_INFINITY;
    MapData bestNext = null;
    
    for (MapData n : current.neighbors) {
      if (Map.distances[n.index][target.index] != Map.distances[target.index][current.index] -1) continue;
      
      double score = state.cells[n.index].myAnts - 0.001 * n.index;
      if (score > bestScore) {
        bestScore = score;
        bestNext = n;
      }
    }
    return bestNext;
  
  }
}
