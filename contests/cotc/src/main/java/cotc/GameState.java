package cotc;

import java.util.ArrayList;
import java.util.List;

import cotc.entities.Barrel;
import cotc.entities.CannonBall;
import cotc.entities.Entity;
import cotc.entities.Mine;
import cotc.entities.Ship;
import cotc.game.Simulation;
import cotc.utils.Coord;

public class GameState {
  private Entity mapEmptyCache[] = new Entity[Simulation.MAP_WIDTH*Simulation.MAP_HEIGHT];
  
  public int shipCount;
  public List<Team> teams = new ArrayList<>();

  public int rounds;
  public List<CannonBall> cannonballs = new ArrayList<>();
  private List<Mine> mines = new ArrayList<>();
  public List<Barrel> barrels = new ArrayList<>();
  public List<Ship> ships = new ArrayList<>(); // all ships
  private Entity mapCache[] = new Entity[Simulation.MAP_WIDTH*Simulation.MAP_HEIGHT];
  
  public int b_rounds;
  public List<CannonBall> b_cannonballs = new ArrayList<>();
  public List<Mine> b_mines = new ArrayList<>();
  public List<Barrel> b_barrels = new ArrayList<>();
  public List<Ship> b_ships = new ArrayList<>(); // all ships
  public Entity b_mapCache[] = new Entity[Simulation.MAP_WIDTH*Simulation.MAP_HEIGHT];

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
    System.arraycopy(mapCache, 0, b_mapCache, 0, Simulation.MAP_WIDTH*Simulation.MAP_HEIGHT);
  }
  private void createMapCache() {
    System.arraycopy(mapEmptyCache, 0, mapCache, 0, Simulation.MAP_WIDTH*Simulation.MAP_HEIGHT);
    mines.forEach(mine -> this.setEntityAt(mine.position, mine));
    barrels.forEach(barrel -> this.setEntityAt(barrel.position, barrel));
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

    System.arraycopy(b_mapCache, 0, mapCache, 0, Simulation.MAP_WIDTH*Simulation.MAP_HEIGHT);
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
  public Entity getEntityAt(Coord coord) {
    return mapCache[coord.x+coord.y*Simulation.MAP_WIDTH];
  }
  public void clearEntityAt(Coord coord) {
    mapCache[coord.x+coord.y*Simulation.MAP_WIDTH] = null;
  }
  public void setEntityAt(Coord coord, Entity entity) {
    mapCache[coord.x+coord.y*Simulation.MAP_WIDTH] = entity;
  }

}
