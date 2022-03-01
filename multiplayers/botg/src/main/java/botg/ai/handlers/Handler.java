package botg.ai.handlers;

import botg.Action;
import botg.Agent;
import botg.State;
import botg.units.Hero;

public abstract class Handler {
  protected State state;
  protected Hero hero;
  protected Agent opp;
  protected Agent me;
  protected Hero friend;

  
  public final Action think(State state, Hero hero) {
    this.hero = hero;
    this.state = state;
    this.me = state.me;
    this.opp = state.opp;
    this.friend = state.me.friendOf(hero);
    
    return _think(state, hero);
  }
  
  protected abstract Action _think(State state, Hero hero);
  
 
  protected Hero heroToAttack() {
    return opp.heroes.stream()
        .sorted((h1, h2) -> Integer.compare(h1.health, h2.health))
        .findFirst()
        .orElse(Hero.DEAD_HERO);
  }
}
