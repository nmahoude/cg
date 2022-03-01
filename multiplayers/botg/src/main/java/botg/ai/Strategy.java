package botg.ai;

import java.util.ArrayList;
import java.util.List;

import botg.Action;
import botg.Agent;
import botg.State;
import botg.ai.handlers.Handler;
import botg.units.Hero;

public abstract class Strategy {
  protected State state;
  protected Hero hero;
  protected Agent opp;
  protected Agent me;
  protected Hero friend;

  protected List<Handler> handlers = new ArrayList<>();
  
  public Action think(Hero hero, State state, int actionIndex) {
    System.err.println("*****************************");
    System.err.println("Thinking for "+hero.name);
    System.err.println("*****************************");
    
    this.hero = hero;
    this.state = state;
    this.me = state.me;
    this.opp = state.opp;
    this.friend = state.me.friendOf(hero);

    for (Handler h : handlers) {
      Action action = h.think(state, hero);
      if (action != null) {
        return action;
      }
    }
    
    return Action.WAIT;
  }

}
