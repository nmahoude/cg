package cotc.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cotc.GameState;
import cotc.Team;
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
  private List<Damage> damage = new ArrayList<>();

  public Simulation(GameState state) {
    this.state = state;
  }

  public void simulate() {
    state.backup();
    for (int i = 0; i < 10; i++) {
      playOneTurn();
      state.rounds ++;
      if (state.rounds == 200) break;
    }
    if (state.rounds == 200) {
      checkEndConditions();
    }
    
    // TODO  evaluate HERE !
    
    state.restore();
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

    for (Coord position : cannonBallExplosions) {
      damage.add(new Damage(position, 0, false));
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
                  boolean cellIsFreeOfBarrels = state.barrels.stream().noneMatch(barrel -> barrel.position.equals(target));
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
                int travelTime = 1 + Math.round(ship.bow().distanceTo(ship.target) / 3);
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
    damage.clear();
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
      List<Ship> collisions = new ArrayList<>();
      boolean collisionDetected = true;
      while (collisionDetected) {
        collisionDetected = false;

        for (Ship ship : state.ships) {
          if (ship.newBowIntersect(state.ships)) {
            collisions.add(ship);
          }
        }

        for (Ship ship : collisions) {
          // Revert last move
          ship.newPosition = ship.position;
          ship.newBowCoordinate = ship.bow();
          ship.newSternCoordinate = ship.stern();

          // Stop ships
          ship.speed = 0;

          collisionDetected = true;
        }
        collisions.clear();
      }

      for (Team team : state.teams) {
        for (Ship ship : team.shipsAlive) {
          if (ship.health == 0) {
            continue;
          }

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
    List<Ship> collisions = new ArrayList<>();
    while (collisionDetected) {
      collisionDetected = false;

      for (Ship ship : state.ships) {
        if (ship.newPositionsIntersect(state.ships)) {
          collisions.add(ship);
        }
      }

      for (Ship ship : collisions) {
        ship.newOrientation = ship.orientation;
        ship.newBowCoordinate = ship.newBow();
        ship.newSternCoordinate = ship.newStern();
        ship.speed = 0;
        collisionDetected = true;
      }

      collisions.clear();
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
        if (position.equals(ship.bow()) || position.equals(ship.stern())) {
          damage.add(new Damage(position, Simulation.LOW_DAMAGE, true));
          ship.damage(Simulation.LOW_DAMAGE);
          it.remove();
          break;
        } else if (position.equals(ship.position)) {
          damage.add(new Damage(position, Simulation.HIGH_DAMAGE, true));
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
        if (mine.position.equals(position)) {
          damage.addAll(mine.explode(state.ships, true));
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
        if (barrel.position.equals(position)) {
          damage.add(new Damage(position, 0, true));
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
      if (barrel.position.equals(bow) || barrel.position.equals(stern) || barrel.position.equals(center)) {
        ship.heal(barrel.health);
        it.remove();
      }
    }

    // Collision with the mines
    for (Iterator<Mine> it = state.mines.iterator(); it.hasNext();) {
      Mine mine = it.next();
      List<Damage> mineDamage = mine.explode(state.ships, false);

      if (!mineDamage.isEmpty()) {
        damage.addAll(mineDamage);
        it.remove();
      }
    }

    return ship.health <= 0;
  }
}
