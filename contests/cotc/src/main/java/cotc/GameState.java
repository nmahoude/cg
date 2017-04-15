package cotc;

import java.util.ArrayList;
import java.util.List;

import cotc.entities.Barrel;
import cotc.entities.CannonBall;
import cotc.entities.Mine;
import cotc.entities.Ship;

public class GameState {
  
  public int shipCount;
  public List<Team> teams = new ArrayList<>();

  public int rounds;
  public List<CannonBall> cannonballs = new ArrayList<>();
  public List<Mine> mines = new ArrayList<>();
  public List<Barrel> barrels = new ArrayList<>();
  public List<Ship> ships = new ArrayList<>(); // all ships
  public List<Ship> myShips = new ArrayList<>();
  public List<Ship> otherShips = new ArrayList<>();
  
  public int b_rounds;
  public List<CannonBall> b_cannonballs = new ArrayList<>();
  public List<Mine> b_mines = new ArrayList<>();
  public List<Barrel> b_barrels = new ArrayList<>();
  public List<Ship> b_ships = new ArrayList<>(); // all ships
  public List<Ship> b_myShips = new ArrayList<>();
  public List<Ship> b_otherShips = new ArrayList<>();

  public void debugOutput() {
    System.err.println("canonballs: "+cannonballs.size());
    System.err.println("mines: "+mines.size());
    System.err.println("barrels: "+barrels.size());
    System.err.println("ships: "+ships.size());
  }
  public void backup() {
    b_rounds = rounds;
    clearBackups();
    b_cannonballs.addAll(cannonballs);
    b_mines.addAll(mines);
    b_barrels.addAll(barrels);
    b_ships.addAll(ships);
    b_myShips.addAll(myShips);
    b_otherShips.addAll(otherShips);
    
    teams.forEach(Team::backup);
    cannonballs.forEach(CannonBall::backup);
    mines.forEach(Mine::backup);
    barrels.forEach(Barrel::backup);
    ships.forEach(Ship::backup);
  }

  public void restore() {
    rounds = b_rounds;
    clear();

    cannonballs.addAll(b_cannonballs);
    mines.addAll(b_mines);
    barrels.addAll(b_barrels);
    ships.addAll(b_ships);
    myShips.addAll(b_myShips);
    otherShips.addAll(b_otherShips);

    teams.forEach(Team::restore);
    cannonballs.forEach(CannonBall::restore);
    mines.forEach(Mine::restore);
    barrels.forEach(Barrel::restore);
    ships.forEach(Ship::restore);
  }

  public void clear() {
    cannonballs.clear();
    mines.clear();
    barrels.clear();
    ships.clear();
    myShips.clear();
    otherShips.clear();
  }

  public void clearBackups() {
    b_cannonballs.clear();
    b_mines.clear();
    b_barrels.clear();
    b_ships.clear();
    b_myShips.clear();
    b_otherShips.clear();
  }
  
  void initRound() {
    // kill all ships ! (will be revive in the update process)
    teams.get(0).shipsAlive.clear();
    teams.get(1).shipsAlive.clear();
    
    for (Ship ship : myShips) {
      ship.health = 0;
    }
    for (Ship ship : otherShips) {
      ship.health = 0;
    }
    
    ships.clear();
    cannonballs.clear();
    mines.clear();
    barrels.clear();
  }

  Ship getShip(List<Ship> fromShips, int entityId) {
    for (Ship entity : fromShips) {
      if (entity.id == entityId) {
        return entity;
      }
    }
    return null;
  }

  public void updateShip(Ship ship) {
    ships.add(ship);
    if (ship.owner == 0) {
      teams.get(0).shipsAlive.add(ship);
    } else {
      teams.get(1).shipsAlive.add(ship);
    }
  }
  
  public Barrel getClosestBarrel(Ship ship) {
    int bestDist = Integer.MAX_VALUE;
    Barrel best = null;
    for (Barrel barrel : barrels) {
      int dist = barrel.position.distanceTo(ship.position);
      if (dist < bestDist) {
        bestDist = dist;
        best = barrel;
      }
    }
    return best;
  }

  public Ship getClosestEnnemy(Ship me) {
    int bestDist = Integer.MAX_VALUE;
    Ship best = null;
    for (Ship other : otherShips) {
      if (other.health == 0) continue;
      int dist = other.position.distanceTo(me.position);
      if (dist < bestDist) {
        bestDist = dist;
        best = other;
      }
    }
    return best;
  }

}
