package botg.ai.handlers;

import botg.Action;
import botg.State;
import botg.units.Groot;
import botg.units.Hero;
import trigonometry.Point;

public class GrootHandler extends Handler {

  @Override
  protected Action _think(State state, Hero hero) {
    System.err.println("I'm @ "+hero.pos);
    for (Groot g : state.groots) {
      System.err.println("Groot @ "+g.pos+" with range "+g.range+" dist to me : "+g.dist(hero));
    }
    
    
    Groot groot = state.groots.stream().filter(g -> hero.inRange(g, g.range+100)).findFirst().orElse(null);
    if (groot == null) return null;
    
    // TODO if we can kill it, juste kill it for the gold
    // TODO move to at least 300 from spawn point of the groot
    System.err.println("TODO should flee groot !");
    return Action.moveTo(Point.from(hero.pos.x, 750));
    
  }

}
