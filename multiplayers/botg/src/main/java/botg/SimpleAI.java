package botg;

import java.util.ArrayList;
import java.util.List;

import botg.units.Hero;
import botg.units.Unit;

public class SimpleAI {
  List<Unit> deadUnits = new ArrayList<>();
  
  
  List<Action> bestActions = new ArrayList<>();


  private State state;
  private Agent my;
  private Agent opp;



  public void think(State state) {
    bestActions.clear();
    
    this.state = state;
    deadUnits.clear();
    
    if (state.roundType < 0) {
      decideHero(state);
      return;
    }
      my = state.me;
      opp = state.opp;
      
      int actionIndex = 0;
      for (Hero hero : my.heroes) {
        bestActions.add(hero.think(state, actionIndex));
      }      
  }

  private void decideHero(State state2) {
    if (state.roundType == -2) {
      bestActions.add(Action.CHOOSE_IRONMAN);
    } else {
      bestActions.add(Action.CHOOSE_STRANGE);
    }
    return;
  }

  private Action thinkFor(int index, Hero hero) {
    Action action = Action.WAIT;
    
    System.err.println("opp heros : "+opp.heroes);
    System.err.println("Index "+index);
    if (index == 1) { // hulk !?
      System.err.println("Hulk cooldown of shield is "+hero.coolDowns[1]);

      long inRange = opp.heroes.stream().filter(hero::inRangeForAttack).count();
      System.err.println("In range =  "+inRange);
      if (inRange > 0 && hero.coolDowns[1] == 0 && hero.mana >= 30) {
        action = new Action("EXPLOSIVESHIELD");
        return action;
      }
    }
    
    if (index == 2) { // deadpool !?
      long inRange = opp.heroes.stream().filter(hero::inRangeForAttack).count();
      System.err.println("In range =  "+inRange);
      if (inRange > 0 && hero.coolDowns[0]  == 0 && hero.mana > 40) {
        action = new Action("COUNTER");
        return action;
      }
    }
    
    
    // check if we can last hit a unit in range
//    Unit lastHitUnit = opp.units.stream().filter(u -> u.pos.dist(hero.pos) < hero.range).filter(u -> u.health < hero.damage).findFirst().orElse(null);
//    if (lastHitUnit != null) {
//      System.err.println("Attacking unit "+lastHitUnit);
//      deadUnits.add(lastHitUnit);
//      return Action.attack(lastHitUnit.unitId);
//    }
      
    
    
    Hero toAttack = opp.heroes.stream().sorted((h1, h2) -> Integer.compare(h1.health, h2.health))
        .filter(h -> h.pos.dist(opp.tower.pos) > opp.tower.range)

        .findFirst().orElse(null);
    if (toAttack != null) {
      return new Action("ATTACK "+toAttack.unitId);
    } else {
      action = Action.attackNearest("Unit");
    }

    
    // check if we can buy something ?
    if (hero.itemsOwned < 4 && state.gold > 0) {
        int bestScore = 0;
        Item bestItem = null;
        for (Item item : state.items) {
            if (item.cost > state.gold)
                continue;
            int score = 0;
            if (item.damage > 0)
                score += 100 * item.damage;
            if (item.health > 0)
                score += 10 * item.health;
            if (score > bestScore) {
                bestScore = score;
                bestItem = item;
            }
        }
        if (bestItem != null) {
            System.err.println("buying item " + bestItem.name + " with score " + bestScore+" for hero "+hero);
            action = new Action("BUY " + bestItem.name, "buy item !");
            state.gold -= bestItem.cost;
            return action;
        }
    }
    
    return action;
  }

  public void output() {
    for (Action action : bestActions) {
      System.out.println(action);
    }
  }
}
