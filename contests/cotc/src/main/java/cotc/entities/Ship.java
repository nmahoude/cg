package cotc.entities;

import java.util.List;

import cgcollections.arrays.FastArray;
import cotc.GameState;
import cotc.utils.Coord;

public class Ship extends Entity {
  public static final int MAX_SHIP_HEALTH = 100;

  public Action action; // TODO why need this ?
  
  public final int owner;
  public int orientation;
  public int speed;
  public int cannonCooldown;
  public int mineCooldown;

  private int b_orientation;
  private int b_speed;
  public int b_health;
  private int b_cannonCooldown;
  public int b_mineCooldown;

  // temporary values before collisions / fire ball, ..
  public int newOrientation;
  public Coord newPosition;
  public Coord newBowCoordinate;
  public Coord newSternCoordinate;
  public Coord target;
  public boolean champion;
  
  // work variable used in simulation. Not clean
  public int initialHealth;

  public Ship(int entityId, int x, int y, int orientation, int owner) {
    super(EntityType.SHIP, entityId, x, y);
    this.orientation = orientation;
    this.speed = 0;
    this.health = MAX_SHIP_HEALTH;
    this.owner = owner;
  }

  public void update(int x, int y, int orientation, int speed, int health, int owner) {
    super.update(x, y);
    this.orientation = orientation;
    this.speed = speed;
    this.health = health;
    
    if (cannonCooldown > 0) cannonCooldown--;
    if (mineCooldown > 0) mineCooldown--;
  }


  public void backup() {
    super.backup();

    b_orientation = orientation;
    b_speed = speed;
    b_health = health;
    b_cannonCooldown = cannonCooldown;
    b_mineCooldown = mineCooldown;
  }

  public void restore() {
    super.restore();

    orientation = b_orientation;
    speed = b_speed;
    health = b_health;
    cannonCooldown = b_cannonCooldown;
    mineCooldown = b_mineCooldown;
  }

  public void faster() {
    this.action = Action.FASTER;
  }

  public void slower() {
    this.action = Action.SLOWER;
  }

  public void port() {
    this.action = Action.PORT;
  }

  public void starboard() {
    this.action = Action.STARBOARD;
  }

  public void placeMine() {
    this.action = Action.MINE;
  }

  public Coord stern() {
    return position.neighborsCache[((orientation + 3) % 6)];
  }

  public Coord bow() {
    return position.neighborsCache[orientation];
  }

  public Coord newStern() {
    return position.neighborsCache[(newOrientation + 3) % 6];
  }

  public Coord newBow() {
    return position.neighborsCache[(newOrientation)];
  }

  public boolean at(Coord coord) {
    return stern() == coord || bow() == coord || position == coord;
  }

  public boolean newBowIntersect(Ship other) {
    return newBowCoordinate == other.newBowCoordinate
        || newBowCoordinate == other.newPosition
        || newBowCoordinate == other.newSternCoordinate;
  }

  public boolean newBowIntersect(List<Ship> ships) {
    for (Ship other : ships) {
      if (this != other && newBowIntersect(other)) {
        return true;
      }
    }
    return false;
  }

  public boolean newPositionsIntersect(Ship other) {
    boolean sternCollision = newSternCoordinate != null && 
        (  newSternCoordinate == other.newBowCoordinate
        || newSternCoordinate == other.newPosition
        || newSternCoordinate == other.newSternCoordinate);
    boolean centerCollision = newPosition != null && (
           newPosition == other.newBowCoordinate 
        || newPosition == other.newPosition
        || newPosition == other.newSternCoordinate);
    return newBowIntersect(other) || sternCollision || centerCollision;
  }

  public boolean newPositionsIntersect(List<Ship> ships) {
    for (Ship other : ships) {
      if (this != other && newPositionsIntersect(other)) {
        return true;
      }
    }
    return false;
  }

  public boolean newPositionsIntersect(FastArray<Ship> ships) {
    for (int i=0;i<ships.length;i++) {
      Ship other = ships.elements[i];
      if (this != other && newPositionsIntersect(other)) {
        return true;
      }
    }
    return false;
  }

  public void damage(int health) {
    this.health -= health;
    if (this.health <= 0) {
      this.health = 0;
    }
  }

  public void heal(int health) {
    this.health = Math.min(this.health + health, MAX_SHIP_HEALTH);
  }

  public void fire(int x, int y) {
    Coord target = Coord.get(x, y);
    this.target = target;
    this.action = Action.FIRE;
  }
  
  public String toPlayerString(int playerIdx) {
    return toPlayerString(orientation, speed, health, owner == playerIdx ? 1 : 0);
  }

  public boolean canDropBomb(GameState state) {
  Coord target = stern().neighbor((orientation + 3) % 6);

  if (target.isInsideMap()) {
    boolean cellIsFreeOfShips = true;
    for (int i = 0; i < state.ships.length; i++) {
      Ship b = state.ships.elements[i];
      if (b == this)
        continue;
      cellIsFreeOfShips = cellIsFreeOfShips && !b.at(target);
    }
    boolean cellIsFreeOfMinesAndBarrels = state.getEntityAt(target) == null;
    return cellIsFreeOfMinesAndBarrels && cellIsFreeOfShips;
  }
  return false;
}
}
