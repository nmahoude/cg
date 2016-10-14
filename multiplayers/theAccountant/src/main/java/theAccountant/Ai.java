package theAccountant;

import java.util.ArrayList;
import java.util.List;

import trigonometry.Point;
import trigonometry.Vector;

public class Ai {
  Command command;
  GameEngine engine = null;
  List<Command> commands = null;

  Ai(GameEngine engine) {
    this.engine = engine;
  }

  public void doYourStuff() {
    double minDistToMe = Integer.MAX_VALUE;
    Enemy closestEnemyToMe = null;

    double minDistToDP = Integer.MAX_VALUE;
    Enemy closestEnemyToDP = null;

    for (Enemy e : engine.enemies) {
      double dist2 = engine.wolff.p.squareDistance(e.p);
      if (dist2 < minDistToMe) {
        minDistToMe = dist2;
        closestEnemyToMe = e;
      }

      Point dpTarget = e.findNearestDataPoint().p;
      double dist22 = dpTarget.squareDistance(e.p);
      if (dist22 < minDistToDP) {
        minDistToDP = dist22;
        closestEnemyToDP = e;
      }

    }

    Point dpTarget = closestEnemyToMe.findNearestDataPoint().p;
    Point nextPos = closestEnemyToMe.nextPosToTarget(dpTarget);
    double nextDist = nextPos.squareDistance(engine.wolff.p);
    double meToEnemyNearestToDp = engine.wolff.p.squareDistance(closestEnemyToDP.p);

    System.err.println("Dist to oneShot "+closestEnemyToMe.id + " = "+ closestEnemyToMe.squareDistanceToOneShot() +" , vs dist="+ minDistToMe);
    if (minDistToMe < Math.pow(Enemy.ENEMY_WOLFF_RANGE + Enemy.ENEMY_MOVE, 2)) {
      System.err.println("escape closest");
      Vector vec = closestEnemyToMe.p.sub(engine.wolff.p);
      command = new Move(new Point(engine.wolff.p.x - vec.vx, engine.wolff.p.y - vec.vy));
    } else {
      if (engine.enemies.size() == 1) {
        if (closestEnemyToMe.squareDistanceToOneShot() > minDistToMe) {
          command = new Shoot(closestEnemyToMe);
        } else if (minDistToMe > Math.pow(Enemy.ENEMY_WOLFF_RANGE + Enemy.ENEMY_MOVE + Wolff.WOLFF_MOVE, 2)){
          System.err.println("too far");
          Vector vec = closestEnemyToMe.p.sub(engine.wolff.p);
          command = new Move(new Point(engine.wolff.p.x + vec.vx, engine.wolff.p.y + vec.vy));
        } else {
          command = new Shoot(closestEnemyToMe);
        }
      }
      else if (closestEnemyToMe.squareDistanceToOneShot() > minDistToMe) {
        System.err.println("One shot");
        command = new Shoot(closestEnemyToMe);
      } else if (minDistToMe > Math.pow(Enemy.ENEMY_WOLFF_RANGE + Enemy.ENEMY_MOVE*2 + Wolff.WOLFF_MOVE, 2)) {
        System.err.println("too far");
        Vector vec = closestEnemyToMe.p.sub(engine.wolff.p);
        command = new Move(new Point(engine.wolff.p.x + vec.vx, engine.wolff.p.y + vec.vy));
      } else if (minDistToMe > Math.pow(Enemy.ENEMY_WOLFF_RANGE + Enemy.ENEMY_MOVE + Wolff.WOLFF_MOVE, 2)) {
        System.err.println("too far, but not full length");
        Vector vec = closestEnemyToMe.p.sub(engine.wolff.p);
        vec = vec.dot(500.0/vec.length());
        command = new Move(new Point(engine.wolff.p.x + vec.vx, engine.wolff.p.y + vec.vy));
      } else {
        System.err.println("shoot closest");
        command = new Shoot(closestEnemyToMe);
      }
    }
    // 100%
    /**
     * if (minDist < Math.pow(Enemy.ENEMY_WOLFF_RANGE + Enemy.ENEMY_MOVE, 2)) {
     * Vector vec = closestEnemy.p.sub(engine.wolff.p); command = new Move(new
     * Point(engine.wolff.p.x - vec.vx, engine.wolff.p.y - vec.vy)); } else if
     * (minDist > Math.pow(Enemy.ENEMY_WOLFF_RANGE + Enemy.ENEMY_MOVE, 2) * 4) {
     * Vector vec = closestEnemy.p.sub(engine.wolff.p); command = new Move(new
     * Point(engine.wolff.p.x + vec.vx, engine.wolff.p.y + vec.vy)); } else {
     * command = new Shoot(closestEnemy); }
     */
  }

  public void doYourStuff_old() {
    if (commands == null || commands.isEmpty()) {
      System.err.println("Finding new commands");
      Enemy target = chooseOneTarget(engine.enemies);
      if (target == null) {
        System.err.println("Best target is null, escape move");
        commands.clear();
        commands.add(new Move(new Point(0, 0)));
      } else {
        commands = new StayAndShoot(engine).getCommands(target);
      }
      System.err.println("Commands: " + commands);
    }
    command = commands.remove(0);
  }

  private Enemy chooseOneTarget(List<Enemy> enemies) {
    int score = 0;
    Enemy best = null;
    List<Enemy> clone = new ArrayList<>(enemies);
    for (Enemy e : clone) {
      GameEngine copyOfEngine = engine.duplicate();
      Enemy copyOfTarget = copyOfEngine.findEnemyById(e.id);
      commands = new StayAndShoot(copyOfEngine).getCommands(copyOfTarget);
      System.err.println("Enemy " + e.id + " score will be " + copyOfEngine.getScore());
      if (copyOfEngine.getScore() > score) {
        best = e;
        score = copyOfEngine.getScore();
      }
    }
    return best;
  }

  private Enemy chooseOneTarget_Closest(List<Enemy> enemies) {
    Enemy best = null;
    int minDist = Integer.MAX_VALUE;

    for (Enemy enemy : enemies) {
      int dist = (int) enemy.p.squareDistance(engine.wolff.p);
      if (dist < minDist) {
        minDist = dist;
        best = enemy;
      }
    }
    return best;
  }
}
