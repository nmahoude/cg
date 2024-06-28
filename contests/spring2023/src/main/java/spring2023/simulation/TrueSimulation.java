package spring2023.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import spring2023.State;
import spring2023.map.Cell;
import spring2023.map.Map;
import spring2023.map.MapData;

public class TrueSimulation {

  public void simulate(State state) {
    State workState = new State();
    workState.copyFrom(state);
    
    
    State nextState = new State();
    nextState.copyFrom(state);
    for (int i=0;i<State.numberOfCells;i++) {
      nextState.cells[i].myAnts = 0;
    }
    
    List<Allocation> allocations = allocatePairs(workState);
    
    System.err.println("Allocations after simulation");
    for (Allocation a : allocations) {
      System.err.println(""+a);
    }
    workState.copyFrom(state);
    
    for (Allocation alloc : allocations) {
      MapData from = alloc.source;
      MapData next = nextForDispatch(workState, alloc.source, alloc.target);
      System.err.println("for ("+alloc+") , next is "+next);
      int amount = alloc.antsToSend;
      //nextState.cells[from.index].myAnts-=amount;
      nextState.cells[next.index].myAnts+=amount;
    }
    
    state.copyFrom(nextState);
    
  }

  private List<Allocation> allocatePairs(State state) {
    int[] wiggleRooms = new int[Map.MAX_CELLS];
    
    List<Allocation> allocations = new ArrayList<>();
    int antSum = 0;
    for (Cell cell : state.cells) {
      antSum += cell.myAnts;
    }

    int beaconSum = 0;
    for (Cell cell : state.cells) {
      if (cell.beacon == 0) continue;
      beaconSum += cell.beacon;
    }

    double scalingFactor = (double) antSum / beaconSum;
    for (Cell cell : state.cells) {
      if (cell.beacon == 0) continue;

      int highBeaconValue = (int) Math.ceil(cell.beacon * scalingFactor);
      int lowBeaconValue = (int) (cell.beacon * scalingFactor);
      cell.beacon = Math.max(1, lowBeaconValue);
      wiggleRooms[cell.index] = highBeaconValue - cell.beacon;
    }

    List<AntBeaconPair> allPairs = new ArrayList<>();

    for (Cell antCell : state.cells) {
      if (antCell.myAnts == 0) continue;
      
      for (Cell beaconCell : state.cells) {
        if (beaconCell.beacon == 0) continue;
        
        if (Map.distances[antCell.index][beaconCell.index] != -1) {
          AntBeaconPair pair = new AntBeaconPair(antCell, beaconCell);
          allPairs.add(pair);
        }
      }
    }

    Collections.sort(allPairs, Comparator.comparing((AntBeaconPair pair) -> Map.distances[pair.beacon.index][pair.ant.index])
        // Tie-breakers
        .thenComparing(pair -> pair.ant.index).thenComparing(pair -> pair.beacon.index));

    boolean stragglers = false;
    while (!allPairs.isEmpty()) {
      for (AntBeaconPair pair : allPairs) {
        Cell antCell = pair.ant;
        int antCount = antCell.myAnts;
        Cell beaconCell = pair.beacon;
        int beaconCount = beaconCell.beacon;
        int wiggleRoom = wiggleRooms[beaconCell.index];

        int maxAlloc = stragglers ? Math.min(antCount, beaconCount + wiggleRoom) : Math.min(antCount, beaconCount);
        if (maxAlloc > 0) {
          allocations.add(new Allocation(antCell.data, beaconCell.data, maxAlloc));

          antCell.myAnts -= maxAlloc;
          if (!stragglers) {
            beaconCell.beacon -= maxAlloc;
          } else {
            beaconCell.beacon -= (maxAlloc - wiggleRoom);
            wiggleRooms[beaconCell.index] = 0;
          }
        }
      }
      allPairs.removeIf(pair -> pair.ant.myAnts<= 0);
      stragglers = true;
    }

    return allocations;
  }
  
  
  public static MapData nextForDispatch(State state, MapData current, MapData target) {
    if (current == target) return target;
    
    double bestScore = Double.NEGATIVE_INFINITY;
    MapData bestNext = null;
    
    for (MapData n : current.neighbors) {
      if (Map.distances[n.index][target.index] != Map.distances[target.index][current.index] -1) continue;
      
      double score = state.cells[n.index].myAnts + (-0.001 * n.index);
      if (score > bestScore) {
        bestScore = score;
        bestNext = n;
      }
    }
    return bestNext;
  
  }
}
