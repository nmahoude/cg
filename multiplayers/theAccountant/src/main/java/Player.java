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
  static final int WOLFF_MOVE = 1000;

  
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
      gameEngineMain.init();

      //debugEnemiesMoveToTheirTargets();
      
      ai.doYourStuff();

      System.out.println(ai.command.get()); 
    }
  }
  private static void debugEnemiesMoveToTheirTargets() {
    for (Enemy e : gameEngineMain.enemies) {
      String moves ="";
      DataPoint dp = e.findNearestDataPoint();
      List<P> ps = e.stepsToTarget(dp.p);
      for (P p : ps) {
        moves+=p.toString()+" -> ";
      }
      System.err.println("e: "+e.id+ "will move : "+moves);
    }
  }

  static class MCTS {
    Command command;
    Map<String, MCTS> childs = new HashMap<>();
    int score = 0;
    boolean gameOver = false;
    
    void simulate(GameEngine engine, MCTS parent, int depth) {

      if (depth == 0) {
        score = engine.getScore();
        return;
      } else {
        // random command
        if (ThreadLocalRandom.current().nextInt(1000) > 0 /* more shoot than move ? */) {
          // move, find to where ?
          int newX = engine.wolff.p.x+100*(ThreadLocalRandom.current().nextInt(21)-10);
          int newY = engine.wolff.p.y+100*(ThreadLocalRandom.current().nextInt(21)-10);
          command = new Move(new P(newX,newY));
        } else {
          // shoot, find a potential target
          List<Enemy> enemies = getPotentialTargets(engine);
          int size = enemies.size();
          int index = ThreadLocalRandom.current().nextInt(size);
          command = new Shoot(enemies.get(index));
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
          mcts.simulate(engine, this, depth-1);
        }
      }
    }

    private List<Enemy> getPotentialTargets(GameEngine engine) {
      List<Enemy> enemies = new ArrayList<>();
      
      for (DataPoint dp : engine.dataPoints) {
        Enemy nearestEnemy = null;
        int minDist = Integer.MAX_VALUE;
        for (Enemy enemy : engine.enemies) {
          int dist = enemy.p.squareDistance(dp.p);
          if (dist < minDist) {
            minDist = dist;
            nearestEnemy = enemy;
          }
        }
        if (nearestEnemy != null) {
          enemies.add(nearestEnemy);
        }
      }
      return enemies;
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
      int bestScore = -1_000_000;
      MCTS bestChild = null;
      for (Entry<String, MCTS> m : childs.entrySet()) {
        int score = m.getValue().score();
//        if (score > -1_000) {
//          System.err.println("child: "+m.getValue().command.get()+" -> "+score);
//        }
        if (score > bestScore) {
          bestScore = score;
          bestChild = m.getValue();
        }
      }
      return bestChild;
    }
  }
  static class AI {
    static int MAX_DEPTH = 1;
    static int BREADTH = 200;
    
    Command command ;
    public void doYourStuff() {
      MCTS root = new MCTS();
      for (int i=0;i<BREADTH;i++) {
        GameEngine copy = gameEngineMain.duplicate();
        root.simulate(copy, root, MAX_DEPTH);
      }
      
      MCTS best = root.getBestChild();
      if (best != null) {
        command = best.command;
      } else {
        command = new Move(new P(0,0));
      }
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
        if (norm > WOLFF_MOVE) {
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
    static final int ENEMY_WOLFF_RANGE = 2000;
    static final int ENEMY_DATAPOINT_RANGE = 500;
    static final int ENEMY_MOVE = 500;
    int lifePoints;
    int id;
    int turnToReachTarget;
    
    public Enemy(GameEngine engine) {
      super(engine,ENEMY_MOVE);
    }

    public void init() {
      DataPoint dp = findNearestDataPoint();
      turnToReachTarget = Math.round(dp.p.squareDistance(p) / (ENEMY_MOVE*ENEMY_MOVE))+1; // FIXME bug here
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
    
    // validated against game engine
    List<P> stepsToTarget(P target) {
      List<P> ps = new ArrayList<>();
      P futurePos = p;
      while (true) {
        Vector v = new Vector(target.x-futurePos.x, target.y-futurePos.y);
        double length = v.length();
        if (length < ENEMY_DATAPOINT_RANGE) {
          ps.add(target);
          return ps;
        } else {
          futurePos = new P((int)(futurePos.x+ENEMY_MOVE * v.vx / length), (int)(futurePos.y+ENEMY_MOVE * v.vy / length));
          ps.add(futurePos);
        }
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

    public int distanceToOneShot() {
      return (int)Math.pow(125_000.0/lifePoints, 1.0 / 1.2);
    }
    
    public Enemy duplicate(GameEngine newEngine) {
      Enemy e = new Enemy(newEngine);
      e.p = p;
      e.id = id;
      e.lifePoints = lifePoints;
      e.turnToReachTarget = turnToReachTarget;
      return e;
    }

    public void updateTurnToReachTarget() {
      DataPoint dp = findNearestDataPoint();
      turnToReachTarget = (int)Math.round(1.0*dp.p.distance(p) / ENEMY_MOVE); // FIXME bug here
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

    int getPotentialDamage(Enemy enemy) {
      double x = p.distance(enemy.p);
      return (int)Math.round(125_000 / Math.pow(x, 1.2));
    }
    void damage(Enemy enemy) {

      enemy.lifePoints -= getPotentialDamage(enemy);
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
    private int totalEnemies;
    
    void init() {
      totalEnemies = 0;
      for (Enemy e : enemies) {
        totalEnemiesLife += e.lifePoints;
        totalEnemies++;
        e.updateTurnToReachTarget();
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

      }
      if (wolffIsDead()) {
        wolffIsDead = true;
        return;
      }
      if (lastCommand.type == Command.Type.SHOOT) {
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
      if (dataPoints.isEmpty() || wolffIsDead) {
        return -1000;
      } else if (enemies.isEmpty()) {
        int score = dataPoints.size() * 100;
        score += 10 * totalEnemies-enemies.size();
        score += dataPoints.size() * Math.max(0, (totalEnemiesLife - 3*shots)) * 3;
        return score;
      } else {
        int score = 0;
        for (DataPoint dp : dataPoints) {
          score+=dp.worth;
        }
        int eLifePoints = 0;
        for (Enemy e : enemies) {
          eLifePoints+=e.lifePoints;
        }
        return score - eLifePoints; //not finished
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
  static class Vector {
    final double vx, vy;
    public Vector(double vx, double vy) {
      this.vx = vx;
      this.vy = vy;
    }
    @Override
    public String toString() {
      return "V("+vx+","+vy+")";
    }
    Vector normalize() {
      return new Vector(vx / length(), vy / length());
    }
    Vector rotate(double angle) {
      return new Vector(vx*Math.cos(angle) - vy*Math.sin(angle),
          vx*Math.sin(angle) + vy*Math.cos(angle));
    }
    Vector add(Vector v) {
      return new Vector(vx+v.vx, vy+v.vy);
    }
    Vector dot(double d) {
      return new Vector(d*vx, d*vy);
    }
    double dot(Vector v) {
      return vx*v.vx + vy*v.vy;
    }
    double length() {
      return Math.sqrt(vx*vx + vy*vy);
    }
    double angle(Vector v) {
      return Math.acos(this.dot(v) / (this.length() * v.length()));
    }
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(vx);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(vy);
      result = prime * result + (int) (temp ^ (temp >>> 32));
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
      Vector other = (Vector) obj;
      if (Double.doubleToLongBits(vx) != Double.doubleToLongBits(other.vx))
        return false;
      if (Double.doubleToLongBits(vy) != Double.doubleToLongBits(other.vy))
        return false;
      return true;
    }
  }
}