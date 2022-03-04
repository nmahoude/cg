package botg.ai.handlers;

import botg.Action;
import botg.State;
import botg.units.Hero;
import botg.units.Unit;

public class DenyLastHitHandler extends Handler {

  @Override
  protected Action _think(State state, Hero hero) {
    // find the unit to do the last hit !
    int maxDist = hero.moveRangeForAttack;
    
    Unit toLastHit = null;
    boolean directAttack = false;
    for( Unit u : me.units) {
      if (u.health <= 0 || u.health > hero.damage) continue;
      
      long canDoLastHitCount = opp.heroes.stream().filter(h -> h.inRangeForAttack(u.pos)).count();
      if (canDoLastHitCount == 0) continue;
      
      
      
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
      System.err.println("Can deny last Hit unit "+toLastHit.unitId+" with "+toLastHit.health+" health left!");
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
