package botg.ai;

import botg.Action;
import botg.units.Hero;

public abstract class RangeHeroStrategy extends Strategy {

  
  protected Action shouldAttack(Hero toAttack) {
    if (toAttack == Hero.DEAD_HERO) return null;
    
    // how many opp unit in viccinity ?
    long countUnit = opp.units.stream().filter(u -> u.dist(hero) < 300).count();
    
    
    if (countUnit < 2 && toAttack.pos.dist(opp.tower.pos) > opp.tower.range) {
      return new Action("ATTACK "+toAttack.unitId);
    }
    
    
    return null;
  }
}
