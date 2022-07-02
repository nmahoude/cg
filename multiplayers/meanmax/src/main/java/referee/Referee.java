package referee;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Random;

import meanmax.Game;
import meanmax.Player;
import meanmax.ai.ag.AG;
import meanmax.entities.Entity;
import meanmax.entities.Tanker;
import meanmax.simulation.Action;
import meanmax.simulation.Simulation;
import referee.CodingGameReferee.TankerSpawn;

/**
 * Do a match with 3 AI !
 *
 */
public class Referee {
  private static final int LOOTER_COUNT = 3;
  static int TANKERS_BY_PLAYER;
  static int TANKERS_BY_PLAYER_MIN = 1;
  static int TANKERS_BY_PLAYER_MAX = 3;
  static int TANKER_MIN_SIZE = 4;
  static int TANKER_MAX_SIZE = 10;
  static double TANKER_SPAWN_RADIUS = 8000.0;
  static int TANKER_START_THRUST = 2000;
  static double TANKER_RADIUS_BASE = 400.0;
  static double TANKER_RADIUS_BY_SIZE = 50.0;
  static double TANKER_MIN_RADIUS = TANKER_RADIUS_BASE + TANKER_RADIUS_BY_SIZE * TANKER_MIN_SIZE;
  static double TANKER_MAX_RADIUS = TANKER_RADIUS_BASE + TANKER_RADIUS_BY_SIZE * TANKER_MAX_SIZE;
  static int TANKER_EMPTY_WATER = 1;
  static double TANKER_EMPTY_MASS = 2.5;
  static double TANKER_MASS_BY_WATER = 0.5;
  static double TANKER_FRICTION = 0.40;
  
  Map<Integer, Queue<TankerSpawn>> tankers = new HashMap<>();
  private Simulation simulation;
  private static AG ai0 = new AG();
  private static AG ai1= new AG();
  private static AG ai2 = new AG();
  private Action actions[] = new Action[9];
  public int turn = 0;
  
  public static void main(String[] args) {
    Thread.currentThread().setPriority(9);
    int wins[] = new int[3];
    initAIs();
    
    for (int i=0;i<50;i++) {
      Referee referee = new Referee();
      referee.playOneGame();
      referee.updateWins(wins);
      System.out.println(String.format("wins : %2d %2d %2d", wins[0], wins[1], wins[2]));
    }
  }
  
  private void updateWins(int[] wins) {
    System.out.print(String.format("score : %2.0f %2.0f %2.0f ->", Game.players[0].score, Game.players[1].score, Game.players[2].score));
    if (Game.players[0].score >= Game.players[1].score 
        && Game.players[0].score >= Game.players[2].score) {
      wins[0]++;
    }
    if (Game.players[1].score >= Game.players[0].score 
        && Game.players[1].score >= Game.players[2].score) {
      wins[1]++;
    }
    if (Game.players[2].score >= Game.players[0].score 
        && Game.players[2].score >= Game.players[1].score) {
      wins[2]++;
    }
  }

  private static void initAIs() {
    ai0.MAX_TIME = 20_000_000;
    ai1.MAX_TIME = 20_000_000;
    ai2.MAX_TIME = 20_000_000;
    
  }

  public void playOneGame() {
    init();
    while (turn < 200 && !gameOver()) {
      playOneTurn();
      if (turn % 20 == 0 ) {
        System.out.print(".");
      }
      //System.err.println(String.format("Scores : %2.0f %2.0f %2.0f", Game.players[0].score,Game.players[1].score,Game.players[2].score));
    }
    System.out.println();
  }

  public void init() {
    Properties properties = new Properties();
    properties.setProperty("seed", "" + new Random(System.currentTimeMillis()).nextLong());
    
    simulation = new Simulation();
    for (int i=0;i<actions.length;i++) {
      actions[i] = new Action();
    }
    turn = 0;
    
    Game.wrecks_FE = 0;
    Game.tankers_FE = 0;
    Game.skillEffects_FE = 0;
    Game.seDoofs_FE = 0;
    Game.entities_FE = 9;
    
    Game.fullBackup();
    initReferee(properties);
    Game.fullBackup();
  }

  public void playOneTurn() {
    turn ++;


    Game.start = System.currentTimeMillis();
    rotatePlayers();
    ai1.think(Game.players[0]);
    actions[3].copyFrom(ai1.bestSolution.actions[0][0]);
    actions[4].copyFrom(ai1.bestSolution.actions[0][1]);
    actions[5].copyFrom(ai1.bestSolution.actions[0][2]);
    rotatePlayers();
    rotatePlayers();
    Game.restore();

    Game.start = System.currentTimeMillis();
    rotatePlayers();
    rotatePlayers();
    ai2.think(Game.players[0]);
    actions[6].copyFrom(ai2.bestSolution.actions[0][0]);
    actions[7].copyFrom(ai2.bestSolution.actions[0][1]);
    actions[8].copyFrom(ai2.bestSolution.actions[0][2]);
    rotatePlayers();
    Game.restore();

    Game.start = System.currentTimeMillis();
    ai0.think(Game.players[0]);
    actions[0].copyFrom(ai0.bestSolution.actions[0][0]);
    actions[1].copyFrom(ai0.bestSolution.actions[0][1]);
    actions[2].copyFrom(ai0.bestSolution.actions[0][2]);
    Game.restore();

    
    simulation.simulate(actions);
    spawnNewTankers();
    Game.cleanup();
    Game.fullBackup();
  }
  
