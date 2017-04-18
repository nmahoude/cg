package cotc.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cotc.GameState;
import cotc.Team;
import cotc.ai.ag.AGSolution;
import cotc.entities.Action;
import cotc.entities.Barrel;
import cotc.entities.CannonBall;
import cotc.entities.Entity;
import cotc.entities.EntityType;
import cotc.entities.Mine;
import cotc.entities.Ship;
import cotc.utils.Coord;

public class Simulation {
  private GameState state;

  public static final int MAP_WIDTH = 23;
  public static final int MAP_HEIGHT = 21;
  public static final int MIN_SHIPS = 1;
  public static final int MAX_SHIPS;
  public static final int MIN_MINES;
  public static final int MAX_MINES;
  public static final int MIN_RUM_BARRELS = 10;
  public static final int MAX_RUM_BARRELS = 26;
  public static final int MIN_RUM_BARREL_VALUE = 10;
  public static final int MAX_RUM_BARREL_VALUE = 20;
  public static final int REWARD_RUM_BARREL_VALUE = 30;
  public static final int MAX_SHIP_SPEED;
  public static final int COOLDOWN_CANNON = 2;
  public static final int COOLDOWN_MINE = 5;
  public static final int FIRE_DISTANCE_MAX = 10;
  public static final int LOW_DAMAGE = 25;
  public static final int HIGH_DAMAGE = 50;
  public static final int NEAR_MINE_DAMAGE = 10;
  public static final int MINE_VISIBILITY_RANGE = 5;
  public static final Coord MAP_CENTER = Coord.get(11, 10);

  private static int travelTimeCache[];
  static {
    travelTimeCache = new int[30];
    for (int dist=0;dist<travelTimeCache.length;dist++) {
      travelTimeCache[dist] = (int) (1 + Math.round(dist / 3.0));
    }
  }
  static {
    MAX_SHIPS = 3;
    MIN_MINES = 5;
    MAX_MINES = 10;
    MAX_SHIP_SPEED = 2;
  }

  private List<Coord> cannonBallExplosions = new ArrayList<>();

  Ship[] collisions = new Ship[20];
  int collisionsFE = 0;

  public Simulation(GameState state) {
    this.state = state;
  }

  public void simulate(AGSolution sol) {
    Map<Ship, Action[]> actions = sol.actions;

    coreSimulation(sol, actions);
    state.restore();
  }

  /* without backup  / restore */
  public void coreSimulation(AGSolution sol, Map<Ship, Action[]> actions) {
    sol.energy = 0;
    for (int i = 0; i < AGSolution.DEPTH; i++) {
      if( state.teams.get(0).dead || state.teams.get(1).dead) break;
      
      applyActions(i, actions);
      playOneTurn();
      state.rounds ++;
      if (state.rounds == 200) break;
      
      if (i == 0) {
        sol.updateEnergyTurn1(state);
      }
    }
    if (state.rounds == 200) {
      checkEndConditions();
    }
    if (state.teams.get(0).dead) {
      sol.energy = -1_000_000;
      return;
    }

    sol.updateEnergy(state);
  }

  private void applyActions(int i, Map<Ship, Action[]> actions) {
    for (Entry<Ship, Action[]> entry : actions.entrySet()) {
      Action action = entry.getValue()[i];
      entry.getKey().action = action;
    }
  }

  private void checkEndConditions() {
    if (state.teams.get(0).getScore() >= state.teams.get(1).getScore()) {
      state.teams.get(1).dead = true;
    } else {
      state.teams.get(0).dead = true;
    }
  }

  public void playOneTurn() {
    reinitSimulation();

    moveCannonballs();
    decrementRum();
    updateInitialRum();
    
    applyActions();
    
    moveShips();
    rotateShips();

    explodeShips();
    explodeMinesAndBarrels();

    for (Ship ship : state.ships) {
      if (ship.health <= 0) {
        int reward = Math.min(REWARD_RUM_BARREL_VALUE, ship.initialHealth);
        if (reward > 0) {
          Barrel barrel = new Barrel(0, ship.position.x, ship.position.y, reward);
          state.barrels.add(barrel);
          state.setEntityAt(ship.position, barrel);
        }
      }
    }
    for (Iterator<Ship> it = state.ships.iterator(); it.hasNext();) {
      Ship ship = it.next();
      if (ship.health <= 0) {
        state.teams.get(ship.owner).shipsAlive.remove(ship);
        it.remove();
      }
    }
    
    for (Team team : state.teams) {
      if (team.shipsAlive.isEmpty()) {
        team.dead = true;
      }
    }
  }

