package botg.ai.handlers;

import botg.Action;
import botg.State;
import botg.units.Hero;

public class RangedHeroAttackHandler extends Handler {

  @Override
  protected Action _think(State state, Hero hero) {
    Hero toAttack = heroToAttack();
    
    if (toAttack  == Hero.DEAD_HERO)
      return null;

    // how many opp unit in viccinity ?
    long countUnit = opp.units.stream().filter(u -> u.dist(hero) < 300).count();

    if (countUnit < 2 && toAttack.pos.distTo(opp.tower.pos) > opp.tower.range) {
      return new Action("ATTACK " + toAttack.unitId);
    }

    return null;
  }

}
