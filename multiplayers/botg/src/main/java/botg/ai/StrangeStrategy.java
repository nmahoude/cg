package botg.ai;

import botg.Action;
import botg.State;
import botg.units.Hero;

public class StrangeStrategy extends RangeHeroStrategy {

  @Override
  protected Action _think(Hero hero, State state, int actionIndex) {
    
    if ((action = moveBack(state, hero))!= null) {
      System.err.println("Move back");
      return action;
    }

    if ((action = doLastHit()) != null) {
      System.err.println("Do last hit !");
      return action;
    }
    
    if ((action = shouldBuyStuff()) != null) {
      System.err.println("buy stuff ");
      return action;
    }

    
    // check for heal
    if ((action = shouldHeal()) != null) {
      return action;
    }
    
    // shield
    if ((action = shouldShield()) != null) {
      return action;
    }
    
    Hero toAttack = opp.heroes.stream().sorted((h1, h2) -> Integer.compare(h1.health, h2.health))
        .findFirst().orElse(Hero.DEAD_HERO);
    
    if (toAttack != Hero.DEAD_HERO) {
      // pull ?
      if (hero.mana >= 40 && hero.coolDowns[2] == 0) {
        if (hero.dist(toAttack) < 400) {
          return Action.on("PULL", toAttack.unitId);
        }
      }
      
    }
    
    if (toAttack.pos.dist(opp.tower.pos) > opp.tower.range && hero.mana < 40) {
      return Action.moveTo(state.me.tower.pos);
    }
    
    if ((action = shouldAttack(toAttack)) != null) {
      return action;
    }
    
    return attackNearestUnit(state, hero);
    
  }

  private Action shouldShield() {
    if (hero.mana >= 40 && hero.coolDowns[1] == 0) {
      if (friend.dist(hero) < 500 && friend.health < friend.maxHealth) {
        return Action.on("SHIELD", hero.unitId);
      }
    }
    return null;
  }

  private Action shouldHeal() {
    if (hero.mana >= 50 && hero.coolDowns[0] == 0) {
      if (friend.maxHealth - friend.health > hero.mana * 0.2) {
        if (hero.dist(friend) < 250) {
          return new Action("AOEHEAL "+friend.pos.x+" "+friend.pos.y);
        } else {
          // TODO what if he moves too ???
          return Action.moveTo(friend.pos);
        }
      }
    }
    return null;
  }

}
