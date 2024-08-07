package botg.ai.handlers;

import botg.Action;
import botg.State;
import botg.units.Hero;
import botg.units.Unit;
import trigonometry.Point;

public class DoLastHitHandler extends Handler {

  @Override
  protected Action _think(State state, Hero hero) {
 // find the unit to do the last hit !
    int maxDist = hero.moveRangeForAttack;;
    
    Unit toLastHit = null;
    boolean directAttack = false;
    for( Unit u : opp.units) {
      if (u.health <= 0 || u.health > hero.damage) continue;
      
      if (u.inRange(hero, hero.range)) {
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
      System.err.println("Can lastHit unit "+toLastHit.unitId+" with "+toLastHit.health+" health left!"+ " with hero damage "+hero.damage);
      toLastHit.health = -1;
      if (directAttack) {
        return Action.attack(toLastHit.unitId);
      } else {
        Point moveTowards = hero.pos.moveTowards(toLastHit.pos, hero.dist(toLastHit) - hero.range);
        if (opp.tower.inRangeForAttack(moveTowards)) {
          System.err.println("To close to enemy tower");
          return null;
        }
        return Action.moveAndAttack(toLastHit.unitId, moveTowards);
      }
    } else {
      return null;
    }
  }
}
