package spring2023.ais;

import spring2023.Player;
import spring2023.State;

public class HardCodedAI implements AI {
  private State state;
  private int[] beacons;

  public void think(State state, int[] beacons) {
    this.state = state;
    this.beacons = beacons;

   
    
    int totalAnts = state.totalMyAnts;
    if (Player.turn < 4) {
      beacons[20] = 100 * 7 / totalAnts ;
      beacons[22] = 100 * 1 / totalAnts ;
      beacons[24] = 100 * 1 / totalAnts ;
      beacons[26] = 100 * 1 / totalAnts ;
    }

    
    if (Player.turn == 7) {
      beacons[20] = 100 * 6 / totalAnts ;
      beacons[22] = 100 * 0 / totalAnts ;
      beacons[24] = 100 * 0 / totalAnts ;
      beacons[26] = 100 * 0 / totalAnts ;
      beacons[36] = 100 * 4 / totalAnts ;
      
    }
    if (Player.turn == 8) {
      beacons[20] = 100 * 6 / totalAnts ;
      beacons[22] = 100 * 1 / totalAnts ;
      beacons[24] = 100 * 1 / totalAnts ;
      beacons[26] = 100 * 1 / totalAnts ;
      beacons[36] = 100 * 1 / totalAnts ;
      
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
