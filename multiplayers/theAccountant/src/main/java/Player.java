import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Shoot enemies before they collect all the incriminating data! The closer you
 * are to an enemy, the more damage you do but don't get too close or you'll get
 * killed.
 **/
class Player {

  
  static GameEngine gameEngineMain = new GameEngine();
  static AI ai = new AI();
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);

    // game loop
    while (true) {
      gameEngineMain.reset();
      
      int x = in.nextInt();
      int y = in.nextInt();
      gameEngineMain.createWolff(x,y);
      
      int dataCount = in.nextInt();
      for (int i = 0; i < dataCount; i++) {
        int dataId = in.nextInt();
        int dataX = in.nextInt();
        int dataY = in.nextInt();
        gameEngineMain.createDataPoint(dataId, dataX, dataY);
      }
      int enemyCount = in.nextInt();
      for (int i = 0; i < enemyCount; i++) {
        int enemyId = in.nextInt();
        int enemyX = in.nextInt();
        int enemyY = in.nextInt();
        int enemyLife = in.nextInt();
        gameEngineMain.createEnemy(enemyId, enemyX, enemyY, enemyLife);
      }

      ai.doYourStuff();

      System.out.println(ai.command.get()); 
    }
  }

  static class MCTS {
    Command command;
    Map<String, MCTS> childs = new HashMap<>();
    int score = 0;
    boolean gameOver = false;
    
    void simulate(GameEngine engine, int depth) {

      if (depth == 0) {
        score = engine.getScore();
        return;
      } else {
        // random command
        if (ThreadLocalRandom.current().nextInt(1000) > 800 /* more shoot than move ? */) {
          // move, find to where ?
          int newX = 100*ThreadLocalRandom.current().nextInt(Zone.width/100);
          int newY = 100*ThreadLocalRandom.current().nextInt(Zone.height/100);
          command = new Move(new P(newX,newY));
        } else {
          // shoot
          int size = engine.enemies.size();
          int index = ThreadLocalRandom.current().nextInt(size);
          command = new Shoot(engine.enemies.get(index));
        }
        engine.lastCommand = command;
        engine.playTurn();

        if (engine.gameOver()) {
          score = engine.getScore();
          gameOver = true;
          return;
        } else {
          MCTS mcts = childs.get(command.get());
          if (mcts == null ) {
            mcts = new MCTS();
            childs.put(command.get(), mcts);
            mcts.command = command;
          }
          mcts.simulate(engine, depth-1);
        }
      }
    }

    public int score() {
      if (childs.isEmpty()) {
        return score;
      } else {
        int score = -1000;
        for (Entry<String, MCTS> m : childs.entrySet()) {
          score = Math.max(score,  m.getValue().score());
        }
        return score;
      }
    }
    public MCTS getBestChild() {
      System.err.println("child count : "+childs.size());
      int bestScore = -1000;
      MCTS bestChild = null;
      for (Entry<String, MCTS> m : childs.entrySet()) {
        int score = m.getValue().score();
        System.err.println("child: "+m.getValue().command.get()+" -> "+score);
        if (score > bestScore) {
          bestScore = score;
          bestChild = m.getValue();
        }
      }
      return bestChild;
    }
  }
  static class AI {
    static int MAX_DEPTH = 10;
    static int BREADTH = 5000;
    
    Command command ;
    public void doYourStuff() {
      MCTS root = new MCTS();
      for (int i=0;i<BREADTH;i++) {
        GameEngine copy = gameEngineMain.duplicate();
        root.simulate(copy, MAX_DEPTH);
      }
      
      MCTS best = root.getBestChild();
      command = best.command;
    }
  }
  
  static class Movable {
    GameEngine gameEngine;
    P p;
    final int maxMove;
    
    public Movable(GameEngine gameEngine, int maxMove) {
      super();
      this.gameEngine = gameEngine;
      this.maxMove = maxMove;
    }

    boolean move(P target) {
      if (target.equals(p)) {
        return true;
      } else {
        int vecx = target.x-p.x;
        int vecy = target.x-p.y;
        double norm = Math.sqrt(vecx*vecx+vecy*vecy);
        if (norm > 1000) {
          p = new P((int)(p.x + 1.0*maxMove / norm * vecx)
                  ,(int)(p.y + 1.0*maxMove / norm * vecy));
          return false;
        } else {
          p = target;
          return true;
        }
      }
    }
  }
  static class Enemy extends Movable {
    private static final int ENEMY_WOLFF_RANGE = 2000;
    private static final int ENEMY_DATAPOINT_RANGE = 500;
    int lifePoints;
    int id;
    public Enemy(GameEngine engine) {
      super(engine,500);
    }

    boolean checkForDeath(P wolffPos) {
      return wolffPos.squareDistance(p) <= ENEMY_WOLFF_RANGE*ENEMY_WOLFF_RANGE;
    }

    public void moveToNearestTarget() {
      DataPoint dp = findNearestDataPoint();
      move(dp.p);
    }

    public void checkReachTarget() {
      DataPoint dp = findNearestDataPoint();
      if (dp.p.squareDistance(p) < ENEMY_DATAPOINT_RANGE*ENEMY_DATAPOINT_RANGE) {
        gameEngine.removeDataPoint(dp);
      }
    }
    private DataPoint findNearestDataPoint() {
      DataPoint closestDP = null;
      int minDist = Integer.MAX_VALUE;
      int minId = Integer.MAX_VALUE;
      for (DataPoint dp : gameEngine.dataPoints) {
        int distance = dp.p.squareDistance(p);
        if (distance < minDist 
            || distance == minDist && minId > dp.id) {
          minDist = distance;
          closestDP = dp;
          minId = dp.id;
        }
      }
      return closestDP;
    }

    public Enemy duplicate(GameEngine newEngine) {
      Enemy e = new Enemy(newEngine);
      e.p = p;
      e.id = id;
      e.lifePoints = lifePoints;
      return e;
    }
  }
  static class DataPoint {
    int id;
    int worth = 100;
    P p;
    
    DataPoint(P p) {
      this.p = p;
    }
    public DataPoint duplicate() {
      DataPoint dp = new DataPoint(p);
      dp.id = id;
      dp.worth = worth;
      return dp;
    }
  }
  static class Wolff  extends Movable {    
    int score;
    
    public Wolff(GameEngine engine) {
      super(engine, 1000);
    }

    void damage(Enemy enemy) {
      double x = p.distance(enemy.p);
      double damage = 125_000 / Math.pow(x, 1.2);
      enemy.lifePoints -= damage;
      if (enemy.lifePoints <= 0) {
        gameEngine.removeEnemy(enemy);
      }
    }
    public void shoot(int id) {
      Enemy enemy = gameEngine.findEnemyById(id);
      damage(enemy);
    }

    public Wolff duplicate(GameEngine newEngine) {
      Wolff w = new Wolff(newEngine);
      w.p = p;
      w.score = score;
      return w;
    }
  }
  static class Zone {
    static int width = 16000;
    static int height = 9000;
    
  }
  
  static class GameEngine {
    int totalEnemiesLife = 0;
    int shots = 0;
    
    Wolff wolff;
    Command lastCommand;
    boolean wolffIsDead = false;
    List<DataPoint> dataPoints = new ArrayList<>();
    List<Enemy> enemies = new ArrayList<>();
    
    void init() {
      for (Enemy e : enemies) {
        totalEnemiesLife += e.lifePoints;
      }
    }
    public void createWolff(int x, int y) {
      wolff= new Wolff(this);
      wolff.p = new P(x,y);
    }
    public GameEngine duplicate() {
      GameEngine newEngine = new GameEngine();
      for (DataPoint dp : dataPoints) {
        newEngine.dataPoints.add(dp.duplicate());
      }
      for (Enemy e : enemies) {
        newEngine.enemies.add(e.duplicate(newEngine));
      }
      newEngine.wolff = wolff.duplicate(newEngine);
      return newEngine;
    }
    public void createEnemy(int enemyId, int enemyX, int enemyY, int enemyLife) {
      Enemy enemy = new Enemy(this);
      enemy.id = enemyId;
      enemy.p = new P(enemyX, enemyY);
      enemy.lifePoints = enemyLife;
      enemies.add(enemy);
    }
    public void createDataPoint(int dataId, int dataX, int dataY) {
      DataPoint dp = new DataPoint(new P(dataX, dataY));
      dp.id = dataId;
      dataPoints.add(dp);
    }
    public void reset() {
      dataPoints.clear();
      enemies.clear();
    }
    public void updateWolffPosition(int x, int y) {
      wolff.p = new P(x,y); 
    }
    void playTurn() {
      enemyMoves();
      issueWolffCommand();
    }
    private void issueWolffCommand() {
      if (lastCommand.type == Command.Type.MOVE) {
        Move m = (Move)lastCommand;
        wolff.move(m.target);
        if (wolffIsDead()) {
          wolffIsDead = true;
          return;
        }
      } else {
        Shoot s = (Shoot)lastCommand;
        wolff.shoot(s.enemy.id);
        shots++;
      }
    }
    
    Enemy findEnemyById(int id) {
      for (Enemy e : enemies) {
        if (e.id == id) {
          return e;
        }
      }
      return null;
    }
    int getScore() {
      if (dataPoints.isEmpty()) {
        return 0;
      } else  if (enemies.isEmpty()) {
        return dataPoints.size() * Math.max(0, (totalEnemiesLife - 3*shots)) * 3;
      } else {
        return -1; //not finished
      }
    }
    private boolean wolffIsDead() {
      for (Enemy enemy : enemies) {
        if (enemy.checkForDeath(wolff.p)) {
          return true;
        }
      }
      return false;
    }


    public void removeDataPoint(DataPoint dp) {
      dataPoints.remove(dp);
    }

    public void removeEnemy(Enemy enemy) {
      enemies.remove(enemy);
    }
    
    private void enemyMoves() {
      for (Enemy enemy : enemies) {
        enemy.moveToNearestTarget();
      }
    }


    boolean gameOver() {
      return enemies.isEmpty() || dataPoints.isEmpty() || wolffIsDead;
    }
  }
  
  static class P {
    static P[][] ps = new P[20][20]; // maximum board
    static {
      for (int x=0;x<20;x++) {
        for (int y=0;y<20;y++) {
          ps[x][y] = new P(x,y);
        }
      }
    }
    static P get(int x,int y) {
      return ps[x][y];
    }
    
    
    final int x;
    final int y;

    public P(int x, int y) {
      super();
      this.x = x;
      this.y = y;
    }

    int distance(P p) {
      return (int)(Math.sqrt(squareDistance(p)));
    }

    private int squareDistance(P p) {
      return (p.x - x) * (p.x - x) + (p.y - y) * (p.y - y);
    }

    int manhattanDistance(P p) {
      return Math.abs(x - p.x) + Math.abs(y - p.y);
    }

    @Override
    public String toString() {
      return "(" + x + "," + y + ")";
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + x;
      result = prime * result + y;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      P other = (P) obj;
      return x == other.x && y == other.y;
    }
  }
  static abstract class Command {
    enum Type {
      MOVE, SHOOT
    }
    final Type type;
    public Command(Type type) {
      super();
      this.type = type;
    }
    abstract String get();
  }
  static class Move extends Command {
    public Move(P target) {
      super(Command.Type.MOVE);
      this.target = target;
    }

    P target;
    @Override
    String get() {
      return "MOVE "+target.x+" "+target.y;
    }
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((target == null) ? 0 : target.hashCode());
      return result;
    }
    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Move other = (Move) obj;
      if (target == null) {
        if (other.target != null)
          return false;
      } else if (!target.equals(other.target))
        return false;
      return true;
    }
  }
  static class Shoot extends Command  {
    public Shoot(Enemy enemy) {
      super(Command.Type.SHOOT);
      this.enemy = enemy;
    }

    Enemy enemy;
    @Override
    String get() {
      return "SHOOT "+enemy.id;
    }
    @Override
    public int hashCode() {
      return enemy.id;
    }
    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Shoot other = (Shoot) obj;
      return other.enemy.id == this.enemy.id;
    }
  }
  
}