  private void spawnNewTankers() {
    for (int i=0;i<Game.tankers_FE;i++) {
      Tanker tanker = Game.tankers[i];
      if (tanker.dead) {
        spawnTanker(tankers, Game.random.nextInt(3));
      }
    }
  }
  private void rotatePlayers() {
    Player temp = Game.players[0];
    Game.players[0] = Game.players[1];
    Game.players[1] = Game.players[2];
    Game.players[2] = temp;
    
    for (int p=0;p<3;p++ ) {
      Entity tmp = Game.entities[0];
      for (int i=0;i<8;i++) {
        Game.entities[i] = Game.entities[i+1];
      }
      Game.entities[8] = tmp;
    }
  }

  public boolean gameOver() {
    return Game.players[0].score>=50 || Game.players[1].score>=50 || Game.players[2].score>=50;
  }



  protected void initReferee(Properties prop) {
    long seed;
    try {
      seed = Long.valueOf(prop.getProperty("seed", String.valueOf(new Random().nextLong())));
    } catch (NumberFormatException e) {
      seed = new Random().nextLong();
    }

    Random random = new Random(seed);

    TANKERS_BY_PLAYER = TANKERS_BY_PLAYER_MIN + random.nextInt(TANKERS_BY_PLAYER_MAX - TANKERS_BY_PLAYER_MIN + 1);

    // Init players
    for (int i=0;i<3;i++) {
      Game.players[i].score = 0;
      Game.players[i].rage = 0;
      Game.players[i].backup();
    }
    
    // Generate the map
    Queue<TankerSpawn> queue = new LinkedList<>();
    for (int i = 0; i < 500; ++i) {
      queue.add(new TankerSpawn(TANKER_MIN_SIZE + random.nextInt(TANKER_MAX_SIZE - TANKER_MIN_SIZE),
          random.nextDouble()));
    }
    tankers.put(0, new LinkedList<>(queue));
    tankers.put(1, new LinkedList<>(queue));
    tankers.put(2, new LinkedList<>(queue));
    
    // Random spawns for looters
    boolean finished = false;
    while (!finished) {
      finished = true;

      for (int i = 0; i < LOOTER_COUNT && finished; ++i) {
        double distance = random.nextDouble() * Game.MAP_RADIUS;
        double angle = random.nextDouble();

        for (meanmax.Player player : Game.players) {
          double looterAngle = (player.index + angle) * (Math.PI * 2.0 / ((double) 3));
          double cos = Math.cos(looterAngle);
          double sin = Math.sin(looterAngle);

          Entity looter = Game.entities[3*player.index + i];
          looter.position.x = cos * distance;
          looter.position.y = sin * distance;

          // If the looter touch a looter, reset everyone and try again
          for (int o=0;o<9;o++) {
            Entity other = Game.entities[o];
            if (other == looter) continue;
            if (looter.distance(other) <= looter.radius + other.radius) {
              finished = false;
              for (int o2=0;o2<9;o2++) {
                Entity u = Game.entities[o2];
                u.position.x = 0;
                u.position.y = 0;
              }
            }
          }
        }
      }
    }

    // Spawn start tankers
    for (int j = 0; j < TANKERS_BY_PLAYER; ++j) {
      for (int i=0;i<3;i++) {
        spawnTanker(tankers, i);
      }
    }

    adjust();
    
    for (int i=0;i<Game.entities_FE;i++) {
      Game.entities[i].unitId = i;
    }
    Game.fullBackup();
  }

  static int UNIT_ID = 200;
  static void spawnTanker(Map<Integer, Queue<TankerSpawn>> tankers, int index) {
    TankerSpawn spawn = tankers.get(index).remove();

    double angle = (index + spawn.angle) * Math.PI * 2.0 / ((double) 3);

    double cos = Math.cos(angle);
    double sin = Math.sin(angle);

    Tanker tanker = Game.tankers[Game.tankers_FE];
    tanker.unitId = UNIT_ID++;
    tanker.dead = false;
    tanker.size = spawn.size;
    tanker.water = TANKER_EMPTY_WATER;
    tanker.mass = TANKER_EMPTY_MASS + TANKER_MASS_BY_WATER * tanker.water;
    tanker.friction = TANKER_FRICTION;
    tanker.radius = TANKER_RADIUS_BASE + TANKER_RADIUS_BY_SIZE * tanker.size;
    
    double distance = TANKER_SPAWN_RADIUS + tanker.radius;

    boolean safe = false;
    while (!safe) {
      tanker.position.x = cos * distance;
      tanker.position.y = sin * distance;
      safe = true;
      for (int i=0;i<Game.entities_FE;i++) {
        Entity entity = Game.entities[i];
        if (entity == tanker) continue;
        if ( tanker.distance(entity) < tanker.radius + entity.radius) {
          safe = false;
        }
      }
      distance += TANKER_MIN_RADIUS;
    }

    tanker.thrust(Game.WATERTOWN, TANKER_START_THRUST);

    Game.entities[Game.entities_FE++] = tanker;
    Game.tankers_FE++;
    tanker.backup();
  }
  
  protected void adjust() {
    for (int i=0;i<Game.entities_FE;i++) {
      Game.entities[i].adjust();
    }
  }
}
