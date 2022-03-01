package botg.ai;

import botg.Action;
import botg.Agent;
import botg.State;
import botg.units.Hero;

public class IronmanStrategy extends RangeHeroStrategy {

  @Override
  protected Action _think(Hero hero, State state, int actionIndex) {
    Agent opp = state.opp;
    
    if ((action = moveBack(state, hero))!= null) {
      return action;
    }

    if ((action = doLastHit()) != null) {
      System.err.println("Do last hit !");
      return action;
    }


    if ((action = shouldBuyStuff())!= null) {
      return action;
    }
    
    Hero toAttack = opp.heroes.stream().sorted((h1, h2) -> Integer.compare(h1.health, h2.health))
        .filter(h -> h.pos.dist(opp.tower.pos) > opp.tower.range)
        .findFirst().orElse(Hero.DEAD_HERO);

    if (toAttack != Hero.DEAD_HERO) {
      if ((action = castFireball(toAttack)) != null) {
        return action;
      }
      
      if ((action = castBurn(toAttack)) != null) {
        return action;
      }
    }
    
    if ((action = shouldAttack(toAttack)) != null) {
      return action;
    }
    
    action = attackNearestUnit(state, hero);
    
    
    return action;
  }

  private Action castBurn(Hero toAttack) {
    if (hero.mana >= 50 && hero.coolDowns[2] == 0) {
      if (toAttack.dist(hero) < 250) {
        return Action.on("BURNING", toAttack.pos);
      }
    }
    return null;
  }

  private Action castFireball(Hero toAttack) {
    if (hero.mana >= 60 && hero.coolDowns[1] == 0) {
      if (toAttack.dist(hero) < 900) {
        return Action.on("FIREBALL", toAttack.pos);
      }
    }
    return null;
  }

}
