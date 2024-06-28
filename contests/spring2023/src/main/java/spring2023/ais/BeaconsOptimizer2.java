package spring2023.ais;

import java.util.List;

import spring2023.Player;
import spring2023.State;
import spring2023.map.Map;
import spring2023.map.MapData;
import spring2023.simulation.Allocation;
import spring2023.simulation.Simulation;

public class BeaconsOptimizer2 {
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
    
    if (Player.DEBUG_OPTIMIZER) {
      System.err.println("*************************");
      System.err.println("Existing allocations");
      for (List<Allocation> allocs : sim.allocations.values()) {
        if (Player.DEBUG_OPTIMIZER) System.err.println(allocs);
      }
      System.err.println("*************************");
    }
    
    // ici j'ai toutes les paires d'allocations remplies !
    for (List<Allocation> allocs : sim.allocations.values()) {
      if (Player.DEBUG_OPTIMIZER) System.err.println(allocs);
      if (Player.DEBUG_OPTIMIZER) System.err.println();
      
      for (Allocation alloc : allocs) {
        if (alloc.target == alloc.source) continue;
        
        MapData target = alloc.target;
        MapData next = alloc.source;
        MapData previous = alloc.source;
        MapData previous2 = alloc.source;
        if (Player.DEBUG_OPTIMIZER) System.err.println("Checking "+alloc);
     
        // si le previous dans la chaine a fait de l'auto-alloc et que j'ai besoin de plus que le delta, 
        // alors je demande un de plus et lui baisse son auto-alloc
        
        while (next != target) {
          previous2 = previous;
          previous = next;
          next = Simulation.nextForDispatch(originalState, next, target);
        }
        if (Player.DEBUG_OPTIMIZER) System.err.println("    Previous in chain is "+previous2+"->"+previous+"->"+next);
        
        // A -> B -> C : A donne à C en passant par B
        MapData A = previous2;
        MapData B = previous;
        MapData C = next;
        
        if (A != alloc.source) continue;
        if (previous == previous2) {
          if (Player.DEBUG_OPTIMIZER) System.err.println("    no chains, passign");
          continue;
        }
        // get autoAllocation of B
        List<Allocation> list = sim.allocations.get(previous);
        if (list == null || list.size() == 0) {
          if (Player.DEBUG_OPTIMIZER) System.err.println("No autoalloc of "+B+" passing");
          continue; // no auto alloc, can't let ants from me
        }
        Allocation allocOfB = list.get(0);
        
        int reallocate = Math.min(alloc.antsToSend, Math.min(originalState.cells[B.index].myAnts, originalBeacons[B.index]));
        
        if (Player.DEBUG_OPTIMIZER) System.err.println("    Can reallocate "+reallocate+" ants");
        allocOfB.antsToSend -= reallocate;
        alloc.antsToSend += reallocate;
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
