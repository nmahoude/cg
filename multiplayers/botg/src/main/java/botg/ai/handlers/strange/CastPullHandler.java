package botg.ai.handlers.strange;

import botg.Action;
import botg.State;
import botg.ai.handlers.Handler;
import botg.units.Hero;

public class CastPullHandler extends Handler {

  @Override
  protected Action _think(State state, Hero hero) {
    Hero toAttack = heroToAttack();
    if (toAttack == Hero.DEAD_HERO) return null;
    
    if (hero.mana >= 40 && hero.coolDowns[2] == 0) {
      if (hero.dist(toAttack) < 400) {
        return Action.on("PULL", toAttack.unitId);
      }
    }
    return null;
  }

}
