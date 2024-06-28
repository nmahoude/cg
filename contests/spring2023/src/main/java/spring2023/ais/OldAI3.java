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
import spring2023.search.SimpleCostWithGoal;

public class OldAI3 implements AI {
  private static AttackChainCalculator ACC = new AttackChainCalculator();
  private State state;

  private int[] beacons;

  public void think(State state, int[] beacons) {
    naiveBeacons(state, beacons);
    reworkingBeaconsStrength(state, beacons);
    limitingBeaconsStrength(state, beacons);
    
    optimizeBeacons(state, beacons);
  }

  public void optimizeBeacons(State state2, int[] beacons2) {
    System.err.println("Beacons optimizer !");
    new BeaconsOptimizer2().think(state, beacons);
  }

  public void naiveBeacons(State state, int[] beacons) {
    this.state = state;
    this.beacons = beacons;
    
    
    state.resetDedicated();
    
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
    
    ACC.update(state);
    
    
    boolean focusOnCrystals = Player.turn > 80 
        || (1.0 * state.myScore / State.originalTotalCrystalsGoal > 0.8)
        || (1.0 * state.oppScore / State.originalTotalCrystalsGoal > 0.8)
        ;
    int goals[] = new int[Map.MAX_CELLS];
    for (int i = 0; i < Map.MAX_CELLS; i++) {
      goals[i] = 1;
    }
    
    
    do {
      int bestResources = -1;
      double bestScore = Double.NEGATIVE_INFINITY;
      final Path bestPath = new Path();
      for (int i = 0; i < Map.MAX_CELLS; i++) {
        if (resources[i] == 0)
          continue;
        if (focusOnCrystals && Map.cells[i].type == 1) {
          continue;
        }
        
        
        double score = 100 - astar.search(state, new SimpleCostWithGoal(goals[i]), currentTargets, state.cells[i]);
        if (Player.turn < 10 && Map.cells[i].type == 1) {
          score += 10;
        }
        score -= goals[i] * 2;
        score += 0.01 * Math.max(state.cells[i].resources, goals[i]);
        
        int myMinDistance = Integer.MAX_VALUE;
        int oppMinDistance = Integer.MAX_VALUE;
        for (Cell cell : state.cells) {
          if (cell.myAnts > 0) myMinDistance = Math.min(myMinDistance, Map.distances[cell.index][i]);
          if (cell.oppAnts > 0) oppMinDistance = Math.min(oppMinDistance, Map.distances[cell.index][i]);
        }
        
        if (myMinDistance + 2 > oppMinDistance) {
          score -= 2*myMinDistance;
        }
        
        if (distToBases(i) == distToOppBases(i)) {
          score += 2;
        }
        
        if (Player.turn < 10 && distToBases(i) < 5 && distToOppBases(i) > 5 && Map.cells[i].type == Map.CELL_CRYSTAL) {
          if (myMinDistance > oppMinDistance - 2) {
            // crystal next to me and opp is far away, don't focus on it
            score -= 10;
          }
        }
        
        int minimumAnts = 1 + ACC.checkPath(state, astar.path);
        int neededAnts = 0;
        for (MapData md : astar.path.path) {
          if (md == astar.path.target) continue;
          if (beacons[md.index] < minimumAnts) {
            neededAnts += (minimumAnts - beacons[md.index]);
          }
        }
        if (neededAnts > totalAnts) {
          if (Player.LOCAL_DEBUG) System.err.println("Abandon de "+i+" pb attack chain : min="+minimumAnts+" sur la path "+astar.path);
          continue;
        }
        
        if (Player.DEBUG_AI) System.err.println("Score of "+i+" (goal = "+goals[i]+") is "+score);
        if (score > bestScore) {
          bestScore = score;
          bestResources = i;
          bestPath.copyFrom(astar.path);
        }
      }
      if (bestResources != -1) {
        if (Player.DEBUG_AI) System.err.println("Best resource is " + bestResources + " with goal "+goals[bestResources]+" and score " + bestScore + " and path " + bestPath);
        int minimumAnts = ACC.checkPath(state, bestPath);
        
        int numberOfAnts = Math.max(goals[bestResources], minimumAnts + 1);
        int tempTotalAnts = totalAnts;
        for (MapData md : bestPath.path) {
          Cell p = state.cells[md.index];
          if (numberOfAnts > beacons[p.index]) {
            int neededAnts = numberOfAnts - beacons[p.index];
            tempTotalAnts -= neededAnts;
            if (Player.LOCAL_DEBUG) {
            }
          }
        }
        if (tempTotalAnts >= 0) {
          totalAnts = tempTotalAnts;
          for (MapData md : bestPath.path) {
            Cell p = state.cells[md.index];
            if (numberOfAnts > beacons[p.index]) {
              beacons[p.index] = numberOfAnts;
              state.cells[p.index].vAnts = Math.max(numberOfAnts, state.cells[p.index].vAnts);
              
              if (state.cells[p.index].dedicatedBase == -1) {
                state.cells[p.index].dedicatedBase = bestPath.origin.index;
              } else if (state.cells[p.index].dedicatedBase != bestPath.origin.index) {
                state.cells[p.index].dedicatedBase = -2; // shared
              }
            }
          }
          goals[bestResources]++;
          resources[bestResources] -= numberOfAnts;
        } else {
          if (Player.DEBUG_AI) System.err.println("   resetting " + bestResources + ", can't reach it with left ants");
          resources[bestResources] = 0;
        }
        if (totalAnts <= 0) {
          break;
        } else {
        }
      } else {
        System.err.println("No reachable cells , ants left ! " + totalAnts);
        break;
      }
      
      if (System.currentTimeMillis() - Player.start > 80) break;
    } while (true);
    System.err.println("End of ai with "+totalAnts+" left to distribute");
  }

