package botg.ai.handlers.strange;

import botg.Action;
import botg.State;
import botg.ai.handlers.Handler;
import botg.units.Hero;
import trigonometry.Point;

public class CastHealHandler extends Handler {

  @Override
  protected Action _think(State state, Hero hero) {
    // TODO cast on ourself is possible ?
    
    if (friend == Hero.DEAD_HERO) return null;
    
    if (hero.mana < 50 || hero.coolDowns[0] != 0) {
      return null;
    }
    
    // TODO check if we can do better than hero.pos with my nearby units (center of gravity or something like this ?
    if (friend.health < friend.maxHealth * 0.4) {
      if (hero.dist(friend) < 250) {
        return new Action("AOEHEAL " + friend.pos.x + " " + friend.pos.y);
      } else if (hero.dist(friend) < 350) {
        Point target = hero.pos.moveTowards(friend.pos, 100);
        return new Action("AOEHEAL " + target.x + " " + target.y);
      } else {
        System.err.println("Move to heal");
        return Action.moveTo(friend.pos);
      }
    }
    return null;
  }

}
