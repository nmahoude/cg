package spring2023.ais;

import java.util.ArrayList;
import java.util.List;

import spring2023.Player;
import spring2023.State;
import spring2023.map.Map;
import spring2023.map.MapData;
import spring2023.simulation.Allocation;
import spring2023.simulation.Simulation;

public class BeaconsOptimizer {
  private final static Simulation sim = new Simulation();
  
  
  private State originalState;
  
  int[][] transfer = new int [Map.MAX_CELLS][Map.MAX_CELLS];
  
  
  
  public void think(State originalState, final int[] originalBeacons) {
    
    
    this.originalState = originalState;
    {
      State state = new State();
      state.copyFrom(originalState);
      for (int i=0;i<State.numberOfCells;i++) {
        state.cells[i].beacon = originalBeacons[i];
      }
      sim.simulate(state);
      
      resetTransfer(sim);
      
      
    }
    
    System.err.println("Start of optimization");
    debugBeacons("Before Optimization", originalBeacons);
    
    // ici j'ai toutes les paires d'allocations remplies !
    for (List<Allocation> allocs : sim.allocations.values()) {
      if (Player.DEBUG_OPTIMIZER) System.err.println(allocs);
      if (Player.DEBUG_OPTIMIZER) System.err.println();
      
      for (Allocation alloc : allocs) {
        if (alloc.target == alloc.source) continue;
        
        MapData target = alloc.target;
        MapData next = alloc.source;
        MapData previous = next;
        System.err.println("Checking "+alloc);
        
        while (true) {
          next = Simulation.nextForDispatch(originalState, next, target);
          if (next == alloc.target) break;
          
          // from the same source, what is the contribution of the same source  to the next ?
          Allocation toNext = null;
          for (Allocation tt : allocs) {
            if (tt.target == next) {
              toNext = tt;
              break;
            }
          }
          if (toNext != null) {
            System.err.println("     allocation exist for "+next+" on the same source");
          } else {
            // no direct allocation, so check if we got auto-allocation
            List<Allocation> list = sim.allocations.get(next);
            if (list == null || list.size() == 0) continue; // no auto alloc, can't let ants from me
            
            // check if no asking is closer than target with a source farther
            boolean foundCloserWithNeeds = false;
            for (List<Allocation> allocs2 : sim.allocations.values()) {
              for (Allocation alloc2 : allocs2) {
                if (alloc2.target == target) continue; // don't check ourself

                if (Map.distances[next.index][alloc2.target.index] < Map.distances[target.index][next.index] 
                    && Map.distances[alloc2.source.index][alloc2.target.index] > Map.distances[next.index][alloc2.target.index]) {
                    foundCloserWithNeeds = true;
                    break;
                }
              }
            } 
            
            if (foundCloserWithNeeds) continue; // can't realloc, another cell will get the ants :/

            
            toNext = list.get(0); // auto alloc
          }
          
          
          if (toNext.antsToSend > 0) {
            
            // je vais te piquer tes cells & ton beacon pour compenser
            int delta = Math.min(toNext.antsToSend, alloc.antsToSend); 
                
//                Math.min(alloc.antsToSend, 
//                              Math.min(toNext.antsToSend, 
//                                  Math.max(0, 
//                                      // combien j'en veux - combien j'en ai 
//                                      originalBeacons[target.index] - originalState.cells[target.index].myAnts ))
//                ); 
            
            // A ---> C ---> B
            
            if (toNext.source == alloc.source) {
              alloc.antsToSend += delta; // B demande plus à A
              toNext.antsToSend -= delta; // C baisse sa demande à A
            } else {
              // C -> C --
              // C -> B ++
              toNext.antsToSend-=delta; // auto alloc
              // find C to B or add it
              Allocation cToB = null;
              List<Allocation> list = sim.allocations.get(next);
              for (Allocation a : list) {
                if (a.target == alloc.source) {
                  cToB = a;
                  break;
                }
              }
              if (cToB == null) {
                // ajouter
                cToB = new Allocation(next, alloc.target, delta);
                sim.allocations.putIfAbsent(next, new ArrayList<>());
                sim.allocations.get(next).add(cToB);
              } else {
                cToB.antsToSend += delta;
              }
            }
            
            if (Player.DEBUG_OPTIMIZER) System.err.println("Reallocating "+delta+" from "+toNext+" to "+alloc);
          }
          
          previous = next;
        }
      }
    }
    
    // recalculate beacons from new distribution
    for (int i=0;i<State.numberOfCells;i++) {
      originalBeacons[i] = 0;
    }
    for (List<Allocation> allocs : sim.allocations.values()) {
      for (Allocation alloc : allocs) {
        originalBeacons[alloc.target.index]+=alloc.antsToSend;
      }
    }
    debugBeacons("After Optimization", originalBeacons);

  }



  public static void debugBeacons(String str, int[] beacons) {
    System.err.println(str);
    for (int i=0;i<Map.MAX_CELLS;i++) {
      if (beacons[i] > 0) System.err.print("["+i+"="+beacons[i]+"];");
    }
    System.err.println();
  }



  private void resetTransfer(Simulation sim) {
    for (int i=0;i<Map.MAX_CELLS;i++) {
      for (int j=0;j<Map.MAX_CELLS;j++) {
        transfer[i][j] = 0;
      }
    }
    
    for (List<Allocation> allocs : sim.allocations.values()) {
      for (Allocation alloc : allocs) {
        transfer[alloc.source.index][alloc.target.index] = alloc.antsToSend;
      }
    }
  }
  
}
