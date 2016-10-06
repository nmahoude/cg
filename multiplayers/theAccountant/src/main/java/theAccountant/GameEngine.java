package theAccountant;

import java.util.ArrayList;
import java.util.List;

import trigonometry.Point;

public class GameEngine {
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
    wolff.p = new Point(x,y);
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
    newEngine.totalEnemies = totalEnemies;
    newEngine.totalEnemiesLife = totalEnemiesLife;
    return newEngine;
  }
  public void createEnemy(int enemyId, int enemyX, int enemyY, int enemyLife) {
    Enemy enemy = new Enemy(this);
    enemy.id = enemyId;
    enemy.p = new Point(enemyX, enemyY);
    enemy.lifePoints = enemyLife;
    enemies.add(enemy);
  }
  public void createDataPoint(int dataId, int dataX, int dataY) {
    DataPoint dp = new DataPoint(new Point(dataX, dataY));
    dp.id = dataId;
    dataPoints.add(dp);
  }
  public void reset() {
    dataPoints.clear();
    enemies.clear();
    wolffIsDead = false;
  }
  public void updateWolffPosition(int x, int y) {
    wolff.p = new Point(x,y); 
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
    System.err.println("Enemy not found : "+id);
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
