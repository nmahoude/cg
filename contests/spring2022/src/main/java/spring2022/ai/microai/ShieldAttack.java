package spring2022.ai.microai;

import spring2022.Action;
import spring2022.Hero;
import spring2022.State;
import spring2022.Unit;
import spring2022.ai.MicroAI;

/**
 * Shield buffy mobs going into opp base
 * @author nmahoude
 *
 */
public class ShieldAttack implements MicroAI {
  private static final int MINIMUM_HEALTH_TO_SHIELD = 12;

  
  
  @Override
  public Action think(State state, Hero hero) {
    if (state.mana[0] < 10) return Action.WAIT; // no mana
    
    
    Unit best = null;
    int bestScore = Integer.MIN_VALUE;
    
    for (int u= 0;u<state.unitsFE;u++) {
      Unit monster = state.fastUnits[u];

      if (monster.isDead()) continue;
      if (monster.hasShield()) continue; // on ne met pas de shield sur les morts et les shieldés
      
      if (monster.health < MINIMUM_HEALTH_TO_SHIELD) continue; // on ne le fait que sur les mobs avec pas mal de health
      if (!monster.isInRange(hero.pos, State.SHIELD_RANGE)) continue; // il faut qu'on puisse caster ...
      
      Unit nextTurn = State.future.get(1).findUnitById(monster.id);
      if (!nextTurn.pos.isInRange(State.oppBase, State.BASE_TARGET_DIST)) continue; // doit entrer dans la base au prochain tour où déjà y etre
    
      if (monster.health > bestScore) {
        bestScore = monster.health;
        best = monster;
      }
    }
    
    if (best != null) {
      System.err.println("ATT SHIELD on "+best);
      return Action.doShield(best.id);
    }
    
    return Action.WAIT;

  }

}
