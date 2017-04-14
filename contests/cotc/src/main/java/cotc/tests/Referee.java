package cotc.tests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cotc.Team;
import cotc.entities.Barrel;
import cotc.entities.CannonBall;
import cotc.entities.Mine;
import cotc.entities.Ship;
import cotc.game.Damage;
import cotc.utils.Coord;
import cotc.utils.Util;

public class Referee {
  private static final Pattern PLAYER_INPUT_MOVE_PATTERN = Pattern.compile("MOVE (?<x>[0-9]{1,8})\\s+(?<y>[0-9]{1,8})(?:\\s+(?<message>.+))?",
      Pattern.CASE_INSENSITIVE);
  private static final Pattern PLAYER_INPUT_SLOWER_PATTERN = Pattern.compile("SLOWER(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
  private static final Pattern PLAYER_INPUT_FASTER_PATTERN = Pattern.compile("FASTER(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
  private static final Pattern PLAYER_INPUT_WAIT_PATTERN = Pattern.compile("WAIT(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
  private static final Pattern PLAYER_INPUT_PORT_PATTERN = Pattern.compile("PORT(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
  private static final Pattern PLAYER_INPUT_STARBOARD_PATTERN = Pattern.compile("STARBOARD(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
  private static final Pattern PLAYER_INPUT_FIRE_PATTERN = Pattern.compile("FIRE (?<x>[0-9]{1,8})\\s+(?<y>[0-9]{1,8})(?:\\s+(?<message>.+))?",
      Pattern.CASE_INSENSITIVE);
  private static final Pattern PLAYER_INPUT_MINE_PATTERN = Pattern.compile("MINE(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);

  private static final int MAP_WIDTH = 23;
  private static final int MAP_HEIGHT = 21;
  private static final int MIN_SHIPS = 1;
  private static final int MAX_SHIPS;
  private static final int MIN_MINES;
  private static final int MAX_MINES;
  private static final int MIN_RUM_BARRELS = 10;
  private static final int MAX_RUM_BARRELS = 26;
  private static final int MIN_RUM_BARREL_VALUE = 10;
  private static final int MAX_RUM_BARREL_VALUE = 20;
  private static final int REWARD_RUM_BARREL_VALUE = 30;
  private static final int MAX_SHIP_SPEED;
  private static final int COOLDOWN_CANNON = 2;
  private static final int COOLDOWN_MINE = 5;
  private static final int FIRE_DISTANCE_MAX = 10;
  private static final int LOW_DAMAGE = 25;
  private static final int HIGH_DAMAGE = 50;
  private static final int NEAR_MINE_DAMAGE = 10;
  private static final int MINE_VISIBILITY_RANGE = 5;

  static {
    MAX_SHIPS = 3;
    MIN_MINES = 5;
    MAX_MINES = 10;
    MAX_SHIP_SPEED = 2;
  }

  public static boolean debugoutput = false;

  int nextEntityId = 0;

  private long seed;
  private List<CannonBall> cannonballs;
  private List<Mine> mines;
  private List<Barrel> barrels;
  private List<Team> teams;
  private List<Ship> ships;
  private List<Damage> damage;
  private List<Ship> shipLosts;
  private List<Coord> cannonBallExplosions;
  int shipsPerPlayer;
  private int mineCount;
  private int barrelCount;
  static Random random;

  public void initReferee(int seed, int playerCount, Properties prop) throws Exception {
    random = new Random(this.seed);

    shipsPerPlayer = Util.clamp(
        Integer.valueOf(prop.getProperty("shipsPerPlayer", String.valueOf(random.nextInt(1 + MAX_SHIPS - MIN_SHIPS) + MIN_SHIPS))), MIN_SHIPS,
        MAX_SHIPS);

    mineCount = Util.clamp(Integer.valueOf(prop.getProperty("mineCount", String.valueOf(random.nextInt(MAX_MINES - MIN_MINES) + MIN_MINES))),
        MIN_MINES, MAX_MINES);

    barrelCount = Util.clamp(
        Integer.valueOf(prop.getProperty("barrelCount", String.valueOf(random.nextInt(MAX_RUM_BARRELS - MIN_RUM_BARRELS) + MIN_RUM_BARRELS))),
        MIN_RUM_BARRELS, MAX_RUM_BARRELS);

    cannonballs = new ArrayList<>();
    cannonBallExplosions = new ArrayList<>();
    damage = new ArrayList<>();
    shipLosts = new ArrayList<>();

    // Generate Players
    this.teams = new ArrayList<Team>(playerCount);
    for (int i = 0; i < playerCount; i++) {
      this.teams.add(new Team(i));
    }
    // Generate Ships
    for (int j = 0; j < shipsPerPlayer; j++) {
      int xMin = 1 + j * MAP_WIDTH / shipsPerPlayer;
      int xMax = (j + 1) * MAP_WIDTH / shipsPerPlayer - 2;

      int y = 1 + random.nextInt(MAP_HEIGHT / 2 - 2);
      int x = xMin + random.nextInt(1 + xMax - xMin);
      int orientation = random.nextInt(6);

      Ship ship0 = new Ship(nextEntityId++, x, y, orientation, 0);
      Ship ship1 = new Ship(nextEntityId++, x, MAP_HEIGHT - 1 - y, (6 - orientation) % 6, 1);

      this.teams.get(0).ships.add(ship0);
      this.teams.get(1).ships.add(ship1);
      this.teams.get(0).shipsAlive.add(ship0);
      this.teams.get(1).shipsAlive.add(ship1);
    }

    this.ships = teams.stream().map(p -> p.ships).flatMap(List::stream).collect(Collectors.toList());

    // Generate mines
    mines = new ArrayList<>();
    while (mines.size() < mineCount) {
      int x = 1 + random.nextInt(MAP_WIDTH - 2);
      int y = 1 + random.nextInt(MAP_HEIGHT / 2);

      Mine m = new Mine(nextEntityId++, x, y);
      boolean valid = true;
      for (Ship ship : this.ships) {
        if (ship.at(m.position)) {
          valid = false;
          break;
        }
      }
      if (valid) {
        if (y != MAP_HEIGHT - 1 - y) {
          mines.add(new Mine(nextEntityId++, x, MAP_HEIGHT - 1 - y));
        }
        mines.add(m);
      }
    }

    // Generate supplies
    barrels = new ArrayList<>();
    while (barrels.size() < barrelCount) {
      int x = 1 + random.nextInt(MAP_WIDTH - 2);
      int y = 1 + random.nextInt(MAP_HEIGHT / 2);
      int h = MIN_RUM_BARREL_VALUE + random.nextInt(1 + MAX_RUM_BARREL_VALUE - MIN_RUM_BARREL_VALUE);

      Barrel m = new Barrel(nextEntityId++, x, y, h);
      boolean valid = true;
      for (Ship ship : this.ships) {
        if (ship.at(m.position)) {
          valid = false;
          break;
        }
      }
      for (Mine mine : this.mines) {
        if (mine.position.equals(m.position)) {
          valid = false;
          break;
        }
      }
      if (valid) {
        if (y != MAP_HEIGHT - 1 - y) {
          barrels.add(new Barrel(nextEntityId++, x, MAP_HEIGHT - 1 - y, h));
        }
        barrels.add(m);
      }
    }
  }

  public void handlePlayerOutput(int frame, int round, int playerIdx, String[] outputs)
      throws WinException, LostException, InvalidInputException {
    Team team = this. teams.get(playerIdx);

    try {
      int i = 0;
      for (String line : outputs) {
        Matcher matchWait = PLAYER_INPUT_WAIT_PATTERN.matcher(line);
        Matcher matchMove = PLAYER_INPUT_MOVE_PATTERN.matcher(line);
        Matcher matchFaster = PLAYER_INPUT_FASTER_PATTERN.matcher(line);
        Matcher matchSlower = PLAYER_INPUT_SLOWER_PATTERN.matcher(line);
        Matcher matchPort = PLAYER_INPUT_PORT_PATTERN.matcher(line);
        Matcher matchStarboard = PLAYER_INPUT_STARBOARD_PATTERN.matcher(line);
        Matcher matchFire = PLAYER_INPUT_FIRE_PATTERN.matcher(line);
        Matcher matchMine = PLAYER_INPUT_MINE_PATTERN.matcher(line);
        Ship ship = team.shipsAlive.get(i++);

        if (matchMove.matches()) {
          throw new InvalidInputException("MOVE not implemented !", line);
        } else if (matchFaster.matches()) {
          ship.faster();
        } else if (matchSlower.matches()) {
          ship.slower();
        } else if (matchPort.matches()) {
          ship.port();
        } else if (matchStarboard.matches()) {
          ship.starboard();
        } else if (matchWait.matches()) {
          // do nothin, wait
        } else if (matchMine.matches()) {
          ship.placeMine();
        } else if (matchFire.matches()) {
          int x = Integer.parseInt(matchFire.group("x"));
          int y = Integer.parseInt(matchFire.group("y"));
          ship.fire(x, y);
        } else {
          throw new InvalidInputException("A valid action", line);
        }
      }
    } catch (InvalidInputException e) {
      team.setDead();
      throw e;
    }
  }

  public void updateGame(int round) throws GameOverException {
    moveCannonballs();
    decrementRum();

    applyActions();
    moveShips();
    rotateShips();

    explodeShips();
    explodeMines();
    explodeBarrels();

    for (Ship ship : shipLosts) {
      barrels.add(new Barrel(nextEntityId++, ship.position.x, ship.position.y, REWARD_RUM_BARREL_VALUE));
    }

    for (Coord position : cannonBallExplosions) {
      damage.add(new Damage(position, 0, false));
    }

    for (Iterator<Ship> it = ships.iterator(); it.hasNext();) {
      Ship ship = it.next();
      if (ship.health <= 0) {
        teams.get(ship.owner).shipsAlive.remove(ship);
        it.remove();
      }
    }

    if (gameIsOver()) {
      throw new GameOverException("endReached");
    }
  }

  private void decrementRum() {
    for (Ship ship : ships) {
      ship.damage(1);
    }
  }

  private void moveCannonballs() {
    for (Iterator<CannonBall> it = cannonballs.iterator(); it.hasNext();) {
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

  private void applyActions() {
    for (Team team : teams) {
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
              if (ship.speed < MAX_SHIP_SPEED) {
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
                  boolean cellIsFreeOfBarrels = barrels.stream().noneMatch(barrel -> barrel.position.equals(target));
                  boolean cellIsFreeOfShips = ships.stream().filter(b -> b != ship).noneMatch(b -> b.at(target));

                  if (cellIsFreeOfBarrels && cellIsFreeOfShips) {
                    ship.mineCooldown = COOLDOWN_MINE;
                    Mine mine = new Mine(nextEntityId++, target.x, target.y);
                    mines.add(mine);
                  }
                }

              }
              break;
            case FIRE:
              int distance = ship.bow().distanceTo(ship.target);
              if (ship.target.isInsideMap() && distance <= FIRE_DISTANCE_MAX && ship.cannonCooldown == 0) {
                int travelTime = 1 + Math.round(ship.bow().distanceTo(ship.target) / 3);
                cannonballs.add(new CannonBall(nextEntityId++, ship.target.x, ship.target.y, ship.id, ship.bow().x, ship.bow().y, travelTime));
                ship.cannonCooldown = COOLDOWN_CANNON;
              }
              break;
            default:
              break;
          }
        }
      }
    }
  }

  private boolean checkCollisions(Ship ship) {
    Coord bow = ship.bow();
    Coord stern = ship.stern();
    Coord center = ship.position;

    // Collision with the barrels
    for (Iterator<Barrel> it = barrels.iterator(); it.hasNext();) {
      Barrel barrel = it.next();
      if (barrel.position.equals(bow) || barrel.position.equals(stern) || barrel.position.equals(center)) {
        ship.heal(barrel.health);
        it.remove();
      }
    }

    // Collision with the mines
    for (Iterator<Mine> it = mines.iterator(); it.hasNext();) {
      Mine mine = it.next();
      List<Damage> mineDamage = mine.explode(ships, false);

      if (!mineDamage.isEmpty()) {
        damage.addAll(mineDamage);
        it.remove();
      }
    }

    return ship.health <= 0;
  }

  private void moveShips() {
    // ---
    // Go forward
    // ---
    for (int i = 1; i <= MAX_SHIP_SPEED; i++) {
      for (Team team : teams) {
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

        for (Ship ship : this.ships) {
          if (ship.newBowIntersect(ships)) {
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

      for (Team team : teams) {
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
    for (Team team : teams) {
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

      for (Ship ship : this.ships) {
        if (ship.newPositionsIntersect(ships)) {
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
    for (Team team : teams) {
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

  private boolean gameIsOver() {
    for (Team team : teams) {
      if (team.shipsAlive.isEmpty()) {
        return true;
      }
    }
    return false;
  }

  void explodeShips() {
    for (Iterator<Coord> it = cannonBallExplosions.iterator(); it.hasNext();) {
      Coord position = it.next();
      for (Ship ship : ships) {
        if (position.equals(ship.bow()) || position.equals(ship.stern())) {
          damage.add(new Damage(position, LOW_DAMAGE, true));
          ship.damage(LOW_DAMAGE);
          it.remove();
          break;
        } else if (position.equals(ship.position)) {
          damage.add(new Damage(position, HIGH_DAMAGE, true));
          ship.damage(HIGH_DAMAGE);
          it.remove();
          break;
        }
      }
    }
  }

  void explodeMines() {
    for (Iterator<Coord> itBall = cannonBallExplosions.iterator(); itBall.hasNext();) {
      Coord position = itBall.next();
      for (Iterator<Mine> it = mines.iterator(); it.hasNext();) {
        Mine mine = it.next();
        if (mine.position.equals(position)) {
          damage.addAll(mine.explode(ships, true));
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
      for (Iterator<Barrel> it = barrels.iterator(); it.hasNext();) {
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

  protected String[] getInputForPlayer(int round, int playerIdx) {
    List<String> data = new ArrayList<>();

    // Player's ships first
    for (Ship ship : teams.get(playerIdx).shipsAlive) {
        data.add(ship.toPlayerString(playerIdx));
    }

    // Number of ships
    data.add(0, String.valueOf(data.size()));

    // Opponent's ships
    for (Ship ship : teams.get((playerIdx + 1) % 2).shipsAlive) {
        data.add(ship.toPlayerString(playerIdx));
    }

    // Visible mines
    for (Mine mine : mines) {
        boolean visible = false;
        for (Ship ship : teams.get(playerIdx).ships) {
            if (ship.position.distanceTo(mine.position) <= MINE_VISIBILITY_RANGE) {
                visible = true;
                break;
            }
        }
        if (visible) {
            data.add(mine.toPlayerString(playerIdx));
        }
    }

    for (CannonBall ball : cannonballs) {
        data.add(ball.toPlayerString(playerIdx));
    }

    for (Barrel barrel : barrels) {
        data.add(barrel.toPlayerString(playerIdx));
    }

    data.add(1, String.valueOf(data.size() - 1));

    return data.toArray(new String[data.size()]);
}

  public int winner() {
    for (Team team : teams) {
      if (team.shipsAlive.isEmpty()) {
        return 1-team.id; // the other team won
      }
    }

    return teams.get(0).getScore() > teams.get(1).getScore() ? 0 : 1;
  }

}
