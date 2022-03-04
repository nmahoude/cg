package botg.ai.handlers;

import botg.Action;
import botg.State;
import botg.units.Hero;

public class NearDeathHandler extends Handler {

  @Override
  protected Action _think(State state, Hero hero) {
    
    
    if (!hero.willDie()) {
      return null;
    }
    
    if (hero.mana > 50) {
      
    }
    
    
    if (friend != Hero.DEAD_HERO) {
      // panic sell, but don't sell if we are the last one 
      System.err.println("Panic selling ...");
      Action action = hero.items.stream()
            .sorted((i1, i2) -> Integer.compare(i2.cost, i1.cost))
            .findFirst()
            .map(item -> Action.sell(state, hero, item))
            .orElse(null);
      
      if (action != null) {
        return action;
      }
    }
    
    return null;
  }

}
