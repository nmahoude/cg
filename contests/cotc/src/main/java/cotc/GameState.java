package cotc;

import java.util.ArrayList;
import java.util.List;

import cotc.entities.Barrel;
import cotc.entities.CannonBall;
import cotc.entities.Mine;
import cotc.entities.Ship;
import cotc.game.Simulation;

public class GameState {
  
  public static final int MAPCACHE_MINE = 1;
  public static final int MAPCACHE_BARREL = 2;
  
  public int shipCount;
  public List<Team> teams = new ArrayList<>();

  public int rounds;
  public List<CannonBall> cannonballs = new ArrayList<>();
  public List<Mine> mines = new ArrayList<>();
  public List<Barrel> barrels = new ArrayList<>();
  public List<Ship> ships = new ArrayList<>(); // all ships
  public int mapCache[][] = new int[Simulation.MAP_WIDTH][Simulation.MAP_HEIGHT];
  
  public int b_rounds;
  public List<CannonBall> b_cannonballs = new ArrayList<>();
  public List<Mine> b_mines = new ArrayList<>();
  public List<Barrel> b_barrels = new ArrayList<>();
  public List<Ship> b_ships = new ArrayList<>(); // all ships
  public int b_mapCache[][] = new int[Simulation.MAP_WIDTH][Simulation.MAP_HEIGHT];

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
    
    teams.forEach(Team::backup);
    cannonballs.forEach(CannonBall::backup);
    mines.forEach(Mine::backup);
    barrels.forEach(Barrel::backup);
    ships.forEach(Ship::backup);
    
    // before backuping the state, we update the map (hopefully, we won't backup to much ^^)
    createMapCache();
    for (int x=0;x<Simulation.MAP_WIDTH;x++) {
      for (int y=0;y<Simulation.MAP_HEIGHT;y++) {
        b_mapCache[x][y] = mapCache[x][y];
      }
    }
  }
  private void createMapCache() {
    for (int x=0;x<Simulation.MAP_WIDTH;x++) {
      for (int y=0;y<Simulation.MAP_HEIGHT;y++) {
        mapCache[x][y] = 0;
      }
    }
    mines.forEach(mine -> mapCache[mine.position.x][mine.position.y] = MAPCACHE_MINE);
    barrels.forEach(barrel -> mapCache[barrel.position.x][barrel.position.y] = MAPCACHE_BARREL);
  }

  public void restore() {
    rounds = b_rounds;
    clear();

    cannonballs.addAll(b_cannonballs);
    mines.addAll(b_mines);
    barrels.addAll(b_barrels);
    ships.addAll(b_ships);

    teams.forEach(Team::restore);
    cannonballs.forEach(CannonBall::restore);
    mines.forEach(Mine::restore);
    barrels.forEach(Barrel::restore);
    ships.forEach(Ship::restore);

    for (int x=0;x<Simulation.MAP_WIDTH;x++) {
      for (int y=0;y<Simulation.MAP_HEIGHT;y++) {
        mapCache[x][y] = b_mapCache[x][y];
      }
    }
  }

  public void clear() {
    cannonballs.clear();
    mines.clear();
    barrels.clear();
    ships.clear();
  }

  public void clearBackups() {
    b_cannonballs.clear();
    b_mines.clear();
    b_barrels.clear();
    b_ships.clear();
  }
  
  void initRound() {
    // kill all ships ! (will be revive in the update process)
    teams.get(0).shipsAlive.clear();
    teams.get(1).shipsAlive.clear();

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
    for (Ship other : teams.get(1).shipsAlive) {
      if (other.health == 0) continue;
      int dist = other.position.distanceTo(me.position);
      if (dist < bestDist) {
        bestDist = dist;
        best = other;
      }
    }
    return best;
  }
  public BarrelDomination getBarrelDominitation() {
    BarrelDomination bd = new BarrelDomination();
    
    for (Barrel barrel : barrels) {
      Ship closest = null;
      int best = Integer.MAX_VALUE;
      for (Ship ship : ships) {
        if (ship.health <= 0) continue;
        int dist = ship.position.distanceTo(barrel.position);
        if (dist < best) {
          best = dist;
          closest = ship;
        }
      }
      if (closest != null) {
        if (closest.owner == 0) {
          bd.barrelCount0+=1; 
          bd.rumCount0 += barrel.health;
        } else {
          bd.barrelCount1+=1;
          bd.rumCount1 += barrel.health;
        }
      }
    }
    return bd;
  }

}
