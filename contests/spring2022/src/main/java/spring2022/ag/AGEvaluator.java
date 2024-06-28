package spring2022.ag;

import spring2022.Player;
import spring2022.Pos;
import spring2022.State;
import spring2022.Unit;

public class AGEvaluator {

  private static final Pos[] PATROL = new Pos[] {
                                              Pos.get(2757, 5500),
                                              Pos.get(6000, 3000),
                                              };

  private static final Pos DEFENSE = new Pos(6000,1000);
  
  
  final Pos target = new Pos();
  public double evaluate(LightState state) {
    double score = 0;

    score += 1_000_000 * state.health;
    score += state.mana;
    score += 4 * state.wildMana;
    
    int closestDist = Integer.MAX_VALUE;
    Unit closest;
    int mobsInBaseSight = 0;
    int oppInBase = 0;
    
    for (int u = 0; u < state.unitsFE; u++) {
      Unit unit = state.units[u];
      int distToBase = unit.pos.fastDist(State.myBase);
    
      if (distToBase > State.BASE_TARGET_DIST + 2000) continue;
      
      mobsInBaseSight++;
      
      if (closestDist > distToBase) {
        closestDist = distToBase;
        closest = unit;
      }
      
      int stepsToKillBase = Math.max(1, (distToBase) / State.MOB_MOVE);
      int distToHero0 = state.hero[0].pos.fastDist(unit.pos);
      int distToHero1 = state.hero[1].pos.fastDist(unit.pos);

      int stepsToReachIt0 = distToHero0 / State.HERO_MAX_MOVE;
      int stepsToReachIt1 = distToHero1 / State.HERO_MAX_MOVE;

      
      if (unit.health > Math.max(0, (stepsToKillBase - stepsToReachIt0)) * 2 + Math.max(0, (stepsToKillBase - stepsToReachIt1)) * 2) {
        score -= 10_000;
      } else {
        score -= 1000.0 * unit.health / (Math.max(0, (stepsToKillBase - stepsToReachIt0)) * 2 + Math.max(0, (stepsToKillBase - stepsToReachIt1)) * 2);
      }
      
      // ne pas avoir de mob dans son rayon d'action
      for (int o=0;o<3;o++) {
        if (state.oppHeroes[o].pos.fastDist(State.myBase) < 7000) {
          oppInBase++;
          
          if (unit.pos.fastDist(state.oppHeroes[o].pos) < 2200) {
            score -= 100;
          }
          
        }
      }
      
      score -= 1.0 * unit.health / stepsToKillBase;
          
    }
    
    int oppInAttackZone = 0;
    for (int o=0;o<3;o++) {
      score += state.oppHeroes[o].pos.fastDist(State.myBase);
      if (state.oppHeroes[o].pos.dist(DEFENSE) < 2000) {
        oppInAttackZone++;
      }
    }

    // go defend
    if (Player.ennemyAttaquantsCount > 1 && oppInAttackZone > 0) {
      score -= 10_000 * Math.min(DEFENSE.fastDist(state.hero[0].pos), DEFENSE.fastDist(state.hero[1].pos));
    }
    
    if (mobsInBaseSight> 0) {
      score -= 1.0 * Math.max(2000, state.hero[0].pos.fastDist(State.myBase));
      score -= 1.0 * Math.max(2000, state.hero[1].pos.fastDist(State.myBase));
    } else {
      score -= 0.001 * state.hero[0].pos.fastDist(PATROL[0]);
      score -= 0.001 * state.hero[1].pos.fastDist(PATROL[1]);
    }
    
    
    return score;
  }
}
