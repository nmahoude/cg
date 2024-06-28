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

public class AI3 implements AI {
  private static AStar astar = new AStar();
  private static AttackChainCalculator ACC = new AttackChainCalculator();
  private State state;

  private int[] beacons;
  int resources[] = new int[Map.MAX_CELLS];
  int resourcesBelongingToBase[] = new int[Map.MAX_CELLS];
  
  public void think(State state, int[] beacons) {

    naiveBeacons(state, beacons);
    
    // optimizeBeacons(state, beacons);
  }

  public void optimizeBeacons(State state, int[] beacons) {
    System.err.println("Beacons optimizer !");
    new BeaconsOptimizer().think(state, beacons);
  }

  public void naiveBeacons(State state, int[] beacons) {
    this.state = state;
    this.beacons = beacons;
    
    int totalAnts = initTotalAnts(resources);
    final int backupTotalAnts = totalAnts;

    
    
    Set<Cell> currentTargets = new HashSet<>();
    Set<Cell> oppCurrentTargets = new HashSet<>();
    
    for (int i = 0; i < state.numberOfCells; i++) {
      state.cells[i].vAnts = 0;
    }
    
    
    for (int i = 0; i < state.numberOfBases; i++) {
      currentTargets.add(state.cells[Map.myBases[i].index]);
      oppCurrentTargets.add(state.cells[Map.oppBases[i].index]);
    }
    
    // count elligible eggs
    int elligibleEggs = 0;
    for (int i = 0; i < Map.MAX_CELLS; i++) {
      if (isElligibleEggs(i)) elligibleEggs++;
    }    
    boolean focusOnEggs = Player.turn < 10 && elligibleEggs > 0 && state.totalCrystals > 50;
    
    boolean focusOnCrystals = Player.turn > 80 || (1.0 * state.myScore / State.originalTotalCrystalsGoal > 0.9);
    if (focusOnCrystals) focusOnEggs = false;
    
    System.err.println("Focus eggs : "+focusOnEggs+" crystals : "+focusOnCrystals);
    do {
      int bestResources = -1;
      double bestScore = Double.NEGATIVE_INFINITY;
      final Path bestPath = new Path();
      for (int i = 0; i < Map.MAX_CELLS; i++) {
        if (resources[i] == 0) continue;
        
        
        if (Player.turn < 10 && Map.cells[i].type == Map.CELL_EGGS) {
          if (!isElligibleEggs(i)) continue;
        }
        
        
        double score = 100 - astar.search(state, currentTargets, state.cells[i]);
        if (focusOnEggs && Map.cells[i].type == Map.CELL_CRYSTAL) {
          score -=100;
        }

        if (focusOnCrystals && Map.cells[i].type == Map.CELL_EGGS) {
          continue;
        }
        
        
        if (Player.turn < 10 && distToBases(i) > 6) {
          System.err.println("Distance to base is "+distToBases(i)+" ignoring "+i);
          continue; // don't go too far
        }
        
        int minimumAnts = ACC.checkPath(state, astar.path);
        // System.err.println("Minimum ants is "+minimumAnts+" on path "+astar.path);
        if (minimumAnts * astar.path.size() > backupTotalAnts) {
          continue;
        }
        
        if (score > bestScore) {
          bestScore = score;
          bestResources = i;
          bestPath.copyFrom(astar.path);
        }
      }
      System.err.println("*** Best resource is " + bestResources + " with score " + bestScore + " and path " + bestPath);
      if (bestResources != -1) {
        int defaultAnts = 1;
        if (focusOnEggs && Map.cells[bestResources].type == Map.CELL_EGGS) {
          defaultAnts = 1; // TODO more strength ?
        }
        int numberOfAnts = Math.max(defaultAnts, ACC.checkPath(state, bestPath)+1);
        
        int tempTotalAnts = totalAnts;
        for (MapData md : bestPath.path) {
          Cell p = state.cells[md.index];
          if (numberOfAnts > beacons[p.index]) {
            int neededAnts = numberOfAnts - beacons[p.index];
            tempTotalAnts -= neededAnts;
            if (Player.LOCAL_DEBUG) {
              System.err.println("removing " + neededAnts + " at " + p);
            }
          }
        }
        if (tempTotalAnts >= 0) {
          totalAnts = tempTotalAnts;
          for (MapData md : bestPath.path) {
            Cell p = state.cells[md.index];
            if (numberOfAnts > beacons[p.index]) {
              beacons[p.index] = numberOfAnts;
            }
          }
          resources[bestResources] = 0;
        } else {
          // remove from choice
          System.err.println("Can't do best resource "+bestResources+" removing from choices");
          
          resources[bestResources] = 0;
        }
        
        if (Player.LOCAL_DEBUG) {
          System.err.println("remaining Ants " + totalAnts);
        }
        if (totalAnts <= 0) {
          break;
        }
      } else {
        break;
      }
    } while (true);
    System.err.println("End of ai");
    
    reworkingBeaconsStrength(state, beacons);
    limitingBeaconsStrength(state, beacons);
    
  }

