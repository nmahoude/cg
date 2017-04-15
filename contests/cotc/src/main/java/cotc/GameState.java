package cotc;

import java.util.ArrayList;
import java.util.List;

import cotc.entities.Barrel;
import cotc.entities.CannonBall;
import cotc.entities.Mine;
import cotc.entities.Ship;

public class GameState {
  public int shipCount;

  public int rounds;
  public List<Team> teams = new ArrayList<>();
  public List<CannonBall> cannonballs = new ArrayList<>();
  public List<Mine> mines = new ArrayList<>();
  public List<Barrel> barrels = new ArrayList<>();
  public List<Ship> ships = new ArrayList<>(); // all ships
  public List<Ship> myShips = new ArrayList<>();
  public List<Ship> otherShips = new ArrayList<>();
  
  public int b_rounds;
  public List<Team> b_teams = new ArrayList<>();
  public List<CannonBall> b_cannonballs = new ArrayList<>();
  public List<Mine> b_mines = new ArrayList<>();
  public List<Barrel> b_barrels = new ArrayList<>();
  public List<Ship> b_ships = new ArrayList<>(); // all ships
  public List<Ship> b_myShips = new ArrayList<>();
  public List<Ship> b_otherShips = new ArrayList<>();


  public void backup() {
    b_rounds = rounds;
    b_teams.addAll(teams);
    b_cannonballs.addAll(cannonballs);
    b_mines.addAll(mines);
    b_barrels.addAll(barrels);
    b_ships.addAll(ships);
    b_myShips.addAll(myShips);
    b_otherShips.addAll(otherShips);
    
    cannonballs.forEach(CannonBall::backup);
    mines.forEach(Mine::backup);
    barrels.forEach(Barrel::backup);
    ships.forEach(Ship::backup);
  }

  public void restore() {
    rounds = b_rounds;
    teams.clear();
    cannonballs.clear();
    mines.clear();
    barrels.clear();
    ships.clear();
    myShips.clear();
    otherShips.clear();

    teams.addAll(teams);
    cannonballs.addAll(cannonballs);
    mines.addAll(mines);
    barrels.addAll(barrels);
    ships.addAll(ships);
    myShips.addAll(myShips);
    otherShips.addAll(otherShips);

    cannonballs.forEach(CannonBall::restore);
    mines.forEach(Mine::restore);
    barrels.forEach(Barrel::restore);
    ships.forEach(Ship::restore);
  }
  
  void initRound() {
    // kill all ships ! (will be revive in the update process)
    for (Ship ship : myShips) {
      ship.health = 0;
    }
    for (Ship ship : otherShips) {
      ship.health = 0;
    }
    
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
}
