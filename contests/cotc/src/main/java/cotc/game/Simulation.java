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
  
  static {
    MAX_SHIPS = 3;
    MIN_MINES = 5;
    MAX_MINES = 10;
    MAX_SHIP_SPEED = 2;
  }

  private List<Coord> cannonBallExplosions = new ArrayList<>();
  private List<Ship> shipLosts = new ArrayList<>();

  Ship[] collisions = new Ship[20];
  int collisionsFE = 0;

  public Simulation(GameState state) {
    this.state = state;
  }

  public void simulate(AGSolution sol) {
    Map<Ship, Action[]> actions = sol.actions;
    state.backup();

    coreSimulation(sol, actions);
    
    state.restore();
  }

  /* without backup  / restore */
  public void coreSimulation(AGSolution sol, Map<Ship, Action[]> actions) {
    double energy = 0;
    for (int i = 0; i < AGSolution.DEPTH; i++) {
      if( state.teams.get(0).dead || state.teams.get(1).dead) break;
      
      applyActions(i, actions);
      playOneTurn();
      state.rounds ++;
      if (state.rounds == 200) break;
      
//      if (i==0) {
//        sol.calculateFeature(state);
//        energy += sol.speedFeature;
//        //System.err.println(actions[0][0] + " -> "+sol.speedFeature+" ==> "+energy);
//      }
    }
    if (state.rounds == 200) {
      checkEndConditions();
    }
    
    sol.calculateFeature(state);
    energy += sol.myHealtFeature + sol.speedFeature;
    sol.energy = energy;
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

    applyActions();
    
    moveShips();
    rotateShips();

    explodeShips();
    explodeMines();
    explodeBarrels();

    for (Ship ship : shipLosts) {
      state.barrels.add(new Barrel(0, ship.position.x, ship.position.y, REWARD_RUM_BARREL_VALUE));
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
                  boolean cellIsFreeOfBarrels = state.barrels.stream().noneMatch(barrel -> barrel.position == target);
                  boolean cellIsFreeOfShips = state.ships.stream().filter(b -> b != ship).noneMatch(b -> b.at(target));

                  if (cellIsFreeOfBarrels && cellIsFreeOfShips) {
                    ship.mineCooldown = Simulation.COOLDOWN_MINE;
                    Mine mine = new Mine(0, target.x, target.y);
                    state.mines.add(mine);
                  }
                }

              }
              break;
            case FIRE:
              int distance = ship.bow().distanceTo(ship.target);
              if (ship.target.isInsideMap() && distance <= Simulation.FIRE_DISTANCE_MAX && ship.cannonCooldown == 0) {
                int travelTime = (int) (1 + Math.round(ship.bow().distanceTo(ship.target) / 3.0));
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
    shipLosts.clear();
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
      for (Team team : state.teams) {
        for (Ship ship : team.shipsAlive) {
          ship.newPosition = ship.position;
          ship.newBowCoordinate = ship.bow();
          ship.newSternCoordinate = ship.stern();
          if (i > ship.speed) {
            continue;
          }


          Coord newCoordinate = ship.position.neighbor(ship.orientation);

          if (newCoordinate.isInsideMap()) {
            // Set new coordinate.
            ship.newPosition = newCoordinate;
            ship.newBowCoordinate = newCoordinate.neighbor(ship.orientation);
            ship.newSternCoordinate = newCoordinate.neighbor((ship.orientation + 3) % 6);
          } else {
            // Stop ship!
            ship.speed = 0;
          }
        }
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

          // Stop ships
          ship.speed = 0;

          collisionDetected = true;
        }
      }

      for (Team team : state.teams) {
        for (Ship ship : team.shipsAlive) {
          ship.position = ship.newPosition;
          if (checkCollisions(ship)) {
            shipLosts.add(ship);
          }
        }
      }
    }
  }
  private void rotateShips() {
    // Rotate
    for (Team team : state.teams) {
      for (Ship ship : team.shipsAlive) {
        ship.newPosition = ship.position;
        ship.newBowCoordinate = ship.newBow();
        ship.newSternCoordinate = ship.newStern();
      }
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

    // Apply rotation
    for (Team team : state.teams) {
      for (Ship ship : team.shipsAlive) {
        if (ship.health == 0) {
          continue;
        }

        ship.orientation = ship.newOrientation;
        if (checkCollisions(ship)) {
          shipLosts.add(ship);
        }
      }
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
  void explodeMines() {
    for (Iterator<Coord> itBall = cannonBallExplosions.iterator(); itBall.hasNext();) {
      Coord position = itBall.next();
      for (Iterator<Mine> it = state.mines.iterator(); it.hasNext();) {
        Mine mine = it.next();
        if (mine.position == position) {
          it.remove();
          itBall.remove();
          break;
        }
      }
    }
  }

  void explodeBarrels() {
    for (Iterator<Coord> itBall = cannonBallExplosions.iterator(); itBall.hasNext();) {
      Coord position = itBall.next();
      for (Iterator<Barrel> it = state.barrels.iterator(); it.hasNext();) {
        Barrel barrel = it.next();
        if (barrel.position == position) {
          it.remove();
          itBall.remove();
          break;
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
      if (barrel.position == bow || barrel.position == stern || barrel.position == center) {
        ship.heal(barrel.health);
        it.remove();
      }
    }

    // Collision with the mines
    for (Iterator<Mine> it = state.mines.iterator(); it.hasNext();) {
      Mine mine = it.next();
      
      if (mine.explode(state.ships, false)) {
        it.remove();
      }
    }

    return ship.health <= 0;
  }
}
