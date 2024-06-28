package spring2023.ais.oldtries;

import java.util.HashSet;
import java.util.Set;

import spring2023.Player;
import spring2023.State;
import spring2023.ais.AI;
import spring2023.map.Cell;
import spring2023.map.Map;
import spring2023.map.MapData;
import spring2023.map.Path;
import spring2023.search.AStar;
import spring2023.search.MyMaximumAnts;
import spring2023.search.OppMaximumAnts;

public class AI4 implements AI {

  private static final int MAX_VALUE = 1_000_000;
  private int[] beacons;

  @Override
  public void think(State originalState, int[] beacons) {
    State work = new State();
    work.copyFrom(originalState);

    State nextState = new State();
    nextState.copyFrom(originalState);

    this.beacons = beacons;

    Set<Cell> currentTargets = new HashSet<>();
    Set<Cell> oppCurrentTargets = new HashSet<>();
    for (int i = 0; i < State.numberOfBases; i++) {
      currentTargets.add(originalState.cells[Map.myBases[i].index]); //
      oppCurrentTargets.add(originalState.cells[Map.oppBases[i].index]);
    }

    AStar astar = new AStar();

    
    // **********************
    // Check Attack chains ! 
    // **********************
    int minChains[] = new int[Map.MAX_CELLS];
    for (int i=0;i<State.numberOfCells;i++) {
      if (originalState.cells[i].resources == 0) continue;
      
      
      double score2 = astar.search(originalState, new MyMaximumAnts(), currentTargets, originalState.cells[i]);
      int myMinAnts = Integer.MAX_VALUE;
      for (MapData p : astar.path.path) {
        myMinAnts = Math.min(originalState.cells[p.index].myAnts, myMinAnts);
      }
      if (myMinAnts == Integer.MAX_VALUE) {
        myMinAnts = 0;
      }

      
      double score = astar.search(originalState, new OppMaximumAnts(), oppCurrentTargets, originalState.cells[i]);
      int oppMinAnts = Integer.MAX_VALUE;
      for (MapData p : astar.path.path) {
        oppMinAnts = Math.min(originalState.cells[p.index].oppAnts, oppMinAnts);
      }
      if (oppMinAnts == Integer.MAX_VALUE) {
        oppMinAnts = 0;
      }

      System.err.println("Min ants on path for "+Map.cells[i]+" is "+myMinAnts+" for me and "+oppMinAnts+" for opp");
      minChains[i] = oppMinAnts;
    }    
    // **********************
    // End of Check Attack chains ! 
    // **********************
    
    
    int totalAnts = originalState.totalMyAnts;

    int currentGoals[] = new int[Map.MAX_CELLS];
    int movables[] = new int[Map.MAX_CELLS];
    int fixed[] = new int[Map.MAX_CELLS];

    for (int i = 0; i < State.numberOfCells; i++) {
      currentGoals[i] = Math.max(1, minChains[i]);
      movables[i] = originalState.cells[i].myAnts;
      fixed[i] = 0;
    }

    while (totalAnts > 0) {
      System.err.println("*** ITER !");
      int bestCost = MAX_VALUE;
      final Path bestPath = new Path();

      for (MapData target : Map.cells) {
        if (work.cells[target.index].resources == 0) continue;
        if (work.cells[target.index].resources <= currentGoals[target.index]) continue;
        
        for (Cell startPointCell : currentTargets) {
          MapData startPoint = startPointCell.data;
          
          astar.search(originalState, startPointCell, originalState.cells[target.index]);
          Path path = astar.path;
          // System.err.println("Checking costs of iteration " + currentGoals[target.index] + " for " + path);

          // fix as much as possible on each cell of path
          int movables2[] = new int[Map.MAX_CELLS];
          int fixed2[] = new int[Map.MAX_CELLS];
          for (int i = 0; i < State.numberOfCells; i++) {
            movables2[i] = movables[i];
            fixed2[i] = fixed[i];
          }
          int cost = costOf(path, currentGoals[target.index], movables2, fixed2);

          
          if (Player.turn < 10 && Map.cells[target.index].type == Map.CELL_CRYSTAL) {
            cost += 5;
          }
          
          // under "attack"  ?
          boolean underAttack = false;
          final int attackDistance = 3;
          for (MapData md : Map.cellsByDistances[target.index].allCellsByDistance) {
            if (Map.distances[target.index][md.index] >= attackDistance) break;
            if (originalState.cells[md.index].oppAnts > 0) {
              underAttack = true;
              break;
            }
          }
          if (!underAttack && Player.turn >= 10 && Player.turn < 30 && Map.distances[startPoint.index][target.index] < 5) {
            cost += 10;
          }
          
          if (Player.LOCAL_DEBUG) System.err.println(path + " with cost " + cost);

          if (cost < bestCost) {
            bestCost = cost;
            bestPath.copyFrom(path);
          }
        }
      }

      System.err.println("Best Path " + bestPath + " with cost " + bestCost);
      if (bestCost != MAX_VALUE) {
        int currentGoal = currentGoals[bestPath.target.index];
        int cost = costOf(bestPath, currentGoals[bestPath.target.index], movables, fixed);
        
        currentGoals[bestPath.target.index] += 1;
        //work.cells[bestPath.target.index].resources = 0;
      } else {
        break;
      }
    }
    
    // update beacons
    for (int i=0;i<Map.MAX_CELLS;i++) {
      beacons[i] = fixed[i];
    }
    
    
  }

  private int costOf(Path path, int currentGoal, int[] movables, int[] fixed) {
    
    int cost = 0;

    for (MapData cell : path.path) {
      int needAntsAtCell = currentGoal;

      if (needAntsAtCell - fixed[cell.index] <= 0) {
        continue; // enough already fixed
      }

      needAntsAtCell -= fixed[cell.index];

      for (MapData d2 : Map.cellsByDistances[cell.index].allCellsByDistance) {
        
        int needToFix;
        if (movables[d2.index] >= needAntsAtCell) {
          needToFix = needAntsAtCell;
        } else {
          needToFix = Math.max(0, movables[d2.index] - needAntsAtCell );
        }
        
        if (needToFix > 0) {
          movables[d2.index] -= needToFix;
          fixed[cell.index] += needToFix;
          needAntsAtCell -= needToFix;
          // System.err.println("For " + cell + " will get " + needToFix + " @ " + d2 + " with local cost : "  + Map.distances[cell.index][d2.index]);
          cost = Math.max(cost, Map.distances[cell.index][d2.index]);
        }

        if (needAntsAtCell == 0)
          break;
      }
      if (needAntsAtCell > 0) {
        return MAX_VALUE; // impossible
      }
    }
    
    // System.err.println("Cost is " + cost);
    return cost;
  }

  @Override
  public void naiveBeacons(State state, int[] beacons) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void reworkingBeaconsStrength(State state, int[] beacons) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void limitingBeaconsStrength(State state, int[] beacons) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void optimizeBeacons(State state, int[] beacons) {
    // TODO Auto-generated method stub
    
  }

}
