package cotc.ai.ag.ref3;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cotc.GameState;
import cotc.entities.Entity;
import cotc.entities.EntityType;
import cotc.entities.Ship;
import cotc.utils.Coord;

public class StateAnalyser {
  
  Map<Ship, ShipStateAnalysis> analyse = new HashMap<>();
  
  public void analyse(GameState state) {
    analyse.clear();
    putShipsOnMap(state);
    
    for (int s=0;s<state.ships.length;s++) {
      Ship ship = state.ships.elements[s];
      if (ship.health <=0) continue;
      
      ShipStateAnalysis info = new ShipStateAnalysis();
      analyse.put(ship,  info);

      // closest enemy
      info.closestEnemy = state.getClosestEnnemy(ship);
      
      // what is forward (stuck or mine) ?
      Coord next = ship.bow();
      boolean canMove = true;
      for (int i=0;i<3;i++) {
        if (next.isInsideMap()) {
          Entity entityAt = state.getEntityAt(next);
          if (i<2) {
            canMove = canMove && ((entityAt == null) || entityAt == ship || entityAt.type != EntityType.SHIP);
            info.canMove[i] = canMove;
          }
          if (i>0) {
            info.mineAt[i-1] = entityAt != null && entityAt.type == EntityType.MINE;
          }
        }
        next = next.neighborsCache[ship.orientation];
      }
  
      // enemy at stern ?
      int sternOrientation = (ship.orientation + 3) %6;
      Coord preBow = ship.position.neighborsCache[sternOrientation].neighborsCache[sternOrientation];
      if (preBow.isInsideMap()) {
        for (int s2=0;s2<state.ships.length;s2++) {
          Ship other = state.ships.elements[s2];
          if (other.health<=0 || other.owner == ship.owner) continue;
          Coord frontOfBow = other.bow().neighborsCache[other.orientation];
          if (frontOfBow == preBow) {
            info.enemyAtStern[0] = true;
          }
          if (frontOfBow.neighborsCache[other.orientation] == preBow) {
            info.enemyAtStern[1] = true;
          }
        }
      }
    }

    removeShipsFromMap(state);
  }

  private void removeShipsFromMap(GameState state) {
    for (int i=0;i<state.ships.length;i++) {
      Ship ship = state.ships.elements[i];
      if (ship.health<=0) continue;
      state.setEntityAt(ship.position, null);
      state.setEntityAt(ship.bow(), null);
      state.setEntityAt(ship.stern(), null);
    }
  }

  private void putShipsOnMap(GameState state) {
    for (int i=0;i<state.ships.length;i++) {
      Ship ship = state.ships.elements[i];
      if (ship.health<=0) continue;
      state.setEntityAt(ship.position, ship);
      state.setEntityAt(ship.bow(), ship);
      state.setEntityAt(ship.stern(), ship);
    }
  }

  public void debug() {
    for (Entry<Ship, ShipStateAnalysis> entry : analyse.entrySet()) {
      Ship ship = entry.getKey();
      ShipStateAnalysis info = entry.getValue();
      System.err.println("Ship "+ship.id);
      info.debug();
    }
  }
}
