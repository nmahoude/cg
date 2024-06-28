package spring2023.ais.newAI;

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

public class AntsAI implements AI {

  @Override
  public void think(State state, int[] beacons) {
    naiveBeacons(state, beacons);
    reworkingBeaconsStrength(state, beacons);
    limitingBeaconsStrength(state, beacons);
    
    optimizeBeacons(state, beacons);
  }

  @Override
  public void naiveBeacons(State state, int[] beacons) {
    AStar astar = new AStar();
    
    Set<Cell> currentTargets = new HashSet<>();
    Set<Cell> oppCurrentTargets = new HashSet<>();
    for (int i = 0; i < state.numberOfBases; i++) {
      currentTargets.add(state.cells[Map.myBases[i].index]);
      oppCurrentTargets.add(state.cells[Map.oppBases[i].index]);
    }
    
    int[] movablesAnts = new int[Map.MAX_CELLS];
    int[] currentAnts = new int[Map.MAX_CELLS];
    int[] goals = new int[Map.MAX_CELLS];
    int[] belongToBase = new int[Map.MAX_CELLS];
    
    for (Cell cell : state.cells) {
      currentAnts[cell.index] = 0;
      movablesAnts[cell.index] = cell.myAnts;
      goals[cell.index] = 1;
    }    
    
    
    boolean focusOnEggs = Player.turn < 10;
    boolean focusOnCrystals = Player.turn > 50 /* end game */ 
                                || (1.0 * state.myScore / State.originalTotalCrystalsGoal > 0.9) /* almost done */
                                ;
         
    do {
      double bestScore = Double.POSITIVE_INFINITY;
      Path bestPath = new Path();
      
      for (Cell cell : state.cells) {
        if (cell.resources == 0) continue;
        
        
        // what is the cost ?
        double cost = 0.0;
        
        cost += astar.search(state, new AntsPathCost(movablesAnts, currentAnts, goals[cell.index]), currentTargets, cell);
        cost += goals[cell.index] * 1;

        if (focusOnEggs) {
          if (Map.cells[cell.index].type == Map.CELL_EGGS) {
            cost -= 10;
          }
        }
        
        if (focusOnCrystals) {
          if (Map.cells[cell.index].type == Map.CELL_CRYSTAL) {
            cost -= 10;
          }
        }
        
        System.err.println("Cost of "+cell+" is "+cost);
        //System.err.println("    Path is "+astar.path);
      
        if (cost < bestScore) {
          bestScore = cost;
          bestPath.copyFrom(astar.path);
        }
      }
      
      if (bestPath.target != null) {
        MapData target = bestPath.target;
        System.err.println("    best (goal = "+goals[target.index]+")is "+bestPath);
        int goal = goals[target.index];
        
        for (MapData cellPath : bestPath.path) {
          int neededAnts = goal;
          if (currentAnts[cellPath.index] >= goal) continue;
          neededAnts -= currentAnts[cellPath.index];
          
          if (movablesAnts[cellPath.index] >= neededAnts) {
            movablesAnts[cellPath.index] -= neededAnts;
            currentAnts[cellPath.index] += neededAnts;
            continue;
          }
          
          for (MapData md : Map.cellsByDistances[cellPath.index].allCellsByDistance) {
            int canTake = Math.min(neededAnts, movablesAnts[md.index]);
            if (canTake > 0) {
              neededAnts -= canTake;
              movablesAnts[md.index]-=canTake;
              currentAnts[cellPath.index] += canTake; // TODO next cell instead of target ?
            }
            if (neededAnts == 0) break;
          } 
        }
        
        belongToBase[target.index] = bestPath.origin.index + 1;
        goals[target.index]++;
        
      } else {
        break;
      }
    } while(true);
    
    for (Cell cell : state.cells) {
      beacons[cell.index] = movablesAnts[cell.index] + currentAnts[cell.index];
    }
    
    
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
