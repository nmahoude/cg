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
import spring2023.search.AntsCost;

public class AI5 implements AI {
  int goals[] = new int[Map.MAX_CELLS];
  int movablesAnts[] = new int[Map.MAX_CELLS];
  int fixedAnts[] = new int[Map.MAX_CELLS];

  @Override
  public void think(State state, int[] beacons) {
    
    State next2 = new State();
    next2.copyFrom(state);
    
    // farm eggs as quick as possible
    
    Set<Cell> myBases = new HashSet<>();
    for (int i=0;i<State.numberOfBases;i++) {
      myBases.add(state.cells[Map.myBases[i].index]);
    }
    
    AStar astar = new AStar();
    
    for (int i=0;i<State.numberOfCells;i++) {
      goals[i] = 0;
      movablesAnts[i] = 0; // consider all ants on the base
      fixedAnts[i] = 0;
    }
    
    movablesAnts[Map.myBases[0].index] = state.totalMyAnts; // all ants on the base
    
    while(true) {
      System.err.println("*** ITER");
       int bestCost = Integer.MAX_VALUE;
       final Path bestPath = new Path();
       
      
      
      // rechercher les eggs les plus proches des start nodes
      int bestCostToAddOneHarvester = Integer.MAX_VALUE;
      for (Cell target : state.cells) {
        if (target.resources <= 0) continue;

        int newGoal = goals[target.index]+1;
        // TODO if newGoal > 1 then only the affected base !
        astar.search(state, new AntsCost(newGoal, movablesAnts, fixedAnts), myBases, target);
        int cost = 0;
        for (MapData md : astar.path.path) {
          if (newGoal <= fixedAnts[md.index]) continue; // no cost
          cost += Math.max(0 , newGoal - (movablesAnts[md.index] + fixedAnts[md.index]));
        }

        if (target.data.type != Map.CELL_EGGS) {
          cost += 2;
          
          // unattacked resources next to me are not high priority
          if (Map.distances[Map.myBases[0].index][target.index] < 5) {
            // check if under attack
            boolean underAttack = false;
            final int attackDistance = 3;
            for (MapData md : Map.cellsByDistances[target.index].allCellsByDistance) {
              if (Map.distances[target.index][md.index] >= attackDistance) break;
              if (state.cells[md.index].oppAnts > 0) {
                underAttack = true;
                break;
              }
            }
            if (!underAttack) {
              cost += 10;
            }
            
          }
          
          
        }

        
        if (Player.LOCAL_DEBUG) System.err.println("Cost of goal "+newGoal+" for "+target+" is "+cost);
        if (cost < bestCost) {
          bestCost = cost;
          bestPath.copyFrom(astar.path);
        }
      }

      if (bestPath.origin != null) {
        System.err.println("Best eggs : "+bestPath.target+" from "+bestPath.origin+" with cost "+bestCost);
        int newGoal = ++goals[bestPath.target.index];
        System.err.println("Updating state, new goal for "+bestPath.target+" is  "+newGoal);
        
        int neededAnts = 0;
        for (MapData md : bestPath.path) {
          if (newGoal <= fixedAnts[md.index]) continue; // no cost, no need for more ants
          
          int needed = newGoal - fixedAnts[md.index];
          
          if (md == bestPath.origin) {
            if (neededAnts > movablesAnts[md.index]) {
              System.err.println("Can't affect ants ! Need to rollback");
              break;
            }
          }
  
          
          
          if (movablesAnts[md.index] >= needed) {
            movablesAnts[md.index]-=needed;
            fixedAnts[md.index]+=needed;
            needed = 0;
          } else {
            fixedAnts[md.index]+=movablesAnts[md.index];
            needed-=movablesAnts[md.index];
            movablesAnts[md.index] = 0; 
            fixedAnts[md.index]+=needed; // add hypotetic future ants :)
          }
          neededAnts += needed;
        }
        
        // state.cells[bestPath.target.index].resources--;
        state.cells[bestPath.target.index].resources --;
        
        System.err.println("needed Ants to fulfill path : "+neededAnts);
        movablesAnts[bestPath.origin.index]-=neededAnts;
        
        
      } else {
        break;
      }
    }
    
    for(int i=0;i<state.numberOfCells;i++) {
      beacons[i] = fixedAnts[i];
    }
    
    
  }

  private void debugAnts(int i) {
    System.err.println("Current state of "+i+" is "+movablesAnts[i]+" movables & "+fixedAnts[i]+" fixed");
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
