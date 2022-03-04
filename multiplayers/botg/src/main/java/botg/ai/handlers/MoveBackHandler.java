package botg.ai.handlers;

import botg.Action;
import botg.State;
import botg.units.Hero;
import trigonometry.Point;

public class MoveBackHandler extends Handler {

  @Override
  protected Action _think(State state, Hero hero) {
    if (opp.units.size() == 0) {
      // no units to check for line
      return null;
    }
    
    
    double maxX = state.enemyLine();
    
    if (hero.pos.x > maxX) {
      System.err.println("Should move back to " + maxX + "!");

      return Action.moveTo(Point.from(maxX, hero.pos.y));
    }

    if (hero.pos.distTo(state.opp.tower.pos) < state.opp.tower.range) {
      return Action.moveTo(Point.from(state.opp.tower.pos.x - (state.opp.tower.range+1), hero.pos.y));
    }

    if (!hero.isSafeToGo(hero.pos)) {
      System.err.println("Don't go too far in enemy territory");
      return Action.moveTo(me.tower.pos);
    }
    
    
    if (hero.dist(me.tower) > me.tower.range) {
      for (Hero h : state.opp.heroes) {
        if (hero.dist(h) < 200) {
          System.err.println("Flee from ennemy "+h);
          return Action.moveTo(state.me.tower.pos);
        }
      }
    }

    return null;
  }

}
