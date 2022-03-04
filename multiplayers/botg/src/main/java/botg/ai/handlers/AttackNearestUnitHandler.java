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
    
    Unit nearest = null;
    double nearestScore= Double.MAX_VALUE;
    
    for (Unit u : state.opp.units) {
      if (u.dist(hero) < nearestScore) {
        nearestScore = u.dist(hero);
        nearest = u;
      }

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
      if (nearest == null) return null;
      
      if (nearest.dist(hero) > hero.range && opp.tower.inRangeForAttack(hero.pos.moveTowards(nearest.pos, 1.0))) {
        // ok can move, we are far and won't fall in tower range if moving
        return Action.attack(nearest.unitId);
      } else if (
          !opp.tower.inRangeForAttack(nearest) // don't go in tower viccinity
          && hero.isSafeToGo(nearest.pos)
          ) {
        // ok move, we are still safe
        return Action.attack(nearest.unitId);
      } else {
        System.err.println("Can't attack nearest unit : "+nearest+" .." +opp.tower.inRangeForAttack(nearest) + " "+hero.isSafeToGo(nearest.pos));
        return null;
      }
    }
  }

}
