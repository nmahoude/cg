package botg.ai.handlers.ironman;

import botg.Action;
import botg.State;
import botg.ai.handlers.Handler;
import botg.units.Hero;

public class CastBurnHandler extends Handler {

  @Override
  protected Action _think(State state, Hero hero) {
    
    if (hero.mana >= 50 && hero.coolDowns[2] == 0) {
      Hero toAttack = heroToAttack();
      if (toAttack  == Hero.DEAD_HERO) return null;
      if (toAttack.dist(hero) < 250) {
        
        return Action.on("BURNING", toAttack.pos);
      }
    }
    return null;
  }

}