  private int distToBases(int index) {
    int bestDist = Integer.MAX_VALUE;
    for (int i=0;i<State.numberOfBases;i++) {
      bestDist = Math.min(bestDist, Map.distances[Map.myBases[i].index][index]);
    }
    return bestDist;
  }

  public void limitingBeaconsStrength(State state, int[] beacons) {
    System.err.println("Limiting ants size on resources leafs");
    // for each cell, it's a resources and a leaf, limit the number of ants to the resource size
    for (Cell c: state.cells) {
      if (beacons[c.index] == 0) continue; 
      if (c.resources == 0) continue;
      
      if (beacons[c.index] < c.resources) continue; // not enough ants to harvest all
      if (c.oppAnts != 0) continue; // don't remove ants from cell with opponents
      
      int deltaAnts = beacons[c.index] - c.resources;
      
      
//      boolean isLeaf = true;
//      for (MapData mdn : c.data.neighbors) {
//        if (beacons[mdn.index] > 0) isLeaf = false;
//      }
//      if (!isLeaf) continue;
      
      System.err.println("Limiting number of ants of "+c+" to "+c.resources+" instead of "+beacons[c.index]);
      beacons[c.index]= c.resources;

      
      // put delta on the closest cell with beacons & resources
      for (MapData md2 : Map.cellsByDistances[c.index].allCellsByDistance) {
        if (beacons[md2.index] > 0 && state.cells[md2.index].resources > beacons[md2.index]) {
          beacons[md2.index] += deltaAnts;
        }
      }
    }
  }

  public void reworkingBeaconsStrength(State state, int[] beacons) {
    System.err.println("Reworking beacons to match ants count ! ");
    int sumOfBeacons = 0;
    int sumOfAnts = 0;
    for (int i=0;i<State.numberOfCells;i++) {
      sumOfBeacons += beacons[i];
      sumOfAnts += state.cells[i].myAnts;
    }

    if (sumOfBeacons == 0) return;
    
    double scalingFactor = 1.0 * sumOfAnts / sumOfBeacons;
    int wiggleRoom = sumOfAnts - (int)(sumOfAnts / sumOfBeacons * sumOfBeacons);
    
    for (int i=0;i<State.numberOfCells;i++) {
      if (beacons[i] == 0) continue;
      
      int highBeaconValue = (int) Math.ceil(beacons[i] * scalingFactor);
      int lowBeaconValue = (int) (beacons[i] * scalingFactor);
      beacons[i] = Math.max(1, lowBeaconValue);
    }

    boolean stop = false;
    while (!stop) {
      for (MapData md : Map.cellsByDistances[Map.myBases[0].index].allCellsByDistance) {
        if (wiggleRoom == 0) {
          stop = true;
          break;
        }
        if (beacons[md.index] > 0) {
          beacons[md.index]++;
          wiggleRoom--;
        }
      }
    }
    
    
    System.err.println("End of reworking beacons");
  }

  private boolean isElligibleEggs(int i) {
    if (resources[i] == 0) return false;
    if (Map.cells[i].type != Map.CELL_EGGS) return false;
    
    boolean closerToMe = distBaseToResources(i);
    if (!closerToMe) return false;
    
    return true;
  }

  private boolean distBaseToResources(int i) {
    int myDist = Integer.MAX_VALUE;
    int oppDist = Integer.MAX_VALUE;
    
    for (int b = 0; b < state.numberOfBases; b++) {
      myDist = Math.min(myDist, Map.distances[Map.myBases[b].index][i]);
      oppDist = Math.min(oppDist, Map.distances[Map.oppBases[b].index][i]);
    }
    
    return myDist <= oppDist;
  }

  private int initTotalAnts(int[] resources) {
    int totalAnts = 0;
    for (Cell c : state.cells) {
      totalAnts += c.myAnts;
      resources[c.index] = c.resources;
      resourcesBelongingToBase[c.index] = -1;
      
    }
    
    for (int i=0;i<State.numberOfBases;i++) {
      resourcesBelongingToBase[Map.myBases[i].index] = i;
    }
    
    
    return totalAnts;
  }

  private void putBeacon(Cell c, int strength) {
    if (beacons[c.index] < strength) {
      beacons[c.index] = strength;
    }
  }

}
