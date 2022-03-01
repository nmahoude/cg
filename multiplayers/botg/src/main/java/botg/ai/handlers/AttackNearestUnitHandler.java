package botg.ai.handlers;

import botg.Action;
import botg.State;
import botg.units.Hero;
import botg.units.Unit;

public class AttackNearestUnitHandler extends Handler{

  @Override
  protected Action _think(State state, Hero hero) {
    Unit best = null;
    int bestScore = Integer.MAX_VALUE;
    for (Unit u : state.opp.units) {
      if (u.inRange(hero, hero.range)) {
        if (u.health < bestScore) {
          bestScore = u.health;
          best = u;
        }
      }
    }
    if (best != null) {
      return Action.attack(best.unitId);
    } else {
      return Action.ATTACK_NEAREST_UNIT;
    }
  }

}
