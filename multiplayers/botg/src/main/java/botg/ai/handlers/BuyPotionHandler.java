package botg.ai.handlers;

import botg.Action;
import botg.Item;
import botg.State;
import botg.units.Hero;

public class BuyPotionHandler extends Handler {

  @Override
  protected Action _think(State state, Hero hero) {

    if (hero.health > hero.maxHealth * 0.3) {
      return null; // no need to buy a health potion for now
    }
    
    Item item = state.items.stream()
                  .filter(i -> i.cost <= state.gold)
                  .sorted((i1, i2) -> Integer.compare(i2.health, i1.health))
                  .findFirst().orElse(null);
    
    if (item != null) {
      return Action.buy(state, hero, item);
    } else {
      return null;
    }
  }

}
