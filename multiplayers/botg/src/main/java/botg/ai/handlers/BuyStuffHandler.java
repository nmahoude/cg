package botg.ai.handlers;

import botg.Action;
import botg.Item;
import botg.State;
import botg.units.Hero;

public class BuyStuffHandler extends Handler{

  @Override
  protected Action _think(State state, Hero hero) {
    if (hero.willDie()) return null;
    
    if (hero.itemsOwned < 4 && state.gold > 0) {
      int bestScore = 0;
      Item bestItem = null;
      for (Item item : state.items) {
        if (item.cost > state.gold) continue;
        int score = 0;
        
        if (item.isPotion) {
          if (hero.maxHealth - hero.health >= item.health - 25 ) score += 200 * item.health;
          if (hero.maxMana - hero.mana>= item.mana - 25 ) score += 100 * item.mana;
          
        } else {
          if (item.health > 0) score += 100 * item.health;
          if (item.mana > 0)   score += 10 * item.mana;
          if (item.damage > 0) score += 1 * item.damage;
          
        }
        
        if (score > bestScore) {
          bestScore = score;
          bestItem = item;
        }
      }
      if (bestItem != null) {
        System.err.println("buying item " + bestItem.name + " with score " + bestScore + " for hero " + hero);
        return Action.buy(state, hero, bestItem);
      }
    }
    return null;
  
  }

}