  private void applyActions() {
    for (Team team : state.teams) {
      for (Ship ship : team.shipsAlive) {
        if (ship.mineCooldown > 0) {
          ship.mineCooldown--;
        }
        if (ship.cannonCooldown > 0) {
          ship.cannonCooldown--;
        }

        ship.newOrientation = ship.orientation;

        if (ship.action != null) {
          switch (ship.action) {
            case FASTER:
              if (ship.speed < Simulation.MAX_SHIP_SPEED) {
                ship.speed++;
              }
              break;
            case SLOWER:
              if (ship.speed > 0) {
                ship.speed--;
              }
              break;
            case WAIT:
              // nothing
              break;
            case PORT:
              ship.newOrientation = (ship.orientation + 1) % 6;
              break;
            case STARBOARD:
              ship.newOrientation = (ship.orientation + 5) % 6;
              break;
            case MINE:
              if (ship.mineCooldown == 0) {
                Coord target = ship.stern().neighbor((ship.orientation + 3) % 6);

                if (target.isInsideMap()) {
                  boolean cellIsFreeOfShips = state.ships.stream().filter(b -> b != ship).noneMatch(b -> b.at(target));
                  boolean cellIsFreeOfMinesAndBarrels = state.getEntityAt(target) == null;
                  
                  if (cellIsFreeOfMinesAndBarrels && cellIsFreeOfShips ) {
                    ship.mineCooldown = Simulation.COOLDOWN_MINE;
                    Mine mine = new Mine(0, target.x, target.y);
                    state.mines.add(mine);
                    state.setEntityAt(target, mine);
                  }
                }

              }
              break;
            case FIRE:
              int distance = ship.bow().distanceTo(ship.target);
              if (ship.target.isInsideMap() && distance <= Simulation.FIRE_DISTANCE_MAX && ship.cannonCooldown == 0) {
                int travelTime = travelTimeCache[ship.bow().distanceTo(ship.target)];
                state.cannonballs.add(new CannonBall(0, ship.target.x, ship.target.y, ship.id, ship.bow().x, ship.bow().y, travelTime));
                ship.cannonCooldown = Simulation.COOLDOWN_CANNON;
              }
              break;
            default:
              break;
          }
        }
      }
    }
  }

  private void reinitSimulation() {
    cannonBallExplosions.clear();
  }

  private void moveCannonballs() {
    for (Iterator<CannonBall> it = state.cannonballs.iterator(); it.hasNext();) {
      CannonBall ball = it.next();
      if (ball.remainingTurns == 0) {
        it.remove();
        continue;
      } else if (ball.remainingTurns > 0) {
        ball.remainingTurns--;
      }

      if (ball.remainingTurns == 0) {
        cannonBallExplosions.add(ball.position);
      }
    }
  }

  private void decrementRum() {
    for (Ship ship : state.ships) {
      ship.damage(1);
    }
  }

  private void moveShips() {
    // ---
    // Go forward
    // ---
    for (int i = 1; i <= MAX_SHIP_SPEED; i++) {
      for (Ship ship : state.ships) {
        if (ship.health <=0) continue;
      //for (Team team : state.teams) {
        //for (Ship ship : team.shipsAlive) {
          Coord bow = ship.bow();
          Coord stern = ship.stern();

          ship.newPosition = ship.position;
          ship.newBowCoordinate = bow;
          ship.newSternCoordinate = stern;
          
          if (i > ship.speed) {
            continue;
          }

          if (bow.isInsideMap()) {
            // Set new coordinate.
            ship.newPosition = bow;
            ship.newBowCoordinate = bow.neighbor(ship.orientation);
            ship.newSternCoordinate = ship.position;
          } else {
            // Stop ship!
            ship.speed = 0;
          }
        //}
      }

      // Check ship and obstacles collisions
      boolean collisionDetected = true;
      while (collisionDetected) {
        collisionDetected = false;
        collisionsFE = 0;

        for (Ship ship : state.ships) {
          if (ship.newBowIntersect(state.ships)) {
            collisions[collisionsFE++] = ship;
          }
        }

        for (int s=0;s<collisionsFE;s++) {
          Ship ship = collisions[s];
          // Revert last move
          ship.newPosition = ship.position;
          ship.newBowCoordinate = ship.bow();
          ship.newSternCoordinate = ship.stern();

          // Stop ship
          ship.speed = 0;

          collisionDetected = true;
        }
      }

      // 1st move all ships, then doCollision
      for (Ship ship : state.ships) {
        if (ship.health <=0) continue;
          ship.position = ship.newPosition;
      }
      for (Ship ship : state.ships) {
        if (ship.health <=0) continue;
          doCollisionWithMinesAndBarrels(ship, ship.bow()); // only bow can collide
      }
    }
  }
  private void rotateShips() {
    // Rotate
    for (Ship ship : state.ships) {
      if (ship.health <=0) continue;
        ship.newPosition = ship.position;
        ship.newBowCoordinate = ship.newBow();
        ship.newSternCoordinate = ship.newStern();
    }

    // Check collisions
    boolean collisionDetected = true;

    while (collisionDetected) {
      collisionsFE = 0;
      collisionDetected = false;

      for (Ship ship : state.ships) {
        if (ship.newPositionsIntersect(state.ships)) {
          collisions[collisionsFE++] = ship;
        }
      }

      for (int i=0;i<collisionsFE;i++) {
        Ship ship = collisions[i];
        ship.newOrientation = ship.orientation;
        ship.newBowCoordinate = ship.newBow();
        ship.newSternCoordinate = ship.newStern();
        ship.speed = 0;
        collisionDetected = true;
      }

    }

    // Apply rotation (1st move to newOrientation, then check collision)
    for (Ship ship : state.ships) {
      if (ship.health <=0) continue;
        ship.orientation = ship.newOrientation;
    }
    for (Ship ship : state.ships) {
      if (ship.health <=0) continue;
        doCollisionWithMinesAndBarrels(ship, ship.bow()); 
        doCollisionWithMinesAndBarrels(ship, ship.stern());
    }
  }
  
