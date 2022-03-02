package botg.ai.handlers.strange;

import botg.Action;
import botg.State;
import botg.ai.handlers.Handler;
import botg.units.Hero;

public class CastShieldHandler extends Handler {

  @Override
  protected Action _think(State state, Hero hero) {
    
    if (friend == Hero.DEAD_HERO) return null;
    
    if (hero.mana >= 40 && hero.coolDowns[1] == 0) {
      if (friend.dist(hero) < 500 && friend.health < friend.maxHealth 
          && (friend.pos.x > state.enemyLine() || friend.nextToEnnemies())) {
        return Action.on("SHIELD", hero.unitId);
      }
    }
    return null;
  }

}
