package god;

import cgcollections.arrays.FastArray;
import god.entities.Drone;
import god.entities.Zone;

public class GameState {
  public static final int WIDTH = 4000;
  public static final int HEIGHT = 1800;
  
  FastArray<Drone> drones = new FastArray<>(Drone.class, 50);
  FastArray<Zone> zones = new FastArray<>(Zone.class, 8);
}
