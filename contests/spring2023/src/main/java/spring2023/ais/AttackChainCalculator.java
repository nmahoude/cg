package spring2023.ais;

import java.util.HashSet;
import java.util.Set;

import spring2023.Player;
import spring2023.State;
import spring2023.map.Cell;
import spring2023.map.Map;
import spring2023.map.MapData;
import spring2023.map.Path;
import spring2023.search.AStar;
import spring2023.search.MyMaximumAnts;
import spring2023.search.OppMaximumAnts;

public class AttackChainCalculator {
  private final AStar astar = new AStar();
  public final int minChains[] = new int[Map.MAX_CELLS];

  public void update(State state) {

    reset();
    
    Set<Cell> currentTargets = new HashSet<>();
    Set<Cell> oppCurrentTargets = new HashSet<>();
    
    for (int i = 0; i < State.numberOfBases; i++) {
      currentTargets.add(state.cells[Map.myBases[i].index]);
      oppCurrentTargets.add(state.cells[Map.oppBases[i].index]);
    }

    
    //*** min chains
    for (int i = 0; i < state.numberOfCells; i++) {
      if (state.cells[i].resources == 0) continue;
      
      
      double score2 = astar.search(state, new MyMaximumAnts(), currentTargets, state.cells[i]);
      int myMinAnts = Integer.MAX_VALUE;
      for (MapData md : astar.path.path) {
        if (md == astar.path.target) continue;
        
        Cell p = state.cells[md.index];
        myMinAnts = Math.min(p.myAnts, myMinAnts);
      }
      if (myMinAnts == Integer.MAX_VALUE) {
        myMinAnts = 0;
      }
      double score = astar.search(state, new OppMaximumAnts(), oppCurrentTargets, state.cells[i]);
      int oppMinAnts = Integer.MAX_VALUE;
      for (MapData md : astar.path.path) {
        if (md == astar.path.target)  continue;
          Cell p = state.cells[md.index];
          oppMinAnts = Math.min(p.oppAnts, oppMinAnts);
      }
      if (oppMinAnts == Integer.MAX_VALUE) {
        oppMinAnts = 0;
      }
      if (Player.DEBUG_ACC)
        System.err.println("Min ants on path for " + Map.cells[i] + " is " + myMinAnts + " for me and " + oppMinAnts + " for opp");
      minChains[i] = oppMinAnts;
    }
    //*** end of minChains

  }


  private void reset() {
    for (int i=0;i<Map.MAX_CELLS;i++) {
      minChains[i] = 0;
    }
  }


  public int checkPath(State state, Path path) {
    Set<Cell> ennemyBases = new HashSet<>();
    for (int i=0;i<State.numberOfBases;i++) {
      ennemyBases.add(state.cells[Map.oppBases[i].index]);
    }
    
    int minOppAnts = 0;
    for (MapData md : path.path) {
      // for each cell on the path, check if best ant path to ennemy base is better
      if (state.cells[md.index].oppAnts > 0 && state.cells[md.index].myAnts > 0) {
        astar.search(state, new OppMaximumAnts(), ennemyBases, state.cells[md.index]);
        if (astar.path.size() == 0) continue; // no way home
        
        int localMinOppAnts = Integer.MAX_VALUE;
        for (MapData md2 : astar.path.path) {
          localMinOppAnts = Math.min(localMinOppAnts, state.cells[md2.index].oppAnts);
        }

        minOppAnts = Math.max(minOppAnts, localMinOppAnts);
      } 
    }

    return minOppAnts;
  }
}
