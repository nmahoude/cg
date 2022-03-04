package botg.ai.handlers.ironman;

import botg.Action;
import botg.State;
import botg.ai.handlers.Handler;
import botg.units.Base;
import botg.units.Hero;

public class CastFireballHandler extends Handler {

  private static final int FIREBALL_RANGE = 900;
  private static final double AGGRO_RANGE = 300;

  @Override
  protected Action _think(State state, Hero hero) {
    
    if (hero.mana >= 60 && hero.coolDowns[1] == 0) {
      Base toAttack = heroToAttack();
      if (toAttack == Hero.DEAD_HERO) return null;
      
      long countUnit = opp.units.stream().filter(u -> u.health > 0 && u.dist(hero) < AGGRO_RANGE).count();
      
      if (countUnit > 0) return null;
      
      if (toAttack.dist(hero) < FIREBALL_RANGE) {
        return Action.on("FIREBALL", toAttack.pos);
      }
    }
    return null;
  }

}
