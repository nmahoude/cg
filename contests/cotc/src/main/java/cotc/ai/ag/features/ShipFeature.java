package cotc.ai.ag.features;

import cotc.GameState;
import cotc.entities.Barrel;
import cotc.entities.Ship;
import cotc.game.Simulation;

public class ShipFeature {
  public static final int MY_HEALTH_FEATURE = 1;
  public static final int SPEED_FEATURE = 2;
  public static final int DISTANCE_TO_CENTER_FEATURE = 3;
  public static final int DISTANCE_TO_ALL_ENEMY_FEATURE = 4;
  public static final int DISTANCE_TO_CLOSEST_ENEMY_FEATURE = 5;
  public static final int DISTANCE_TO_ALL_BARREL_FEATURE = 6;
  public static final int DISTANCE_TO_CLOSEST_BARREL_FEATURE = 7;
  public static final int LAST = 8;
  public double features[] = new double[LAST];

  public void calculate(Ship ship, GameState state) {
    for (int i=0;i<LAST;i++) {
      features[i] = 0;
    }
    features[MY_HEALTH_FEATURE] += ship.health;
    features[SPEED_FEATURE] += ship.speed;
    features[DISTANCE_TO_CENTER_FEATURE] += ship.position.distanceTo(Simulation.MAP_CENTER);

    // distances to ships
    int bestDist = Integer.MAX_VALUE;
    for (int s2=0;s2<state.teams[1].shipsAlive.FE;s2++) {
      Ship other = state.teams[1].shipsAlive.elements[s2];
      if (other.health <= 0) continue;
      int distToShip = other.position.distanceTo(ship.position);
      features[DISTANCE_TO_ALL_ENEMY_FEATURE] += distToShip;
      if (distToShip < bestDist) {
        bestDist = distToShip;
      }
    }
    features[DISTANCE_TO_CLOSEST_ENEMY_FEATURE] +=bestDist;

    // dist To Barrels
    bestDist = Integer.MAX_VALUE;
    for (int b = 0; b < state.barrels.FE; b++) {
      Barrel barrel = state.barrels.elements[b];
      int distToBarrel = barrel.position.distanceTo(ship.position);
      if (distToBarrel < bestDist) bestDist = distToBarrel;
      features[DISTANCE_TO_ALL_BARREL_FEATURE] += distToBarrel;
    }
    features[DISTANCE_TO_CLOSEST_BARREL_FEATURE] += bestDist < Integer.MAX_VALUE ? bestDist : 0;
  }

  public double applyWeights(ShipWeight weights) {
    double total = 0;
    for (int i=0;i<LAST;i++) {
      total += weights.weights[i] * features[i];
    }
    return total;
  }
}
