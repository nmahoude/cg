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
import spring2023.search.SimpleCostWithGoal;

public class NewOldAI3 implements AI {
  private State state;

  private int[] beacons;

  public void think(State state, int[] beacons) {
    naiveBeacons(state, beacons);
    BeaconsOptimizer.debugBeacons("after naive : ", beacons);
    reworkingBeaconsStrength(state, beacons);
    BeaconsOptimizer.debugBeacons("after reworking: ", beacons);
    limitingBeaconsStrength(state, beacons);
    BeaconsOptimizer.debugBeacons("after limiting : ", beacons);
 
    //optimizeBeacons(state, beacons);
  }

  public void optimizeBeacons(State state2, int[] beacons2) {
    new BeaconsOptimizer().think(state, beacons);
  }

  public void naiveBeacons(State state, int[] beacons) {
    this.state = state;
    this.beacons = beacons;
    int resources[] = new int[Map.MAX_CELLS];
    int totalAnts = 0;
    for (Cell c : state.cells) {
      totalAnts += c.myAnts;
      resources[c.index] = c.resources;
      beacons[c.index] = 0;
    }

    for (Cell c : state.cells) {
      resources[c.index] = c.resources - beacons[c.index];
    }
    
    boolean usedOldvAnts = true;
    for (int i = 0; i < state.numberOfCells; i++) {
      state.cells[i].vAnts = state.cells[i].myAnts;
    }
    
    final int backupTotalAnts = totalAnts;
    Set<Cell> currentTargets = new HashSet<>();
    Set<Cell> oppCurrentTargets = new HashSet<>();
    for (int i = 0; i < state.numberOfBases; i++) {
      currentTargets.add(state.cells[Map.myBases[i].index]);
      oppCurrentTargets.add(state.cells[Map.oppBases[i].index]);
    }
    AStar astar = new AStar();
    int minChains[] = new int[Map.MAX_CELLS];
    for (int i = 0; i < state.numberOfCells; i++) {
      if (resources[i] == 0)
        continue;
      double score2 = astar.search(state, new MyMaximumAnts(), currentTargets, state.cells[i]);
      int myMinAnts = Integer.MAX_VALUE;
      for (MapData md : astar.path.path) {
        if (md != astar.path.target) {
          Cell p = state.cells[md.index];
          myMinAnts = Math.min(p.myAnts, myMinAnts);
        }
      }
      if (myMinAnts == Integer.MAX_VALUE) {
        myMinAnts = 0;
      }
      double score = astar.search(state, new OppMaximumAnts(), oppCurrentTargets, state.cells[i]);
      int oppMinAnts = Integer.MAX_VALUE;
      for (MapData md : astar.path.path) {
        if (md != astar.path.target) {
          Cell p = state.cells[md.index];
          oppMinAnts = Math.min(p.oppAnts, oppMinAnts);
        }
      }
      if (oppMinAnts == Integer.MAX_VALUE) {
        oppMinAnts = 0;
      }
      if (Player.DEBUG_ACC) System.err.println("Min ants on path for " + Map.cells[i] + " is " + myMinAnts + " for me and " + oppMinAnts + " for opp");
      minChains[i] = oppMinAnts;
    }

    // focus on crystals
    boolean focusOnCrystals = Player.turn > 80 || (1.0 * state.myScore / State.originalTotalCrystalsGoal > 0.9);
    focusOnCrystals |= specialCasesForCrystals(state);
    
    
    System.err.println("Focus on crystals : "+focusOnCrystals);
    
    
    
    int goals[] = new int[Map.MAX_CELLS];
    for (int i=0;i<Map.MAX_CELLS;i++) {
      goals[i] = 1;
    }
    
    boolean work = true;
    do {
      int bestResources = -1;
      double bestScore = Double.NEGATIVE_INFINITY;
      final Path bestPath = new Path();
      for (int i = 0; i < Map.MAX_CELLS; i++) {
        if (resources[i] == 0)
          continue;

        // if (Player.turn < 10 && distToBases(i) > 6) {
        // System.err.println("Distance to base is "+distToBases(i)+" ignoring "+i);
        // continue; // don't go too far
        // }

//        if (Player.turn < 10 && !isCloserToMyBases(i)) {
//          continue; // don't rush near his positions
//          // TODO or maybe if on the limit and opp ants are "farther" than mine
//        }
        

        
        double score = -astar.search(state, new SimpleCostWithGoal(goals[i]), currentTargets, state.cells[i]);
        
        // Ne pas chercher les crystaux pres de sa base si j'en ai encore
        if (Map.cells[i].type == Map.CELL_CRYSTAL) {
          //System.err.println("Crystals closer to me : "+crystalsCountCloserToMe(state)+" "+i+" near my base ? "+isCloserToMyBases(i));
          if (crystalsCountCloserToMe(state) > 0 && !isCloserToMyBases(i)) {
            score -= 1000;
         }
        }
        
        if (focusOnCrystals && Map.cells[i].type == Map.CELL_EGGS) {
          score -= 50;
        }

        if (Player.turn < 10 && Map.cells[i].type == Map.CELL_EGGS) {
          score += 100;
        }
        
        score -= 5 * goals[i] ; // si on a déjà des fourmis, on baisse le score pour avantager le spreading
        
        if (astar.path.size() * minChains[i] > backupTotalAnts) {
          score -= 5000;
        }
        if (score > bestScore) {
          bestScore = score;
          bestResources = i;
          bestPath.copyFrom(astar.path);
        }
      }
      if (Player.DEBUG_AI) System.err.println("Best resource is " + bestResources + " with score " + bestScore + " and path " + bestPath);
      if (bestResources != -1) {
        
        int numberOfAnts = Math.max(goals[bestResources], minChains[bestResources] + 1);
        
        int tempTotalAnts = totalAnts;
        for (MapData md : bestPath.path) {
          Cell p = state.cells[md.index];
          if (numberOfAnts > beacons[p.index]) {
            int neededAnts = numberOfAnts - beacons[p.index];
            tempTotalAnts -= neededAnts;
            if (Player.LOCAL_DEBUG) {
              // System.err.println("removing " + neededAnts + " at " + p);
            }
          }
        }
        if (tempTotalAnts >= 0) {
          // reset vAnts from last turn 
//          if (usedOldvAnts) {
//            usedOldvAnts = false;
//            for (int i=0;i<state.numberOfCells;i++) {
//              state.cells[i].vAnts = 0;
//            }
//          }
          totalAnts = tempTotalAnts;
          for (MapData md : bestPath.path) {
            Cell p = state.cells[md.index];
            if (numberOfAnts > beacons[p.index]) {
              beacons[p.index] = numberOfAnts;
              state.cells[p.index].vAnts = numberOfAnts;
            }
          }
          goals[bestResources]++;
          resources[bestResources] -= numberOfAnts;
        } else {
          if (Player.DEBUG_AI) System.err.println("   resetting "+bestResources+", can't reach it with left ants");
          resources[bestResources] = 0;
        }
        if (totalAnts <= 0) {
          break;
        } else {
        }
      } else {
        if (Player.DEBUG_AI) System.err.println("No reachable cells , ants left ! "+totalAnts);
        break;
      }
      
    } while (true);
    System.err.println("End of ai");
  }

