package botg.ai.handlers;

import java.util.List;
import java.util.stream.Collectors;

import botg.Action;
import botg.State;
import botg.units.Hero;
import trigonometry.Point;

public class RangedHeroAttackHandler extends Handler {

  private static final int AGGRO_RANGE = 300;

  @Override
  protected Action _think(State state, Hero hero) {
    
    List<Hero> opps = opp.heroes.stream()
        .sorted((o1, o2) -> Double.compare(o1.dist(hero), o2.dist(hero)))
        .collect(Collectors.toList());

    for (Hero toAttack : opps) {
      if (!hero.inRange(toAttack, hero.range + hero.moveRangeForAttack)) continue; // not in range
      
      // how many opp unit in viccinity ?
      long countUnit = opp.units.stream().filter(u -> u.health > 0 && u.dist(hero) < AGGRO_RANGE).count();
  
      if (countUnit == 0 && toAttack.pos.distTo(opp.tower.pos) > opp.tower.range) {
        
        if (hero.inRangeForAttack(toAttack)) {
          return new Action("ATTACK " + toAttack.unitId);
        } else {
          Point pos = hero.pos.moveTowards(toAttack.pos, hero.moveRangeForAttack);
          return Action.moveAndAttack(toAttack.unitId, pos);
        }
      }
    }

    return null;
  }

}
