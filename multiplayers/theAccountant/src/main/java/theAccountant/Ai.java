package theAccountant;

import java.util.ArrayList;
import java.util.List;


public class Ai {
  Command command ;
  GameEngine engine = null;
  List<Command> commands = null;
  Ai(GameEngine engine) {
    this.engine = engine;
  }
  
  
  
  
  public void doYourStuff() {
    if (commands == null || commands.isEmpty()) {
      System.err.println("Finding new commands");
      Enemy target = chooseOneTarget(engine.enemies);
      commands = new StayAndShoot(engine).getCommands(target);
      System.err.println("Commands: "+commands);
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
      System.err.println("Enemy "+e.id+" score will be "+copyOfEngine.getScore());
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
      int dist = (int)enemy.p.squareDistance(engine.wolff.p);
      if (dist < minDist) {
        minDist = dist;
        best = enemy;
      }
    }
    return best;
  }
}
