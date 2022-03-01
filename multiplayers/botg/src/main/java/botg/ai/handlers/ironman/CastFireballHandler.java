package botg.ai.handlers.ironman;

import botg.Action;
import botg.State;
import botg.ai.handlers.Handler;
import botg.units.Base;
import botg.units.Hero;

public class CastFireballHandler extends Handler {

  @Override
  protected Action _think(State state, Hero hero) {
    
    if (hero.mana >= 60 && hero.coolDowns[1] == 0) {
      Base toAttack = heroToAttack();
      if (toAttack == Hero.DEAD_HERO) return null;
      
      if (toAttack.dist(hero) < 900) {
        return Action.on("FIREBALL", toAttack.pos);
      }
    }
    return null;
  }

}
