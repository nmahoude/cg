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
import cotc.utils.FastArray;

public class GameState {
  private static Entity mapEmptyCache[] = new Entity[Simulation.MAP_WIDTH*Simulation.MAP_HEIGHT];
  
  public int shipCount;
  public List<Team> teams = new ArrayList<>();

  public int rounds;
  public FastArray<CannonBall> cannonballs = new FastArray<>(CannonBall.class, 100);
  public FastArray<Mine> mines = new FastArray<>(Mine.class, 100);
  public FastArray<Barrel> barrels = new FastArray<>(Barrel.class, 100);
  public FastArray<Ship> ships = new FastArray<>(Ship.class, 6);
  private Entity mapCache[] = new Entity[Simulation.MAP_WIDTH*Simulation.MAP_HEIGHT];
  
  public int b_rounds;
  public FastArray<CannonBall> b_cannonballs = new FastArray<>(CannonBall.class, 100);
  public FastArray<Mine> b_mines = new FastArray<>(Mine.class, 100);
  public FastArray<Barrel> b_barrels = new FastArray<>(Barrel.class, 100);
  public FastArray<Ship> b_ships = new FastArray<>(Ship.class, 6);
  public Entity b_mapCache[] = new Entity[Simulation.MAP_WIDTH*Simulation.MAP_HEIGHT];

  public void debugOutput() {
    System.err.println("canonballs: "+cannonballs.size());
    System.err.println("mines: "+mines.size());
    System.err.println("barrels: "+barrels.size());
    System.err.println("ships: "+ships.size());
  }
  public void backup() {
    b_rounds = rounds;
    
    teams.forEach(Team::backup);

    b_cannonballs.copyFrom(cannonballs);
    for (int i=0;i<cannonballs.FE;i++) {
      cannonballs.elements[i].backup();
    }

    b_mines.copyFrom(mines);
    for (int i=0;i<mines.FE;i++) {
      mines.elements[i].backup();
    }

    b_barrels.copyFrom(barrels);
    for (int i=0;i<barrels.FE;i++) {
      barrels.elements[i].backup();
    }
    
    b_ships.copyFrom(ships);
    for (int i=0;i<ships.FE;i++) {
      ships.elements[i].backup();
    }

    // before backuping the state, we update the map (hopefully, we won't backup to much ^^)
    createMapCache();
    System.arraycopy(mapCache, 0, b_mapCache, 0, Simulation.MAP_WIDTH*Simulation.MAP_HEIGHT);
  }
  private void createMapCache() {
    System.arraycopy(mapEmptyCache, 0, mapCache, 0, Simulation.MAP_WIDTH*Simulation.MAP_HEIGHT);
    for (int i=0;i<mines.FE;i++) {
      Mine mine = mines.elements[i];
      this.setEntityAt(mine.position, mine);
    }
    for (int i=0;i<barrels.FE;i++) {
      Barrel barrel = barrels.elements[i];
      this.setEntityAt(barrel.position, barrel);
    }
  }

  public void restore() {
    rounds = b_rounds;

    teams.forEach(Team::restore);

    cannonballs.copyFrom(b_cannonballs);
    for (int i=0;i<cannonballs.FE;i++) {
      cannonballs.elements[i].restore();
    }

    barrels.copyFrom(b_barrels);
    for (int i=0;i<barrels.FE;i++) {
      barrels.elements[i].restore();
    }
    
    mines.copyFrom(b_mines);
    for (int i=0;i<mines.FE;i++) {
      mines.elements[i].restore();
    }

    ships.copyFrom(b_ships);
    for (int i=0;i<ships.FE;i++) {
      ships.elements[i].restore();
    }
    System.arraycopy(b_mapCache, 0, mapCache, 0, Simulation.MAP_WIDTH*Simulation.MAP_HEIGHT);
  }

  public void initRound() {
    // kill all ships ! (will be revive in the update process)
    teams.get(0).shipsAlive.clear();
    teams.get(1).shipsAlive.clear();

    ships.clear();
    cannonballs.clear();
    mines.clear(); //TODO handle fog of war
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

  Ship getShip(FastArray<Ship> fromShips, int entityId) {
    for (int i=0;i<fromShips.FE;i++) {
      Ship ship = fromShips.elements[i];
      if (ship.id == entityId) {
        return ship;
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
    for (int i=0;i<barrels.FE;i++) {
      Barrel barrel = barrels.elements[i];
      int dist = Coord.distanceCache[ship.position.x+10 + (ship.position.y+10)*50][barrel.position.x+10 + (barrel.position.y+10)*50];
      if (dist < bestDist) {
        bestDist = dist;
        best = barrel;
      }
    }
    return best;
  }
  public int getClosestBarrelDistance(Ship ship) {
    int bestDist = Integer.MAX_VALUE;
    for (int i=0;i<barrels.FE;i++) {
      Barrel barrel = barrels.elements[i];
      int dist = Coord.distanceCache[ship.position.x+10 + (ship.position.y+10)*50][barrel.position.x+10 + (barrel.position.y+10)*50];
      if (dist < bestDist) {
        bestDist = dist;
      }
    }
    return bestDist;
  }

  
  public Ship getClosestEnnemy(Ship me) {
    int bestDist = Integer.MAX_VALUE;
    Ship best = null;
    for (int i=0;i<ships.FE;i++) {
      Ship other = ships.elements[i];
      if (other.owner == me.owner || other.health <= 0) continue;
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
    
    for (int b=0;b<barrels.FE;b++) {
      Barrel barrel = barrels.elements[b];
      Ship closest = null;
      int best = Integer.MAX_VALUE;
      for (int s=0;s<ships.size();s++) {
        Ship ship = ships.elements[s];
        if (ship.health <= 0) continue;
        int dist = Coord.distanceCache[ship.position.x+10 + (ship.position.y+10)*50][barrel.position.x+10 + (barrel.position.y+10)*50];
        //int dist = ship.position.distanceTo(barrel.position);
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
    if (!coord.isInsideMap()) return;
    mapCache[coord.x+coord.y*Simulation.MAP_WIDTH] = entity;
  }

}
