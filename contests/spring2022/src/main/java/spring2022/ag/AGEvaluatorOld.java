package spring2022.ag;

import spring2022.Hero;
import spring2022.Player;
import spring2022.Pos;
import spring2022.State;
import spring2022.Unit;

public class AGEvaluatorOld {

  private static final Pos[] PATROL = new Pos[] {
                                              Pos.get(6000, 2000),
                                              Pos.get(2757, 5500)};

  
  final Pos target = new Pos();
  public double evaluate(LightState state) {
    double score = 0;

    score += 1_000_000 * state.health;
    score += state.extraBonus;// add something the simulator try to say (like a control )
    
    if (Player.turn < 40) {
      score += 1 * state.mana;
      score += 400 * state.wildMana;
       
    } else {
      score += 1 * state.mana;
      score += 2 * state.wildMana;
    }
    
    int mobsInBaseZoneCount = 0;
    int closestDist = Integer.MAX_VALUE;
    double relativeDists = 0;
    double relativeDistsToOpps = 0;
    double healthValue = 0.0;
    
    for (int u = 0; u < state.unitsFE; u++) {
      Unit unit = state.units[u];
      
      double coeff = 1.0;
//      if (!unit.nearBase && unit.threatFor == 0 ) {
//        coeff = 0.1;
//      } else {
//        coeff = 1.0;
//      }
      
      int distToBase = unit.pos.dist(State.myBase);
      if (distToBase < State.BASE_TARGET_DIST) {
        mobsInBaseZoneCount++;
        closestDist = Math.min(closestDist, distToBase);
        int stepsToKillBase = Math.max(1, (distToBase - 300) / State.MOB_MOVE);
        int distToHero0 = unit.pos.dist(state.hero[0].pos);
        int distToHero1 = unit.pos.dist(state.hero[1].pos);
        relativeDists += Math.min(distToHero0, distToHero1) / stepsToKillBase;
        if (stepsToKillBase < unit.health / 4) {
          // grave danger car on ne peut plus le rattraper ! TODO c'est vrai ca ?
          healthValue += coeff * 100;
        }
        healthValue += coeff * Math.max(0, unit.health - 4 * stepsToKillBase);
      } else {
      }
    }
    
    relativeDistsToOpps = 0;
    for (int h=0;h<3;h++) {
      Hero opp = state.oppHeroes[h];
      if (opp.isInFog()) continue;
      if (!opp.isInRange(State.myBase, State.BASE_TARGET_DIST+2000)) continue;

      // A TESTER
      target.copyFrom(opp.pos);
      target.add(50, 0);
      
      int distToHero0 = target.dist(state.hero[0].pos);
      int distToHero1 = target.dist(state.hero[1].pos);
      relativeDistsToOpps += Math.min(distToHero0, distToHero1);
    }
    
    
    
    score -= 100 * relativeDists;
    score -= 1 * relativeDistsToOpps;
    score -= healthValue;
    return score;
  }

  public double finalEval(LightState state) {
    Hero[] hero = state.hero;
    Hero[] oppHeroes = state.oppHeroes;
    double score = 0.0;
    int closestDist = Integer.MAX_VALUE;
    int mobsInBaseZoneCount = 0;
    for (int u = 0; u < state.unitsFE; u++) {
      Unit unit = state.units[u];
      int distToBase = unit.pos.dist(State.myBase);
      closestDist = Math.min(closestDist, distToBase);
      if (distToBase < State.BASE_TARGET_DIST) {
        mobsInBaseZoneCount++;
      }
    }
    if (mobsInBaseZoneCount > 0) {
      score -= 500 * mobsInBaseZoneCount;
      for (int i = 0; i < hero.length; i++) {
        Hero h = hero[i];
        score -= Math.max(0, h.pos.dist(State.myBase) - (closestDist));
      }
      int distToPatrol0 = Math.min(state.hero[0].pos.dist(PATROL[0]), state.hero[1].pos.dist(PATROL[0]));
      int distToPatrol1 = Math.min(state.hero[0].pos.dist(PATROL[1]), state.hero[1].pos.dist(PATROL[1]));
      score -= 0.001 * (distToPatrol0 + distToPatrol1);
    } else {
      // patrol ?
      int distToPatrol0 = Math.min(state.hero[0].pos.dist(PATROL[0]), state.hero[1].pos.dist(PATROL[0]));
      int distToPatrol1 = Math.min(state.hero[0].pos.dist(PATROL[1]), state.hero[1].pos.dist(PATROL[1]));
      score -= 0.001 * (distToPatrol0 + distToPatrol1);
    }
    for (int i = 0; i < oppHeroes.length; i++) {
      Hero h = oppHeroes[i];
      if (h.isInFog())
        continue;
      score += Math.max(0, h.pos.dist(State.myBase));
    }
    for (int i = 0; i < hero.length; i++) {
      Hero h = hero[i];
      score += -Math.max(0, h.pos.dist(State.myBase) - 8_000);
    }
    return score;
  }

}
