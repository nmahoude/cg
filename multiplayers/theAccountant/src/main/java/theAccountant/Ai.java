package theAccountant;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


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
    }
    command = commands.remove(0);
  }

  private Enemy chooseOneTarget(List<Enemy> enemies) {
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
