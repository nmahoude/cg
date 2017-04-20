package cotc.ai.ag.features;

import cotc.BarrelDomination;
import cotc.GameState;
import cotc.entities.Entity;
import cotc.entities.EntityType;
import cotc.entities.Ship;
import cotc.utils.Coord;

public class Feature {
  public static final int HIS_HEALTH_FEATURE = 1;
  public static final int BARREL_COUNT0_FEATURE = 2;
  public static final int BARREL_COUNT1_FEATURE = 3;
  public static final int RUM_COUNT0_FEATURE = 4;
  public static final int RUM_COUNT1_FEATURE = 5;
  public static final int MY_MOBILITY_FEATURE = 6;
  public static final int HIS_MOBILITY_FEATURE = 7;
  public static final int CANNONBALL_FIRED_FEATURE = 8;
  public static final int BARREL_COUNT_FEATURE = 9;
  public static final int MINE_DROPPED_FEATURE = 10;

  public static final int LAST = 17;

  public ShipFeature shipFeatures[] = new ShipFeature[3];
  public double features[] = new double[LAST];
  private int shipsCount;
  
  public Feature() {
    for (int i=0;i<3;i++) {
      shipFeatures[i] = new ShipFeature();
    }
  }

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
    
    shipsCount = state.teams[0].shipsAlive.FE;
    for (int s=0;s<state.teams[0].shipsAlive.FE;s++) {
      Ship ship = state.teams[0].shipsAlive.elements[s];
      shipFeatures[s].calculate(ship, state);
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

  public double applyWeights(FeatureWeight weights) {
    double total = 0;
    for (int i=0;i<LAST;i++) {
      total += weights.weights[i] * features[i];
    }
    for (int s=0;s<shipsCount;s++) {
      total += shipFeatures[s].applyWeights(weights.shipWeights[s]);
    }
    return total;
  }
}
