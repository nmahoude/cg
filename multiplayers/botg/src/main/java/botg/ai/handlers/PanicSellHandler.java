package botg.ai.handlers;

import botg.Action;
import botg.Item;
import botg.State;
import botg.units.Hero;

public class PanicSellHandler extends Handler {

  @Override
  protected Action _think(State state, Hero hero) {
    if (hero.willDie()) {
      System.err.println();
      for (Item i : hero.items) {
        hero.items.remove(i);
        return Action.sell(i.name);
      }
    }
    
    return null;
  }

}
