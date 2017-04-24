package cotc.game;

import java.util.Map;
import java.util.Map.Entry;

import cgcollections.arrays.FastArray;
import cotc.GameState;
import cotc.Team;
import cotc.ai.AISolution;
import cotc.ai.ag.AGAction;
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

  public static int travelTimeCache[];
  static {
    travelTimeCache = new int[60];
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

  private int currentDepth;

  public Simulation(GameState state) {
    this.state = state;
  }

  public void setState(GameState state) {
    this.state = state;
  }

  public void simulateNew(AGSolution sol) {
    coreSimulation(sol, sol.getActionsNew());
    state.restore();
  }

  /* without backup  / restore */
  public void coreSimulation(AISolution sol, FastArray<AGAction> actions) {
    sol.resetEnergy();
    for (int depth = 0; depth < AGSolution.DEPTH; depth++) {
      if( state.teams[0].dead || state.teams[1].dead) break;
      applyActions(depth, 0, actions);
      
      // TODO What the frack ???? j'ai une autre eval qui evite de passer derriere
      // bon je la laisse pour pas influer
      for (int s=0;s<state.teams[1].ships.length;s++) {
        Ship ship = state.teams[1].ships.elements[s];
        if (depth == 0) {
          ship.action = Action.MINE;
        } else {
          ship.action = Action.WAIT;
        }
      }
      playOneTurn();
      if (state.rounds == 200) break;
      
      sol.updateEnergyTurn(depth, state);
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
    for (currentDepth = 0; currentDepth < AGSolution.DEPTH; currentDepth++) {
      if( state.teams[0].dead || state.teams[1].dead) break;
      
      applyActions(currentDepth, actions);
      playOneTurn();
      state.rounds ++;
      if (state.rounds == 200) break;
      
      sol.updateEnergyTurn(currentDepth, state);
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
  private void applyActions(int depth, int teamId, FastArray<AGAction> actions) {
    for (int s=0;s<state.teams[teamId].ships.length;s++) {
      Ship ship = state.teams[teamId].ships.elements[s];
      ship.action = actions.elements[depth + s*AGSolution.DEPTH].action;
      ship.target = actions.elements[depth + s*AGSolution.DEPTH].target;
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

    for (int i=0;i<state.ships.length;i++) {
      Ship ship = state.ships.elements[i];

      if (ship.health <= 0) {
        // reward !
        int reward = Math.min(REWARD_RUM_BARREL_VALUE, ship.initialHealth);
        if (reward > 0) {
          Barrel barrel = new Barrel(0, ship.position.x, ship.position.y, reward);
          state.barrels.add(barrel);
          state.setEntityAt(ship.position, barrel);
        }
        // remove ships from lists
        state.teams[ship.owner].shipsAlive.remove(ship);
        state.ships.remove(i);
        i--;
      }
    }
    
    if (state.teams[0].shipsAlive.length == 0) state.teams[0].dead = true;
    if (state.teams[1].shipsAlive.length == 0) state.teams[1].dead = true;
    state.rounds ++;
    if (state.rounds >= 200) {
      checkEndConditions();
    }
  }

  private void applyActions() {
    for (Team team : state.teams) {
      for (int s=0;s<team.shipsAlive.length;s++) {
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
                if (ship.canDropBomb(state) ) {
                  Coord target = ship.stern().neighbor((ship.orientation + 3) % 6);
                  if (ship.canDropBomb(state)) {
                    ship.mineCooldown = Simulation.COOLDOWN_MINE;
                    Mine mine = new Mine(0, target.x, target.y);
                    state.droppedMines++;
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
                CannonBall cannonBall = new CannonBall(0, ship.target.x, ship.target.y, ship.id, ship.bow().x, ship.bow().y, travelTime);
                state.cannonballs.add(cannonBall);
                state.firedCannonballs++;
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
    for (int i=0;i<state.cannonballs.length;i++) {
      CannonBall ball = state.cannonballs.elements[i];
      if (ball.remainingTurns == 0) {
        state.cannonballs.remove(i);
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
    for (int i=0;i<state.ships.length;i++) {
      Ship ship = state.ships.elements[i];
      ship.damage(1);
    }
  }

  private void moveShips() {
    // ---
    // Go forward
    // ---
    // init new positions
    for (int s=0;s<state.ships.length;s++) {
      Ship ship = state.ships.elements[s];
      ship.newPosition = ship.position;
      ship.newBowCoordinate = ship.bow();
      ship.newSternCoordinate = ship.stern();
    }
    
    for (int i = 1; i <= MAX_SHIP_SPEED; i++) {
      for (int s = 0; s < state.ships.length; s++) {
        Ship ship = state.ships.elements[s];
        if (ship.health <= 0) continue;
        if (i > ship.speed)   continue;

        Coord bow = ship.bow();
        if (bow.isInsideMap()) {
          // Set new coordinate.
          ship.newPosition = bow;
          ship.newBowCoordinate = bow.neighbor(ship.orientation);
          ship.newSternCoordinate = ship.position;
        } else {
          // Stop ship!
          ship.speed = 0;
        }
      }

      // Check ship and obstacles collisions
      boolean collisionDetected = true;
      while (collisionDetected) {
        collisionDetected = false;
        collisions.clear();

        for (int s=0;s<state.ships.length;s++) {
          Ship ship = state.ships.elements[s];
          for (int s2=s+1;s2<state.ships.length;s2++) {
            if (s == s2) continue;
            Ship other = state.ships.elements[s2];
            
            if (ship.newBowIntersect(other)) {
              collisions.add(ship);
            }
            if (other.newBowIntersect(ship)) {
              collisions.add(other);
            }
          }
        }

        for (int s=0;s<collisions.length;s++) {
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
      for (int s=0;s<state.ships.length;s++) {
        Ship ship = state.ships.elements[s];
        if (ship.health <=0) continue;
        ship.position = ship.newPosition;
      }
      for (int s=0;s<state.ships.length;s++) {
        Ship ship = state.ships.elements[s];
        if (ship.health <=0) continue;
        doCollisionWithMinesAndBarrels(ship, ship.bow()); // only bow can collide
      }
    }
  }
  private void rotateShips() {
    // Rotate
    for (int s=0;s<state.ships.length;s++) {
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

      for (int s=0;s<state.ships.length;s++) {
        Ship ship = state.ships.elements[s];
        if (ship.newPositionsIntersect(state.ships)) {
          collisions.add(ship);
        }
      }

      for (int i=0;i<collisions.length;i++) {
        Ship ship = collisions.elements[i];
        ship.newOrientation = ship.orientation;
        ship.newBowCoordinate = ship.newBow();
        ship.newSternCoordinate = ship.newStern();
        ship.speed = 0;
        collisionDetected = true;
      }

    }

    // Apply rotation (1st move to newOrientation, then check collision)
    for (int s=0;s<state.ships.length;s++) {
      Ship ship = state.ships.elements[s];
      if (ship.health <=0) continue;
        ship.orientation = ship.newOrientation;
    }
    for (int s=0;s<state.ships.length;s++) {
      Ship ship = state.ships.elements[s];
      if (ship.health <=0) continue;
        doCollisionWithMinesAndBarrels(ship, ship.bow()); 
        doCollisionWithMinesAndBarrels(ship, ship.stern());
    }
  }
  
  private void updateInitialRum() {
    for (int s=0;s<state.ships.length;s++) {
      Ship ship = state.ships.elements[s];
      ship.initialHealth = ship.health;
    }
  }

  void explodeShips() {
    
    for (int i=0;i<cannonBallExplosions.length; i++) {
      Coord position = cannonBallExplosions.elements[i];
      for (int s=0;s<state.ships.length;s++) {
        Ship ship = state.ships.elements[s];
        if (position == ship.bow() || position == ship.stern()) {
          ship.damage(Simulation.LOW_DAMAGE);
          cannonBallExplosions.remove(i);
          i--;
          break;
        } else if (position == ship.position) {
          ship.damage(Simulation.HIGH_DAMAGE);
          cannonBallExplosions.remove(i);
          i--;
          break;
        }
      }
    }
  }
  void explodeMinesAndBarrels() {
    for (int i=0;i<cannonBallExplosions.length; i++) {
      Coord position = cannonBallExplosions.elements[i];
      Entity entityAtPosition = state.getEntityAt(position);
      if (entityAtPosition != null) {
        if (entityAtPosition.type == EntityType.MINE) {
          Mine mine = (Mine)entityAtPosition;
          mine.explode(state.ships, true);
          state.mines.remove(mine);
          state.clearEntityAt(mine.position);
          cannonBallExplosions.remove(i);
          i--;
        } else {
          Barrel barrel = (Barrel)entityAtPosition;
          state.barrels.remove(barrel);
          state.clearEntityAt(barrel.position);
          cannonBallExplosions.remove(i);
          i--;
          state.destroyedBarrels++;
        }
      }
    }
  }

  private void doCollisionWithMinesAndBarrels(Ship ship, Coord coord) {
    if (!coord.isInsideMap()) return;
    
    Entity entity = state.getEntityAt(coord);
    if (entity != null && entity.type != EntityType.SHIP) {
      ship.heal(entity.health);
      if (entity.explode(state.ships, false)) {
        if (entity.type == EntityType.BARREL) {
          state.barrels.remove(entity);
        } else {
          state.mines.remove(entity);
        }
        state.clearEntityAt(coord);
      }
    }
  }

  public void simulate() {
    // TODO Auto-generated method stub
    
  }
}
