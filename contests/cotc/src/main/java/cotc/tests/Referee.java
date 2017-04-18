package cotc.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cotc.GameState;
import cotc.Team;
import cotc.entities.Barrel;
import cotc.entities.CannonBall;
import cotc.entities.Mine;
import cotc.entities.Ship;
import cotc.game.Simulation;
import cotc.utils.Util;

public class Referee {
  private static final Pattern PLAYER_INPUT_MOVE_PATTERN = Pattern.compile("MOVE (?<x>-?[0-9]{1,8})\\s+(?<y>-?[0-9]{1,8})(?:\\s+(?<message>.+))?",      Pattern.CASE_INSENSITIVE);
  private static final Pattern PLAYER_INPUT_SLOWER_PATTERN = Pattern.compile("SLOWER(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
  private static final Pattern PLAYER_INPUT_FASTER_PATTERN = Pattern.compile("FASTER(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
  private static final Pattern PLAYER_INPUT_WAIT_PATTERN = Pattern.compile("WAIT(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
  private static final Pattern PLAYER_INPUT_PORT_PATTERN = Pattern.compile("PORT(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
  private static final Pattern PLAYER_INPUT_STARBOARD_PATTERN = Pattern.compile("STARBOARD(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
  private static final Pattern PLAYER_INPUT_FIRE_PATTERN = Pattern.compile("FIRE (?<x>[0-9]{1,8})\\s+(?<y>[0-9]{1,8})(?:\\s+(?<message>.+))?",
      Pattern.CASE_INSENSITIVE);
  private static final Pattern PLAYER_INPUT_MINE_PATTERN = Pattern.compile("MINE(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);

  public static boolean debugoutput = false;

  int nextEntityId = 0;

  private long seed;
  
  int shipsPerPlayer;
  private int mineCount;
  private int barrelCount;
  static Random random;

  GameState state = new GameState(); // will know perfect informations
  Simulation simulation = new Simulation(state);
  
  public void initReferee(int seed, int playerCount, Properties prop) throws Exception {
    random = new Random(this.seed);
    
    shipsPerPlayer = Util.clamp(
        Integer.valueOf(prop.getProperty("shipsPerPlayer", String.valueOf(random.nextInt(1 + Simulation.MAX_SHIPS - Simulation.MIN_SHIPS) + Simulation.MIN_SHIPS))), Simulation.MIN_SHIPS,
        Simulation.MAX_SHIPS);

    mineCount = Util.clamp(Integer.valueOf(prop.getProperty("mineCount", String.valueOf(random.nextInt(Simulation.MAX_MINES - Simulation.MIN_MINES) + Simulation.MIN_MINES))),
        Simulation.MIN_MINES, Simulation.MAX_MINES);

    barrelCount = Util.clamp(
        Integer.valueOf(prop.getProperty("barrelCount", String.valueOf(random.nextInt(Simulation.MAX_RUM_BARRELS - Simulation.MIN_RUM_BARRELS) + Simulation.MIN_RUM_BARRELS))),
        Simulation.MIN_RUM_BARRELS, Simulation.MAX_RUM_BARRELS);

    // Generate Players
    for (int i = 0; i < playerCount; i++) {
      state.teams.add(new Team(i));
    }
    // Generate Ships
    for (int j = 0; j < shipsPerPlayer; j++) {
      int xMin = 1 + j * Simulation.MAP_WIDTH / shipsPerPlayer;
      int xMax = (j + 1) * Simulation.MAP_WIDTH / shipsPerPlayer - 2;

      int y = 1 + random.nextInt(Simulation.MAP_HEIGHT / 2 - 2);
      int x = xMin + random.nextInt(1 + xMax - xMin);
      int orientation = random.nextInt(6);

      Ship ship0 = new Ship(nextEntityId++, x, y, orientation, 0);
      Ship ship1 = new Ship(nextEntityId++, x, Simulation.MAP_HEIGHT - 1 - y, (6 - orientation) % 6, 1);

      state.teams.get(0).ships.add(ship0);
      state.teams.get(1).ships.add(ship1);
      state.teams.get(0).shipsAlive.add(ship0);
      state.teams.get(1).shipsAlive.add(ship1);

    }

    for (Ship ship : state.teams.get(0).ships) {
      state.ships.add(ship);
    }
    for (Ship ship : state.teams.get(1).ships) {
      state.ships.add(ship);
    }

    // Generate mines
    while (state.mines.size() < mineCount) {
      int x = 1 + random.nextInt(Simulation.MAP_WIDTH - 2);
      int y = 1 + random.nextInt(Simulation.MAP_HEIGHT / 2);

      Mine m = new Mine(nextEntityId++, x, y);
      boolean cellIsFreeOfMines = state.mines.stream().noneMatch(mine -> mine.position.equals(m.position));
      boolean cellIsFreeOfShips = true;
      for (int i=0;i<state.ships.size();i++) {
        Ship b = state.ships.get(i);
        cellIsFreeOfShips= cellIsFreeOfShips && !b.at(m.position);
      }
      
      if (cellIsFreeOfShips && cellIsFreeOfMines) {
        if (y != Simulation.MAP_HEIGHT - 1 - y) {
          state.mines.add(new Mine(nextEntityId++, x, Simulation.MAP_HEIGHT - 1 - y));
        }
        state.mines.add(m);
      }
    }

    // Generate supplies
    state.barrels = new ArrayList<>();
    while (state.barrels.size() < barrelCount) {
      int x = 1 + random.nextInt(Simulation.MAP_WIDTH - 2);
      int y = 1 + random.nextInt(Simulation.MAP_HEIGHT / 2);
      int h = Simulation.MIN_RUM_BARREL_VALUE + random.nextInt(1 + Simulation.MAX_RUM_BARREL_VALUE - Simulation.MIN_RUM_BARREL_VALUE);

      Barrel m = new Barrel(nextEntityId++, x, y, h);
      boolean valid = true;
      for (int i=0;i<state.ships.size();i++) {
        Ship ship = state.ships.get(i);
        if (ship.at(m.position)) {
          valid = false;
          break;
        }
      }
      for (Mine mine : state.mines) {
        if (mine.position == m.position) {
          valid = false;
          break;
        }
      }
      if (valid) {
        if (y != Simulation.MAP_HEIGHT - 1 - y) {
          state.barrels.add(new Barrel(nextEntityId++, x, Simulation.MAP_HEIGHT - 1 - y, h));
        }
        state.barrels.add(m);
      }
    }
  }

  public void handlePlayerOutput(int frame, int round, int playerIdx, String[] outputs)
      throws WinException, LostException, InvalidInputException {
    Team team = state.teams.get(playerIdx);

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
    simulation.playOneTurn();
    state.backup();
    
    if (state.teams.get(0).dead == true || state.teams.get(1).dead == true) {
      throw new GameOverException("endReached");
    }
  }

  protected String[] getInputForPlayer(int round, int playerIdx) {
    List<String> data = new ArrayList<>();

    // Player's ships first
    for (Ship ship : state.teams.get(playerIdx).shipsAlive) {
        data.add(ship.toPlayerString(playerIdx));
    }

    // Number of ships
    data.add(0, String.valueOf(data.size()));

    // Opponent's ships
    for (Ship ship : state.teams.get((playerIdx + 1) % 2).shipsAlive) {
        data.add(ship.toPlayerString(playerIdx));
    }

    // Visible mines
    for (Mine mine : state.mines) {
        boolean visible = false;
        for (Ship ship : state.teams.get(playerIdx).shipsAlive) {
            if (ship.position.distanceTo(mine.position) <= Simulation.MINE_VISIBILITY_RANGE) {
                visible = true;
                break;
            }
        }
        if (visible) {
            data.add(mine.toPlayerString(playerIdx));
        }
    }

    for (CannonBall ball : state.cannonballs) {
        data.add(ball.toPlayerString(playerIdx));
    }

    for (Barrel barrel : state.barrels) {
        data.add(barrel.toPlayerString(playerIdx));
    }

    data.add(1, String.valueOf(data.size() - 1));

    return data.toArray(new String[data.size()]);
}

  public int winner() {
    for (Team team : state.teams) {
      if (team.shipsAlive.isEmpty()) {
        return 1-team.id; // the other team won
      }
    }

    return state.teams.get(0).getScore() > state.teams.get(1).getScore() ? 0 : 1;
  }

}
