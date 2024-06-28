package spring2022.ai.microai;

import java.util.concurrent.ThreadLocalRandom;

import spring2022.Action;
import spring2022.Hero;
import spring2022.Pos;
import spring2022.State;
import spring2022.Unit;
import spring2022.ai.MicroAI;

/**
 * Shield buffy mobs going into opp base
 * @author nmahoude
 *
 */
public class ControlMobToOppBase implements MicroAI {
  public static final ControlMobToOppBase i = new ControlMobToOppBase();

  private static final ThreadLocalRandom random = ThreadLocalRandom.current();
  private static final int MIN_RANGE_FROM_BASE = 1000;
  private static final int MAX_RANGE_FROM_BASE = 4000;

  private static final int MINIMUM_HEALTH_TO_CONTROL = 20;

  
  private static final Pos[] TargetDirection = new Pos[] { new Pos(17630, 5000), new Pos(13630, 9000), State.oppBase };
  
  
  @Override
  public Action think(State state, Hero hero) {
    if (state.mana[0] < 10) return Action.WAIT;
    
    for (int u= 0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];

      if (unit.isDead()) continue;
      
      if (!unit.isInRange(hero, State.CONTROL_RANGE)) continue;
      if (unit.health < MINIMUM_HEALTH_TO_CONTROL) continue;   // pas assez de vie
      if (unit.hasShield()) continue; // shield
      if (unit.isInRange(State.oppBase, State.BASE_TARGET_DIST + MIN_RANGE_FROM_BASE)) continue; // trop pret de la base
      if (!unit.isInRange(State.oppBase, State.BASE_TARGET_DIST + MAX_RANGE_FROM_BASE)) continue; // trop loin de la base
      if (unit.nearBase || unit.threatFor == 2) continue; // pas besoin si elle va déjà dans la direction
      
      //TODO check current dir & mobs in quadrant instead of random
      //Pos target = TargetDirection[random.nextInt(3)];
      Pos target = State.oppBase;
      
      return Action.doControl(unit.id, target);
    
    }
    return Action.WAIT;
  }

}
