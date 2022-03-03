package botg.ai.handlers;

import botg.Action;
import botg.State;
import botg.units.Hero;
import botg.units.Unit;

public class DoLastHitHandler extends Handler {

  @Override
  protected Action _think(State state, Hero hero) {
 // find the unit to do the last hit !
    int maxDist = (int)(0.9 * hero.movementSpeed);
    
    Unit toLastHit = null;
    boolean directAttack = false;
    for( Unit u : opp.units) {
      if (u.health <= 0 || u.health > hero.damage) continue;
      
      if (u.inRange(hero, hero.range)) {
        System.err.println("Can lastHit unit "+u.unitId+" with "+u.health+" health left!");
        toLastHit = u;
        directAttack = true;
        break;
      }
      if (u.inRange(hero, hero.range+maxDist)) {
        toLastHit = u;
        directAttack = false;
        // don"t break, let a chance to find a simple attack (unit nearest)
      }
      
    }
    if (toLastHit != null) {
      toLastHit.health = -1;
      if (directAttack) {
        return Action.attack(toLastHit.unitId);
      } else {
        return Action.moveAndAttack(toLastHit.unitId, hero.pos.moveTowards(toLastHit.pos, hero.dist(toLastHit) - hero.range));
      }
    } else {
      return null;
    }
  }
}
