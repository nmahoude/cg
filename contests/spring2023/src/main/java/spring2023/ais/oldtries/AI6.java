package spring2023.ais.oldtries;

import spring2023.Player;
import spring2023.State;
import spring2023.ais.AI;
import spring2023.ais.AttackChainCalculator;
import spring2023.map.Cell;
import spring2023.map.Map;
import spring2023.map.MapData;
import spring2023.map.Path;

public class AI6 implements AI {
  private static AttackChainCalculator ACC = new AttackChainCalculator();
  
  @Override
  public void think(State state, int[] beacons) {
    
    MapData base = Map.myBases[0];
    
    ACC.update(state);
    
    
    int allAnts = state.totalMyAnts;
    
    while(true) {
      double bestScore = Double.NEGATIVE_INFINITY;
      Cell bestTarget = null;
      int goal = 0;
      
      for (Cell target : state.cells) {
        if (target.resources == 0) continue;
        
        double score = 0.0;
        
        int cost = 0;
        Path path = Map.paths[base.index][target.index];
        
        
        
        int newGoal;
        if (beacons[path.target.index] == 0 && ACC.minChains[target.index] > 0) {
          newGoal = ACC.minChains[target.index];
        } else {
          newGoal = beacons[target.index]+1;
        }
        
        for (MapData md : path.path) {
          cost+= Math.max(0, newGoal - beacons[md.index]);
        }

        if (cost > allAnts) {
          score = Double.NEGATIVE_INFINITY;
        } else {
          score -= 1.0 * cost;
        }
        
        if (Player.turn < 10) {
          if (target.data.type == Map.CELL_EGGS) {
            score += 0.5; // bonus for eggs
          }
        }
        
        
        
        if (score > bestScore) {
          bestScore = score;
          bestTarget = target;
        }
      }
      
      if (bestTarget == null) {
        break;
      }
      
      System.err.println("Best target is "+bestTarget+" with score "+bestScore);
      Path path = Map.paths[base.index][bestTarget.index];

      int newGoal = beacons[path.target.index]+1;
      state.cells[bestTarget.index].resources--;
      
      for (MapData md : path.path) {
        int needed = Math.max(0, newGoal - beacons[md.index]);
        beacons[md.index] += needed;
        allAnts-= needed;
      }
      
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
