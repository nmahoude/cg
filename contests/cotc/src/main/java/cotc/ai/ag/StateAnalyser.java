package cotc.ai.ag;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cotc.GameState;
import cotc.entities.Entity;
import cotc.entities.EntityType;
import cotc.entities.Ship;
import cotc.utils.Coord;

public class StateAnalyser {
  public static class PerShip {
    boolean enemyAtStern = false; // do we have a ship that can takes a mine
    boolean canMove[] = new boolean[2];
    boolean mineAt[] = new boolean[2];
    public Ship closestEnemy;
    public void debug() {
      System.err.println("enemy at stern "+enemyAtStern);
      System.err.println("canMoves: "+canMove[0]+" "+canMove[1]);
      System.err.println("mines: "+mineAt[0]+" "+mineAt[1]);
      System.err.println("Closest enemy : "+closestEnemy.id);
    }
  }
  
  Map<Ship, PerShip> analyse = new HashMap<>();
  
  public void analyse(GameState state) {
    analyse.clear();
    putShipsOnMap(state);
    
    for (int s=0;s<state.ships.FE;s++) {
      Ship ship = state.ships.elements[s];
      if (ship.health <=0) continue;
      
      PerShip info = new PerShip();
      analyse.put(ship,  info);

      // closest enemy
      info.closestEnemy = state.getClosestEnnemy(ship);
      
      // what is forward (stuck or mine) ?
      Coord next = ship.bow();
      for (int i=0;i<3;i++) {
        if (next.isInsideMap()) {
          Entity entityAt = state.getEntityAt(next);
          if (i<2) {
            info.canMove[i] = (entityAt == null) || entityAt == ship || entityAt.type != EntityType.SHIP;
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
        for (int s2=0;s2<state.ships.FE;s2++) {
          Ship other = state.ships.elements[s2];
          if (other.health<=0 || other.owner == ship.owner) continue;
          if (other.bow().neighborsCache[other.orientation] == preBow) {
            info.enemyAtStern = true;
          }
        }
      }
    }

    removeShipsFromMap(state);
  }

  private void removeShipsFromMap(GameState state) {
    for (int i=0;i<state.ships.FE;i++) {
      Ship ship = state.ships.elements[i];
      if (ship.health<=0) continue;
      state.setEntityAt(ship.position, null);
      state.setEntityAt(ship.bow(), null);
      state.setEntityAt(ship.stern(), null);
    }
  }

  private void putShipsOnMap(GameState state) {
    for (int i=0;i<state.ships.FE;i++) {
      Ship ship = state.ships.elements[i];
      if (ship.health<=0) continue;
      state.setEntityAt(ship.position, ship);
      state.setEntityAt(ship.bow(), ship);
      state.setEntityAt(ship.stern(), ship);
    }
  }

  public void debug() {
    for (Entry<Ship, PerShip> entry : analyse.entrySet()) {
      Ship ship = entry.getKey();
      PerShip info = entry.getValue();
      System.err.println("Ship "+ship.id);
      info.debug();
    }
  }
}
