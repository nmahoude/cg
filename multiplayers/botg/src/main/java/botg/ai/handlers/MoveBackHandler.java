package botg.ai.handlers;

import botg.Action;
import botg.Pos;
import botg.State;
import botg.units.Hero;

public class MoveBackHandler extends Handler {

  @Override
  protected Action _think(State state, Hero hero) {

    int maxX = state.enemyLine();
    if (hero.pos.x > maxX) {
      System.err.println("Should move back to " + maxX + "!");

      return Action.moveTo(Pos.from(maxX, hero.pos.y));
    }

    if (hero.pos.dist(state.opp.tower.pos) < state.opp.tower.range) {
      return Action.moveTo(Pos.from(hero.pos.x - 100, hero.pos.y));
    }

    for (Hero h : state.opp.heroes) {
      if (hero.dist(h) < 200) {
        return Action.moveTo(state.me.tower.pos);
      }
    }

    return null;
  }

}
