package cotc.game;

import java.util.Map;
import java.util.Map.Entry;

import cotc.GameState;
import cotc.Team;
import cotc.ai.AISolution;
import cotc.ai.ag.AGAction;
import cotc.ai.ag.AGSolution;
import cotc.ai.ag.ShipActions;
import cotc.entities.Barrel;
import cotc.entities.CannonBall;
import cotc.entities.Entity;
import cotc.entities.EntityType;
import cotc.entities.Mine;
import cotc.entities.Ship;
import cotc.utils.Coord;
import cotc.utils.FastArray;

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

  public static final Coord COORD_ZERO = Coord.get(0, 0);

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

  FastArray<Coord> cannonBallExplosions = new FastArray<>(Coord.class, 100);
  FastArray<Ship> collisions = new FastArray<>(Ship.class, 20);

  public Simulation(GameState state) {
    this.state = state;
  }

  public void simulateNew(AGSolution sol) {
    coreSimulation(sol, sol.getActionsNew());
    state.restore();
  }

  /* without backup  / restore */
  public void coreSimulation(AISolution sol, ShipActions[] actions) {
    sol.resetEnergy();
    for (int i = 0; i < AGSolution.DEPTH; i++) {
      if( state.teams[0].dead || state.teams[1].dead) break;
      ShipActions sActions = actions[0];
      applyActions(i, actions);
      playOneTurn();
      state.rounds ++;
      if (state.rounds == 200) break;
      
      sol.updateEnergyTurn(i, state);
    }
    if (state.rounds == 200) {
      checkEndConditions();
    }
    if (state.teams[0].dead) {
      sol.setEnergy(-1_000_000);
      return;
    }

    sol.updateEnergyEnd(state);
  }

  public void simulate(AISolution sol) {
    Map<Ship, AGAction[]> actions = sol.getActions();
    coreSimulation(sol, actions);
    state.restore();
  }

  /* without backup  / restore */
  public void coreSimulation(AISolution sol, Map<Ship, AGAction[]> actions) {
    sol.resetEnergy();
    for (int i = 0; i < AGSolution.DEPTH; i++) {
      if( state.teams[0].dead || state.teams[1].dead) break;
      
      applyActions(i, actions);
      playOneTurn();
      state.rounds ++;
      if (state.rounds == 200) break;
      
      sol.updateEnergyTurn(i, state);
    }
    if (state.rounds == 200) {
      checkEndConditions();
    }
    if (state.teams[0].dead) {
      sol.setEnergy(-1_000_000);
      return;
    }

    sol.updateEnergyEnd(state);
  }

  private void applyActions(int i, Map<Ship, AGAction[]> actions) {
    for (Entry<Ship, AGAction[]> entry : actions.entrySet()) {
      AGAction action = entry.getValue()[i];
      entry.getKey().action = action.action;
      entry.getKey().target = action.target;
    }
  }

  private void checkEndConditions() {
    if (state.teams[0].getScore() >= state.teams[1].getScore()) {
      state.teams[1].dead = true;
    } else {
      state.teams[0].dead = true;
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

    for (int i=0;i<state.ships.FE;i++) {
      Ship ship = state.ships.elements[i];

      if (ship.health <= 0) {
        int reward = Math.min(REWARD_RUM_BARREL_VALUE, ship.initialHealth);
        if (reward > 0) {
          Barrel barrel = new Barrel(0, ship.position.x, ship.position.y, reward);
          state.barrels.add(barrel);
          state.setEntityAt(ship.position, barrel);
        }
      }
    }
    for (int i=0;i<state.ships.FE;i++) {
      Ship ship = state.ships.elements[i];
      if (ship.health <= 0) {
        state.teams[ship.owner].shipsAlive.remove(ship);
        state.ships.removeAt(i);
        i--;
      }
    }
    
    for (Team team : state.teams) {
      if (team.shipsAlive.isEmpty()) {
        team.dead = true;
      }
    }
  }

  private void applyActions(int i, ShipActions[] actions) {
    for (int s=0;s<state.teams[0].ships.FE;s++) {
      Ship ship = state.teams[0].ships.elements[s];
      AGAction agAction = actions[i].actions[s];
      ship.action = agAction.action;
      ship.target = agAction.target;
    }
  }


  private void applyActions() {
    for (Team team : state.teams) {
      for (int s=0;s<team.shipsAlive.FE;s++) {
        Ship ship = team.shipsAlive.elements[s];
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
                  boolean cellIsFreeOfShips = true;
                  for (int i=0;i<state.ships.FE;i++) {
                    Ship b = state.ships.elements[i];
                    if (b == ship) continue;
                    cellIsFreeOfShips = cellIsFreeOfShips && !b.at(target);
                  }
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
    cannonBallExplosions.clear();
    for (int i=0;i<state.cannonballs.FE;i++) {
      CannonBall ball = state.cannonballs.elements[i];
      if (ball.remainingTurns == 0) {
        state.cannonballs.removeAt(i);
        i--; // we remove an object, so we have to get back one item
        continue;
      } else if (ball.remainingTurns == 1) {
        ball.remainingTurns = 0;
        cannonBallExplosions.add(ball.position);
      } else if (ball.remainingTurns > 0) {
        ball.remainingTurns--;
      }
    }
  }

  private void decrementRum() {
    for (int i=0;i<state.ships.FE;i++) {
      Ship ship = state.ships.elements[i];
      ship.damage(1);
    }
  }

  private void moveShips() {
    // ---
    // Go forward
    // ---
    for (int i = 1; i <= MAX_SHIP_SPEED; i++) {
      for (int s=0;s<state.ships.FE;s++) {
        Ship ship = state.ships.elements[s];
        if (ship.health <=0) continue;
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
        collisions.clear();

        for (int s=0;s<state.ships.FE;s++) {
          Ship ship = state.ships.elements[s];
          for (int s2=0;s2<state.ships.FE;s2++) {
            if (s == s2) continue;
            Ship other = state.ships.elements[s2];
            if (ship.newBowIntersect(other)) {
              collisions.add(ship);
            }
          }
        }

        for (int s=0;s<collisions.FE;s++) {
          Ship ship = collisions.elements[s];
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
      for (int s=0;s<state.ships.FE;s++) {
        Ship ship = state.ships.elements[s];
        if (ship.health <=0) continue;
          ship.position = ship.newPosition;
      }
      for (int s=0;s<state.ships.FE;s++) {
        Ship ship = state.ships.elements[s];
        if (ship.health <=0) continue;
          doCollisionWithMinesAndBarrels(ship, ship.bow()); // only bow can collide
      }
    }
  }
  private void rotateShips() {
    // Rotate
    for (int s=0;s<state.ships.FE;s++) {
      Ship ship = state.ships.elements[s];
      if (ship.health <=0) continue;
        ship.newPosition = ship.position;
        ship.newBowCoordinate = ship.newBow();
        ship.newSternCoordinate = ship.newStern();
    }

    // Check collisions
    boolean collisionDetected = true;
    while (collisionDetected) {
      collisions.clear();
      collisionDetected = false;

      for (int s=0;s<state.ships.FE;s++) {
        Ship ship = state.ships.elements[s];
        if (ship.newPositionsIntersect(state.ships)) {
          collisions.add(ship);
        }
      }

      for (int i=0;i<collisions.FE;i++) {
        Ship ship = collisions.elements[i];
        ship.newOrientation = ship.orientation;
        ship.newBowCoordinate = ship.newBow();
        ship.newSternCoordinate = ship.newStern();
        ship.speed = 0;
        collisionDetected = true;
      }

    }

    // Apply rotation (1st move to newOrientation, then check collision)
    for (int s=0;s<state.ships.FE;s++) {
      Ship ship = state.ships.elements[s];
      if (ship.health <=0) continue;
        ship.orientation = ship.newOrientation;
    }
    for (int s=0;s<state.ships.FE;s++) {
      Ship ship = state.ships.elements[s];
      if (ship.health <=0) continue;
        doCollisionWithMinesAndBarrels(ship, ship.bow()); 
        doCollisionWithMinesAndBarrels(ship, ship.stern());
    }
  }
  
  private void updateInitialRum() {
    for (int s=0;s<state.ships.FE;s++) {
      Ship ship = state.ships.elements[s];
      ship.initialHealth = ship.health;
    }
  }

  void explodeShips() {
    
    for (int i=0;i<cannonBallExplosions.FE; i++) {
      Coord position = cannonBallExplosions.elements[i];
      for (int s=0;s<state.ships.FE;s++) {
        Ship ship = state.ships.elements[s];
        if (position == ship.bow() || position == ship.stern()) {
          ship.damage(Simulation.LOW_DAMAGE);
          cannonBallExplosions.removeAt(i);
          i--;
          break;
        } else if (position == ship.position) {
          ship.damage(Simulation.HIGH_DAMAGE);
          cannonBallExplosions.removeAt(i);
          i--;
          break;
        }
      }
    }
  }
  void explodeMinesAndBarrels() {
    for (int i=0;i<cannonBallExplosions.FE; i++) {
      Coord position = cannonBallExplosions.elements[i];
      Entity entityAtPosition = state.getEntityAt(position);
      if (entityAtPosition != null) {
        if (entityAtPosition.type == EntityType.MINE) {
          Mine mine = (Mine)entityAtPosition;
          mine.explode(state.ships, true);
          state.mines.remove(mine);
          state.clearEntityAt(mine.position);
          cannonBallExplosions.removeAt(i);
          i--;
        } else {
          Barrel barrel = (Barrel)entityAtPosition;
          state.barrels.remove(barrel);
          state.clearEntityAt(barrel.position);
          cannonBallExplosions.removeAt(i);
          i--;
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
}
