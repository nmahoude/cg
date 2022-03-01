package botg.ai.handlers;

import botg.Action;
import botg.State;
import botg.units.Hero;
import botg.units.Unit;

public class DoLastHitHandler extends Handler {

  @Override
  protected Action _think(State state, Hero hero) {
 // find the unit to do the last hit !

    Unit toLastHit = null;
    for( Unit u : opp.units) {
      if (u.health <= 0 || u.health > hero.damage) continue;
      if (u.inRange(hero, hero.range)) {
        System.err.println("Can lastHit unit "+u.unitId+" with "+u.health+" health left!");
        toLastHit = u;
      }
      
      
      
    }
    if (toLastHit != null) {
      toLastHit.health = -1;
      return Action.attack(toLastHit.unitId);
    } else {
      return null;
    }
  }
}
