package spring2023.ais.oldtries;

import java.util.HashSet;
import java.util.Set;

import spring2023.Player;
import spring2023.State;
import spring2023.ais.AI;
import spring2023.ais.AttackChainCalculator;
import spring2023.map.Cell;
import spring2023.map.Map;
import spring2023.map.MapData;
import spring2023.map.Path;
import spring2023.search.AStar;

public class AI3 implements AI {
  private static AttackChainCalculator ACC = new AttackChainCalculator();
  
  private State state;
  private int[] beacons;

  @Override
  public void think(State state, int[] beacons) {
    this.state = state;
    this.beacons = beacons;
    int resources[] = new int[Map.MAX_CELLS];
    int totalAnts = 0;
    
    
    for (Cell c : state.cells) {
      totalAnts += c.myAnts;
      resources[c.index] = c.resources;
    }
    
    final int backupTotalAnts = totalAnts;
    Set<Cell> currentTargets = new HashSet<>();
    Set<Cell> oppCurrentTargets = new HashSet<>();
    
    for (int i = 0; i < state.numberOfBases; i++) {
      currentTargets.add(state.cells[Map.myBases[i].index]);
      oppCurrentTargets.add(state.cells[Map.oppBases[i].index]);
    }
    
    AStar astar = new AStar();
    
    ACC.update(state);
    
    
    int goals[] = new int[Map.MAX_CELLS];
    
    boolean found = false;
    do {
      if (Player.LOCAL_DEBUG) System.err.println("*** ITER");
      found = false;
      int bestResources = -1;
      double bestScore = Double.NEGATIVE_INFINITY;
      Path bestPath = new Path();
      for (int i = 0; i < State.numberOfCells; i++) {
        if (resources[i] == 0) continue;
        
        
        astar.search(state, currentTargets, state.cells[i]);
        
        double score = 0.0;
        if (Player.turn < 10 && Map.cells[i].type == Map.CELL_EGGS) {
          score += 10;
        }
        
        
        int newGoal;
        if (beacons[i] == 0 && ACC.minChains[i] > 0) {
          newGoal = ACC.minChains[i];
        } else {
          newGoal = beacons[i]+1;
        }
        
        int cost = 0;
        for (MapData md : astar.path.path) {
          cost+= Math.max(0, newGoal - beacons[md.index]);
        }
        score -= cost;
        
        if (Player.LOCAL_DEBUG) System.err.println("  cost of "+i+" is "+cost);
        if (cost > backupTotalAnts) {
          continue;
        }

        if (score > bestScore) {
          bestScore = score;
          bestResources = i;
          bestPath.copyFrom(astar.path);
        }
      }
      System.err.println("Best resource is " + bestResources + " with score " + bestScore + " and path " + bestPath);
      if (bestResources != -1) {
        int numberOfAnts = Math.max(goals[bestResources]+1, ACC.minChains[bestResources] + 1);
        goals[bestResources] = numberOfAnts;
        
        for (MapData p : bestPath.path) {
          if (numberOfAnts > beacons[p.index]) {
            int neededAnts = Math.max(0, numberOfAnts - beacons[p.index]);
            totalAnts -= neededAnts;
            if (Player.LOCAL_DEBUG) {
              System.err.println("removing " + neededAnts + " at " + p);
            }
          }
        }
        if (totalAnts >= 0) {
          for (MapData p : bestPath.path) {
            beacons[p.index] = Math.max(beacons[p.index], numberOfAnts);
          }
        }
        if (Player.LOCAL_DEBUG) {
          System.err.println("remaining Ants " + totalAnts);
        }
        resources[bestResources]= Math.max(0, resources[bestResources] - numberOfAnts);
        
        
        if (totalAnts > 0) {
          found = true;
        }
      }
    } while (found);
    System.err.println("End of ai");
  }

  private void putBeacon(Cell c, int strength) {
    if (beacons[c.index] < strength) {
      beacons[c.index] = strength;
    }
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
