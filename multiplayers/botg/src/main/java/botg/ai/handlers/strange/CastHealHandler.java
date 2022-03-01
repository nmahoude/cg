package botg.ai.handlers.strange;

import botg.Action;
import botg.State;
import botg.ai.handlers.Handler;
import botg.units.Hero;

public class CastHealHandler extends Handler {

  @Override
  protected Action _think(State state, Hero hero) {
    // TODO cast on ourself is possible ?
    
    if (friend == Hero.DEAD_HERO) return null;
    
    if (hero.mana >= 50 && hero.coolDowns[0] == 0) {
      if (friend.maxHealth - friend.health > hero.mana * 0.2) {
        if (hero.dist(friend) < 250) {
          return new Action("AOEHEAL " + friend.pos.x + " " + friend.pos.y);
        } else {
          // TODO what if he moves too ???
          return Action.moveTo(friend.pos);
        }
      }
    }
    return null;
  }

}
