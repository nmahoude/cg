package theAccountant;

import java.util.ArrayList;
import java.util.List;

import trigonometry.Point;
import trigonometry.Vector;

public class StayAndShoot extends Strategy {

  private GameEngine engine;
  public StayAndShoot(GameEngine engine) {
    this.engine = engine;
  }
  
  List<Command> getCommands(Enemy target) {
    List<Command> commands = new ArrayList<>();
    int count = 0;
    while (target.lifePoints > 0 && !engine.wolffIsDead) {
      int currentLifePoints = target.lifePoints;
      Command command = new Shoot(target);
      engine.lastCommand = command;
      engine.playTurn();
      System.err.println("shooting target "+target.id+ " his simLife go from "+currentLifePoints+" to "+target.lifePoints);
      count++;
      if (engine.wolffIsDead) {
        System.err.println("Wolff will be dead :( ");
        // find escape route
        Vector direction = engine.wolff.p.sub(target.p);
        Point escape1 = engine.wolff.p.add(direction.rotate(Math.PI / 2).normalize().dot(1000));
        Point escape2 = engine.wolff.p.add(direction.rotate(-Math.PI / 2).normalize().dot(1000));
        commands.add(new Move(escape2));
      } else {
        commands.add(command);
      }
    }
    System.err.println("Predicted points will be "+engine.getScore());
    return commands;
  }
}
