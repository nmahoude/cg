package spring2023.old;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AI3 {

  private State state;

  private int[] beacons;

  public void think(State state, int[] beacons) {
    naiveBeacons(state, beacons);
    
    optimizedBeacons(state, beacons);
  }

  public void optimizedBeacons(State state2, int[] beacons2) {
  }

  public void naiveBeacons(State state, int[] beacons) {
    this.state = state;
    this.beacons = beacons;
    int resources[] = new int[Map.MAX_CELLS];
    int totalAnts = 0;
    for (Cell c : Map.cells) {
      totalAnts += c.myAnts;
      resources[c.index] = c.resources;
      beacons[c.index] = 0;
    }
    final int backupTotalAnts = totalAnts;
    Set<Cell> currentTargets = new HashSet<>();
    Set<Cell> oppCurrentTargets = new HashSet<>();
    for (int i = 0; i < state.numberOfBases; i++) {
      currentTargets.add(State.myBases[i]);
      oppCurrentTargets.add(State.oppBases[i]);
    }
    AStar astar = new AStar();
    int minChains[] = new int[Map.MAX_CELLS];
    for (int i = 0; i < Map.MAX_CELLS; i++) {
      if (resources[i] == 0)
        continue;
      double score2 = astar.search(new MyMaximumAnts(), currentTargets, Map.cells[i]);
      int myMinAnts = Integer.MAX_VALUE;
      for (Cell p : astar.path) {
        myMinAnts = Math.min(p.myAnts, myMinAnts);
      }
      if (myMinAnts == Integer.MAX_VALUE) {
        myMinAnts = 0;
      }
      double score = astar.search(new OppMaximumAnts(), oppCurrentTargets, Map.cells[i]);
      int oppMinAnts = Integer.MAX_VALUE;
      for (Cell p : astar.path) {
        oppMinAnts = Math.min(p.oppAnts, oppMinAnts);
      }
      if (oppMinAnts == Integer.MAX_VALUE) {
        oppMinAnts = 0;
      }
      System.err.println(
          "Min ants on path for " + Map.cells[i] + " is " + myMinAnts + " for me and " + oppMinAnts + " for opp");
      minChains[i] = oppMinAnts;
    }

    // focus
    boolean focusOnCrystals = OldPlayer.turn > 80 || (1.0 * state.myScore / State.originalTotalCrystalsGoal > 0.9);

    boolean found = false;
    do {
      found = false;
      int bestResources = -1;
      double bestScore = Double.NEGATIVE_INFINITY;
      List<Cell> bestPath = new ArrayList<>(10);
      for (int i = 0; i < Map.MAX_CELLS; i++) {
        if (resources[i] == 0)
          continue;

        // if (Player.turn < 10 && distToBases(i) > 6) {
        // System.err.println("Distance to base is "+distToBases(i)+" ignoring "+i);
        // continue; // don't go too far
        // }

        if (focusOnCrystals && Map.cells[i].type == 1) {
          continue;
        }

        double score = 100 - astar.search(currentTargets, Map.cells[i]);
        if (OldPlayer.turn < 10 && Map.cells[i].type == 1) {
          score += 10;
        }
        if (astar.path.size() * minChains[i] > backupTotalAnts) {
          continue;
        }
        if (score > bestScore) {
          bestScore = score;
          bestResources = i;
          bestPath.clear();
          bestPath.addAll(astar.path);
        }
      }
      System.err.println("Best resource is " + bestResources + " with score " + bestScore + " and path " + bestPath);
      if (bestResources != -1) {
        int numberOfAnts = Math.max(1, minChains[bestResources] + 1);
        for (Cell p : bestPath) {
          if (numberOfAnts > beacons[p.index]) {
            int neededAnts = numberOfAnts - beacons[p.index];
            totalAnts -= neededAnts;
            if (OldPlayer.LOCAL_DEBUG) {
              System.err.println("removing " + neededAnts + " at " + p);
            }
          }
        }
        if (totalAnts > 0) {
          for (Cell p : bestPath) {
            if (numberOfAnts > beacons[p.index]) {
              beacons[p.index] = numberOfAnts;
            }
          }
        }
        if (OldPlayer.LOCAL_DEBUG) {
          System.err.println("remaining Ants " + totalAnts);
        }
        resources[bestResources] = 0;
        if (totalAnts > 0) {
          found = true;
        }
      }
    } while (found);
    System.err.println("End of ai");
  }

  private int distToBases(int index) {
    int bestDist = Integer.MAX_VALUE;
    for (int i = 0; i < State.numberOfBases; i++) {
      bestDist = Math.min(bestDist, Map.distances[State.myBases[i].index][index]);
    }
    return bestDist;
  }

  private void putBeacon(Cell c, int strength) {
    if (beacons[c.index] < strength) {
      beacons[c.index] = strength;
    }
  }
}