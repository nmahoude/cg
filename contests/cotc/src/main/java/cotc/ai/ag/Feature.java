package cotc.ai.ag;

import cotc.BarrelDomination;
import cotc.GameState;
import cotc.entities.Barrel;
import cotc.entities.Entity;
import cotc.entities.EntityType;
import cotc.entities.Ship;
import cotc.game.Simulation;
import cotc.utils.Coord;

public class Feature {
  public static final int MY_HEALTH_FEATURE = 0;
  public static final int HIS_HEALTH_FEATURE = 1;
  public static final int SPEED_FEATURE = 2;
  public static final int DISTANCE_TO_CLOSEST_BARREL_FEATURE = 3;
  public static final int DISTANCE_TO_ALL_BARREL_FEATURE = 4;
  public static final int BARREL_COUNT0_FEATURE = 5;
  public static final int BARREL_COUNT1_FEATURE = 6;
  public static final int RUM_COUNT0_FEATURE = 7;
  public static final int RUM_COUNT1_FEATURE = 8;
  public static final int DISTANCE_TO_CENTER_FEATURE = 9;
  public static final int MY_MOBILITY_FEATURE = 10;
  public static final int HIS_MOBILITY_FEATURE = 11;
  public static final int DISTANCE_TO_CLOSEST_ENEMY_FEATURE = 12;
  public static final int DISTANCE_TO_ALL_ENEMY_FEATURE = 13;
  public static final int CANNONBALL_FIRED_FEATURE = 14;
  public static final int BARREL_COUNT_FEATURE = 15;
  public static final int MINE_DROPPED_FEATURE = 16;

  public static final int LAST = 17;

  public double features[] = new double[LAST];
  
  public void calculateFeatures(GameState state) {
    for (int i=0;i<LAST;i++) {
      features[i] = 0;
    }

    BarrelDomination barrelDomination = state.getBarrelDominitation();
    features[BARREL_COUNT0_FEATURE] = barrelDomination.barrelCount0;
    features[BARREL_COUNT1_FEATURE] = barrelDomination.barrelCount1;
    features[RUM_COUNT0_FEATURE] = barrelDomination.rumCount0;
    features[RUM_COUNT1_FEATURE] = barrelDomination.rumCount1;
    features[CANNONBALL_FIRED_FEATURE] = state.firedCannonballs; // hack to know how many cannonballs have been shot during the simulation
    features[MINE_DROPPED_FEATURE] = state.droppedMines; // hack to know how many cannonballs have been shot during the simulation
    features[BARREL_COUNT_FEATURE] = state.barrels.FE; // Number of barrels

    updateMobilityFeature(state);
    
    for (int s=0;s<state.teams[1].shipsAlive.FE;s++) {
      Ship ship = state.teams[1].shipsAlive.elements[s];
      features[HIS_HEALTH_FEATURE] += ship.health;
    }
    
    for (int s=0;s<state.teams[0].shipsAlive.FE;s++) {
      Ship ship = state.teams[0].shipsAlive.elements[s];

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
  }
  
  // TODO handle ship speed ????!!!
  // TODO handle cannonBalls
  private void updateMobilityFeature(GameState state) {
    // sale hack sans risque (pun intended).....
    for (int i=0;i<state.ships.FE;i++) {
      Ship ship = state.ships.elements[i];
      if (ship.health<=0) continue;
      state.setEntityAt(ship.position, ship);
      state.setEntityAt(ship.bow(), ship);
      state.setEntityAt(ship.stern(), ship);
    }
    
    for (int i=0;i<state.ships.FE;i++) {
      Ship ship = state.ships.elements[i];
      if (ship.health<=0) continue;
//      Coord nextShipPosition = ship.position;
//      for (int i=0;i<ship.speed;i++) {
//        nextShipPosition = nextShipPosition.neighborsCache[ship.orientation];
//      }
      
      int newOrientation = (ship.orientation + 1) % 6;
      Coord newBow = ship.position.neighborsCache[newOrientation];
      Coord newStern = ship.position.neighborsCache[(newOrientation + 3) %6];
      Entity newBowEntity = newBow.isInsideMap() ? state.getEntityAt(newBow) : null;
      Entity newSternEntity = newStern.isInsideMap() ? state.getEntityAt(newStern) : null;
      boolean canPort = (newBowEntity == null || newBowEntity.type == EntityType.BARREL)
                     && (newSternEntity == null || newSternEntity.type == EntityType.BARREL) ;
      
      newOrientation = (ship.orientation + 5) % 6;
      newBow = ship.position.neighborsCache[newOrientation];
      newStern = ship.position.neighborsCache[(newOrientation + 3) %6];
      newBowEntity = newBow.isInsideMap() ? state.getEntityAt(newBow) : null;
      newSternEntity = newStern.isInsideMap() ? state.getEntityAt(newStern) : null;
      boolean canStarboard= (newBowEntity == null || newBowEntity.type == EntityType.BARREL)
                     && (newSternEntity == null || newSternEntity.type == EntityType.BARREL) ;

      Coord newPosition = ship.position.neighborsCache[ship.orientation];
      Entity newPositionEntity = newPosition.isInsideMap() ? state.getEntityAt(newPosition) : null;
      boolean canMove1 = newPositionEntity == null || newPositionEntity == ship || newPositionEntity.type == EntityType.BARREL;
      newPosition= newPosition.neighborsCache[ship.orientation];
      newPositionEntity = newPosition.isInsideMap() ? state.getEntityAt(newPosition) : null;
      boolean canMove2 = canMove1 && (newPosition.isInsideMap() && (newPositionEntity == null || newPositionEntity.type == EntityType.BARREL));

      int mobility = (canPort ? 1:0) 
                       + (canStarboard ? 1:0) 
                       + (canMove1 ? 1:0) 
                       + (canMove2 ? 1:0);
      if (ship.owner == 0) {
        features[MY_MOBILITY_FEATURE] += mobility;
      } else {
        features[HIS_MOBILITY_FEATURE] += mobility;
      }
    }

    for (int i=0;i<state.ships.FE;i++) {
      Ship ship = state.ships.elements[i];
      if (ship.health<=0) continue;
      state.setEntityAt(ship.position, null);
      state.setEntityAt(ship.bow(), null);
      state.setEntityAt(ship.stern(), null);
    }

  }

  public void debug() {
    System.err.println("HealtFeature: " + features[MY_HEALTH_FEATURE] + " / "+features[HIS_HEALTH_FEATURE]);
    System.err.println("MobilityFeature: " + features[MY_MOBILITY_FEATURE] + " / "+features[HIS_MOBILITY_FEATURE]);
    System.err.println("speedFeature: " + features[SPEED_FEATURE]);
    System.err.println("distToBarrelFeature: " + features[DISTANCE_TO_CENTER_FEATURE]);
    System.err.println("distanceToCenterFeature: " + features[DISTANCE_TO_CENTER_FEATURE]);
    System.err.println("distanceToClosestBarrelFeature: " + features[DISTANCE_TO_CLOSEST_BARREL_FEATURE]);
  }
  public double applyWeights(FeatureWeight weights) {
    double total = 0;
    for (int i=0;i<LAST;i++) {
      total += weights.weights[i] * features[i];
    }
    return total;
  }
}
