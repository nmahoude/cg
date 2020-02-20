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
  public static final int DISTANCE_TO_CLOSEST_ENEMY_BOW2_FEATURE = 2;
  public static final int SPEED_FEATURE = 3;
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
  public static final int HIS_DELTA_HEALTH_FEATURE = 17;
  public static final int DESTROYED_BARRELS = 18;
  public static final int DISTANCE_TO_CLOSEST_BARREL_FEATURE = 19;
  public static final int LAST = 20;

  public double features[] = new double[LAST];
  public static final String[] debugFeatures= {
      "myHealth          ",
      "hisHealth         ",
      "dist2closestBow2  ",
      "speed             ",
      "dist2AllBarrels   ",
      "BarrelCount0      ",
      "BarrelCount1      ",
      "RumCount0         ",
      "RumCount1         ",
      "dist2Center       ",
      "myMobility        ",
      "hisMobility       ",
      "dist2ClosestEnemy ",
      "distToAllEnemy    ",
      "cannonBallFired   ",
      "barrelCount       ",
      "MineDropped       ",
      "HisDeltaHealth    ",
      "destroyedBarrels  ",
      "dist2ClosestBarrel",
  };
  
  public final static double[] speeds = new double []{ 0.0, 1.0 , 1.3};
  
  public void calculateFeaturesFinal(GameState state) {

    BarrelDomination barrelDomination = state.getBarrelDominitation();
    features[BARREL_COUNT0_FEATURE] = barrelDomination.barrelCount0;
    features[BARREL_COUNT1_FEATURE] = barrelDomination.barrelCount1;
    features[RUM_COUNT0_FEATURE] = barrelDomination.rumCount0;
    features[RUM_COUNT1_FEATURE] = barrelDomination.rumCount1;
    features[CANNONBALL_FIRED_FEATURE] = state.firedCannonballs; // hack to know how many cannonballs have been shot during the simulation
    features[MINE_DROPPED_FEATURE] = state.droppedMines; // hack to know how many cannonballs have been shot during the simulation
    features[BARREL_COUNT_FEATURE] = state.barrels.length; // Number of barrels
    features[DESTROYED_BARRELS] = state.destroyedBarrels;
    updateMobilityFeature(state);
    
    for (int s=0;s<state.teams[1].shipsAlive.length;s++) {
      Ship ship = state.teams[1].shipsAlive.elements[s];
      features[HIS_HEALTH_FEATURE] += ship.health;
      features[HIS_DELTA_HEALTH_FEATURE] = (ship.health - ship.b_health);
    }
    
    for (int s=0;s<state.teams[0].shipsAlive.length;s++) {
      Ship ship = state.teams[0].shipsAlive.elements[s];
      if (ship.health < 0) continue;

      features[MY_HEALTH_FEATURE] += ship.health * (ship.champion ? 2 : 1);
      features[SPEED_FEATURE] += speeds[ship.speed];
      features[DISTANCE_TO_CENTER_FEATURE] += ship.position.distanceTo(Simulation.MAP_CENTER);

      // distances to ships
      int bestDist = Integer.MAX_VALUE;
      Ship closestShip = null;
      for (int s2=0;s2<state.teams[1].shipsAlive.length;s2++) {
        Ship other = state.teams[1].shipsAlive.elements[s2];
        if (other.health <= 0) continue;
        int distToShip = other.position.distanceTo(ship.position);
        features[DISTANCE_TO_ALL_ENEMY_FEATURE] += distToShip;
        if (distToShip < bestDist) {
          bestDist = distToShip;
          closestShip = other;
        }
      }
      features[DISTANCE_TO_CLOSEST_ENEMY_FEATURE] +=bestDist;
      // dist To Barrels
      bestDist = Integer.MAX_VALUE;
      for (int b = 0; b < state.barrels.length; b++) {
        Barrel barrel = state.barrels.elements[b];
        int distToBarrel = barrel.position.distanceTo(ship.position);
        if (distToBarrel < bestDist) bestDist = distToBarrel;
        features[DISTANCE_TO_ALL_BARREL_FEATURE] += distToBarrel;
      }
      features[DISTANCE_TO_CLOSEST_BARREL_FEATURE] += bestDist < Integer.MAX_VALUE ? bestDist : 0;
    }
    
    // update healths otherwise low health mean ship will suicide !
    double myHealth = features[MY_HEALTH_FEATURE];
    double hisHealth = features[HIS_HEALTH_FEATURE];
    double total = myHealth + hisHealth;
    if (total > 0) {
      features[MY_HEALTH_FEATURE] = 100.0*myHealth / total;
      features[HIS_HEALTH_FEATURE] = 100.0*hisHealth / total;
    }
    
    
  }

  public void reset() {
    for (int i=0;i<LAST;i++) {
      features[i] = 0;
    }
  }
  
  // TODO handle ship speed ????!!!
  // TODO handle cannonBalls
  private void updateMobilityFeature(GameState state) {
    // sale hack sans risque (pun intended).....
    for (int i=0;i<state.ships.length;i++) {
      Ship ship = state.ships.elements[i];
      if (ship.health<=0) continue;
      state.setEntityAt(ship.position, ship);
      state.setEntityAt(ship.bow(), ship);
      state.setEntityAt(ship.stern(), ship);
    }
    
    for (int i=0;i<state.ships.length;i++) {
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

    for (int i=0;i<state.ships.length;i++) {
      Ship ship = state.ships.elements[i];
      if (ship.health<=0) continue;
      state.setEntityAt(ship.position, null);
      state.setEntityAt(ship.bow(), null);
      state.setEntityAt(ship.stern(), null);
    }

  }

  public void debugFeature(FeatureWeight weight) {
    for (int i=0;i<LAST;i++) {
      debugFeature(weight, i);
    }
  }

  private void debugFeature(FeatureWeight weight, int i) {
    System.err.printf("%s = %.0f * %.2f = %.2f\n",
        debugFeatures[i], features[i], weight.weights[i],
        features[i]*weight.weights[i]
        );
  }
    
  public double applyWeights(FeatureWeight weights) {
    double total = 0;
    for (int i=0;i<LAST;i++) {
      total += weights.weights[i] * features[i];
    }
    return total;
  }
}