  private boolean specialCasesForCrystals(State state) {
    // si plus qu'un et que je peux le farm
    int crystalCount = crystalsCountCloserToMe(state);

    if (crystalCount <= 1) return true;
    
    
    return false;
  }

  private int crystalsCountCloserToMe(State state) {
    int crystalCount = 0;
    
    for (Cell s : state.cells) {
      
      
      if (s.resources > 0 && Map.cells[s.index].type == Map.CELL_CRYSTAL) {
        if (isCloserToMyBases(s.index)) {
          crystalCount++;
        }
      }
    }
    return crystalCount;
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
      
      if (Player.DEBUG_AI) System.err.println("Limiting number of ants of "+c+" to "+c.resources+" instead of "+beacons[c.index]);
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

  
  private boolean isCloserToMyBases(int i) {
    int myDist = Integer.MAX_VALUE;
    int oppDist = Integer.MAX_VALUE;
    
    for (int b = 0; b < state.numberOfBases; b++) {
      myDist = Math.min(myDist, Map.distances[Map.myBases[b].index][i]);
      oppDist = Math.min(oppDist, Map.distances[Map.oppBases[b].index][i]);
    }
    
    return myDist <= oppDist;
  }
  private int distToBases(int index) {
    int bestDist = Integer.MAX_VALUE;
    for (int i = 0; i < State.numberOfBases; i++) {
      bestDist = Math.min(bestDist, Map.distances[Map.myBases[i].index][index]);
    }
    return bestDist;
  }

  private void putBeacon(Cell c, int strength) {
    if (beacons[c.index] < strength) {
      beacons[c.index] = strength;
    }
  }
  
}
