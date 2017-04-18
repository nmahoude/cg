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
  public int myHealtFeature;
  public int speedFeature;
  public int distToBarrelFeature;
  public int hisHealthFeature;
  public BarrelDomination barrelDomination;
  public int distanceToCenterFeature;
  public int distanceToClosestBarrelFeature;
  public int myMobilityFeature;
  public int hisMobilityFeature;

  public void calculateFeatures(GameState state) {
    myHealtFeature = 0;
    hisHealthFeature = 0;
    speedFeature = 0;
    distToBarrelFeature = 0;
    distanceToCenterFeature = 0;
    distanceToClosestBarrelFeature = 0;
    myMobilityFeature = 0;
    hisMobilityFeature = 0;
        
    barrelDomination = state.getBarrelDominitation();

    if (state.barrels.size() > 0) {
      for (Ship ship : state.teams.get(0).shipsAlive) {
        if (ship.health < 75) {
          distanceToClosestBarrelFeature += 40-state.getClosestBarrelDistance(ship);
        }
      }
    }
    
    updateMobilityFeature(state);
    
    for (Ship ship : state.teams.get(0).shipsAlive) {
      myHealtFeature += ship.health;
      speedFeature += ship.speed;
      distanceToCenterFeature += 10-(ship.position.distanceTo(Simulation.MAP_CENTER));
    }
    
    for (Ship ship : state.teams.get(1).shipsAlive) {
      hisHealthFeature += ship.health;
    }
    
    // distToBarrel
    for (Ship ship : state.teams.get(0).shipsAlive) {
      Barrel barrel = state.getClosestBarrel(ship);
      if (barrel != null) {
        int dist = barrel.position.distanceTo(ship.position);
        distToBarrelFeature +=(45.0-dist) / 45.0;
      } else {
        break; // don't go further, no more barrels
      }
    }
  }
  // TODO handle ship speed ????!!!
  // TODO handle cannonBalls
  private void updateMobilityFeature(GameState state) {
    // sale hack sans risque (pun intended).....
    for (Ship ship : state.ships) {
      if (ship.health<=0) continue;
      state.setEntityAt(ship.position, ship);
      state.setEntityAt(ship.bow(), ship);
      state.setEntityAt(ship.stern(), ship);
    }
    
    for (Ship ship : state.ships) {
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
        myMobilityFeature += mobility;
      } else {
        hisMobilityFeature += mobility;
      }
    }

    for (Ship ship : state.ships) {
      if (ship.health<=0) continue;
      state.setEntityAt(ship.position, null);
      state.setEntityAt(ship.bow(), null);
      state.setEntityAt(ship.stern(), null);
    }

  }

  public void debug() {
    System.err.println("HealtFeature: " + myHealtFeature + " / "+hisHealthFeature);
    System.err.println("MobilityFeature: " + myMobilityFeature + " / "+hisMobilityFeature);
    System.err.println("speedFeature: " + speedFeature);
    System.err.println("distToBarrelFeature: " + distanceToCenterFeature);
    System.err.println("distanceToCenterFeature: " + distanceToCenterFeature);
    System.err.println("distanceToClosestBarrelFeature: " + distanceToClosestBarrelFeature);
    barrelDomination.debug();
  }
}