  public void limitingBeaconsStrength(State state, int[] beacons) {
    System.err.println("Limiting ants size on resources leafs");
    for (Cell c : state.cells) {
      if (beacons[c.index] == 0)  continue;
      if (c.resources == 0) continue;
      if (beacons[c.index] < c.resources) continue;
      
      if (c.oppAnts != 0) continue;
      
      int countNeihborsWithBeacons = 0;
      for (MapData n : c.data.neighbors) {
        countNeihborsWithBeacons += beacons[n.index] > 0 ? 1 : 0;
      }
      if (countNeihborsWithBeacons > 1) continue;
      
      
      int deltaAnts = beacons[c.index] - c.resources;
      System.err.println("Limiting number of ants of " + c + " to " + c.resources + " instead of " + beacons[c.index]);
      beacons[c.index] = c.resources;
      for (MapData md2 : Map.cellsByDistances[c.index].allCellsByDistance) {
        if (beacons[md2.index] > 0 && state.cells[md2.index].resources > beacons[md2.index]) {
          beacons[md2.index] += deltaAnts;
          break;
        }
      }
    }
  }

  public void reworkingBeaconsStrength(State state, int[] beacons) {
    System.err.println("Reworking beacons to match ants count ! ");
    int sumOfBeacons = 0;
    int sumOfAnts = 0;
    for (int i = 0; i < State.numberOfCells; i++) {
      sumOfBeacons += beacons[i];
      sumOfAnts += state.cells[i].myAnts;
    }
    if (sumOfBeacons == 0)
      return;
    double scalingFactor = 1.0 * sumOfAnts / sumOfBeacons;
    int wiggleRoom = sumOfAnts - (int) (sumOfAnts / sumOfBeacons * sumOfBeacons);
    for (int i = 0; i < State.numberOfCells; i++) {
      if (beacons[i] == 0)
        continue;
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

  private int distToOppBases(int index) {
    int bestDist = Integer.MAX_VALUE;
    for (int i = 0; i < State.numberOfBases; i++) {
      bestDist = Math.min(bestDist, Map.distances[Map.oppBases[i].index][index]);
    }
    return bestDist;
  }

  private void putBeacon(Cell c, int strength) {
    if (beacons[c.index] < strength) {
      beacons[c.index] = strength;
    }
  }

  
}
