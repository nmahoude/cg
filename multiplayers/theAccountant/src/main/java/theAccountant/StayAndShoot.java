package theAccountant;

import java.util.ArrayList;
import java.util.List;

import trigonometry.Point;

public class StayAndShoot extends Strategy {

  private GameEngine engine;
  public StayAndShoot(GameEngine engine) {
    this.engine = engine;
  }
  
  List<Command> getCommands(Enemy target) {
    List<Command> commands = new ArrayList<>();
    int count = 0;
    while (target.lifePoints > 0 && !engine.wolffIsDead) {
      System.err.println("shooting target "+target.id+ " his simLife is "+target.lifePoints);
      Command command = new Shoot(target);
      engine.lastCommand = command;
      engine.playTurn();
      count++;
      commands.add(command);
    }
    if (engine.wolffIsDead) {
      System.err.println("Wolff is dead :( ");
      commands.add(new Move(new Point(0, 0)));
    }
    return commands;
  }
}