  private void updateInitialRum() {
    for (Ship ship : state.ships) {
      ship.initialHealth = ship.health;
    }
  }

  void explodeShips() {
    for (Iterator<Coord> it = cannonBallExplosions.iterator(); it.hasNext();) {
      Coord position = it.next();
      for (Ship ship : state.ships) {
        if (position == ship.bow() || position == ship.stern()) {
          ship.damage(Simulation.LOW_DAMAGE);
          it.remove();
          break;
        } else if (position == ship.position) {
          ship.damage(Simulation.HIGH_DAMAGE);
          it.remove();
          break;
        }
      }
    }
  }
  void explodeMinesAndBarrels() {
    for (Iterator<Coord> itBall = cannonBallExplosions.iterator(); itBall.hasNext();) {
      Coord position = itBall.next();
      Entity entityAtPosition = state.getEntityAt(position);
      if (entityAtPosition != null) {
        if (entityAtPosition.type == EntityType.MINE) {
          Mine mine = (Mine)entityAtPosition;
          state.mines.remove(mine);
          state.clearEntityAt(mine.position);
          itBall.remove();
        } else {
          Barrel barrel = (Barrel)entityAtPosition;
          state.barrels.remove(barrel);
          state.clearEntityAt(barrel.position);
          itBall.remove();
        }
      }
    }
  }

  private void doCollisionWithMinesAndBarrels(Ship ship, Coord coord) {
    if (!coord.isInsideMap()) return;
    
    Entity entity = state.getEntityAt(coord);
    if (entity != null) {
      if (entity.type == EntityType.BARREL) {
        Barrel barrel = (Barrel)entity;
        ship.heal(barrel.health);
        state.clearEntityAt(coord);
        state.barrels.remove(barrel);
      } else { // MINE
        Mine mine = (Mine)entity;
        if (mine.explode(state.ships, false)) {
          state.mines.remove(mine);
          state.clearEntityAt(coord);
        }
      }
    }
  }
  
  private boolean checkCollisions(Ship ship) {
    Coord bow = ship.bow();
    Coord stern = ship.stern();
    Coord center = ship.position;

    // Collision with the barrels
    for (Iterator<Barrel> it = state.barrels.iterator(); it.hasNext();) {
      Barrel barrel = it.next();
      if (barrel.position == center || barrel.position == bow || barrel.position == stern) {
        ship.heal(barrel.health);
        it.remove();
        state.clearEntityAt(barrel.position);
      }
    }

    boolean mineExploded = false;
    // Collision with the mines
    for (Iterator<Mine> it = state.mines.iterator(); it.hasNext();) {
      Mine mine = it.next();

      //TODO check if <3 is sufficient to explode mine
      if (mine.position.distanceTo(ship.position) < 3) {
        if (mine.explode(state.ships, false)) {
          it.remove();
          state.clearEntityAt(mine.position);
          mineExploded = true;
        }
      }
    }
    return mineExploded;
  }
}
