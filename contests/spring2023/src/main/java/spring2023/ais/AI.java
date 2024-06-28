package spring2023.ais;

import spring2023.State;

public interface AI {

  void think(State state, int[] beacons);
  void naiveBeacons(State state, int[] beacons);
  void reworkingBeaconsStrength(State state, int[] beacons);
  void limitingBeaconsStrength(State state, int[] beacons);
  void optimizeBeacons(State state, int[] beacons);
}