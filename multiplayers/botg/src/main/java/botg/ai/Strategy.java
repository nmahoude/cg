package botg.ai;

import botg.Action;
import botg.Agent;
import botg.Item;
import botg.Pos;
import botg.State;
import botg.units.Base;
import botg.units.Hero;
import botg.units.Unit;

public abstract class Strategy {
  Action action = Action.WAIT;

  protected State state;
  protected Hero hero;
  protected Agent opp;
  protected Agent me;
  protected Hero friend;

  
  public Action think(Hero hero, State state, int actionIndex) {
    System.err.println("*****************************");
    System.err.println("Thinking for "+hero.name);
    System.err.println("*****************************");
    
    this.hero = hero;
    this.state = state;
    this.me = state.me;
    this.opp = state.opp;
    this.friend = state.me.friendOf(hero);
    
    return _think(hero, state, actionIndex);
  }

  protected abstract Action _think(Hero hero, State state, int actionIndex);

  Action moveBack(State state, Hero hero) {
    int maxX = 0;
    for (Base unit : state.me.units) {
      if (unit.pos.x > maxX) maxX = unit.pos.x;
    }
    if (hero.pos.x > maxX) {
      System.err.println("Should move back to "+maxX+"!");

      return Action.moveTo(Pos.from(maxX, hero.pos.y));
    }

    if (hero.pos.dist(state.opp.tower.pos) < state.opp.tower.range) {
      return Action.moveTo(Pos.from(hero.pos.x-100, hero.pos.y));
    }
    
    
    for (Hero h : state.opp.heroes) {
      if (hero.dist(h) < 200) {
        return Action.moveTo(state.me.tower.pos);
      }
    }
    
    
    return null;
  }
  
  protected Action doLastHit() {
    // find the unit to do the last hit !

    Unit toLastHit = null;
    for( Unit u : opp.units) {
      if (u.health <= 0 || u.health > hero.damage) continue;
      if (u.inRange(hero, hero.range)) {
        System.err.println("Can lastHit unit "+u.unitId+" with "+u.health+" health left!");
        toLastHit = u;
      }
      
      
      
    }
    if (toLastHit != null) {
      toLastHit.health = -1;
      return Action.attack(toLastHit.unitId);
    } else {
      return null;
    }
  }
  
  
  protected Action shouldBuyStuff() {
    if (hero.itemsOwned < 4 && state.gold > 0) {
      int bestScore = 0;
      Item bestItem = null;
      for (Item item : state.items) {
        if (item.cost > state.gold) continue;
        int score = 0;
        if (item.health > 0) score += 100 * item.health;
        if (item.mana > 0)   score += 10 * item.mana;
        if (item.damage > 0) score += 1 * item.damage;
        
        if (score > bestScore) {
          bestScore = score;
          bestItem = item;
        }
      }
      if (bestItem != null) {
        System.err.println("buying item " + bestItem.name + " with score " + bestScore + " for hero " + hero);
        action = new Action("BUY " + bestItem.name, "buy item !");
        state.gold -= bestItem.cost;
        return action;
      }
    }
    return null;
  }

  protected Action attackNearestUnit(State state, Hero hero) {
    Unit best = null;
    int bestScore = Integer.MAX_VALUE;
    for (Unit u : state.opp.units) {
      if (u.inRange(hero, hero.range)) {
        if (u.health < bestScore) {
          bestScore = u.health;
          best = u;
        }
      }
    }
    if (best != null) {
      return Action.attack(best.unitId);
    } else {
      return Action.ATTACK_NEAREST_UNIT;
    }
  }
}
