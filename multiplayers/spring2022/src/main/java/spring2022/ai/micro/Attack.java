package spring2022.ai.micro;

import spring2022.Action;
import spring2022.Hero;
import spring2022.Pos;
import spring2022.State;
import spring2022.Unit;

public class Attack {

  public static Action shieldUnit(State state, Hero hero) {
    if (state.mana[0] < 10) return Action.WAIT;
    
    for (int u=0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];
      
      if (unit.hasShield()) continue;
      if (!unit.isInRange(hero, State.SHIELD_RANGE)) continue;
      if (!unit.isInRange(State.oppBase, State.BASE_TARGET_DIST)) continue;
      
      if (unit.health < 15) continue;
      
      return Action.doShield(unit.id);
      
    }  
    
    return Action.WAIT;
  }  
  
  
  public static Action windToOppBase(State state, Hero hero) {
    if (state.mana[0] < 10) return Action.WAIT;
    
    for (int u=0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];
      if (unit.hasShield()) continue;
      if (unit.health < 13) continue;
      
      if (!unit.isInRange(hero, State.WIND_RANGE))  continue; // too far
      
      Pos pos = getWindProjectionPos(unit, State.oppBase);
      if (pos.fastDist(State.oppBase) > State.BASE_TARGET_DIST) continue; // will not fall in opp base dist
      
      System.err.println("wind projection for unit "+unit+" is "+pos);
      // check if units in near the positions
      int unitCount = State.future.get(1).countUnitNear(pos, 800);
      if (unitCount > 0) {
        System.err.println("WindToOppBase : can't wind "+unit+" beacuse too many mobs near "+pos);
        continue;
      }
      return Action.doWind(State.oppBase);
    }

    return Action.WAIT;
  }

  private static Pos getWindProjectionPos(Unit unit, Pos target) {
    
    Pos nextPos = new Pos();
    nextPos.copyFrom(unit.pos);
    nextPos.moveToward(target, State.WIND_PUSH_DISTANCE);
    
    return nextPos;
  }

  public static Action controlToOppBase(State state, Hero hero) {
    if (state.mana[0] < 10) return Action.WAIT;
    
    for (int u=0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];
      if (unit.hasShield()) continue;
      if (unit.health < 15) continue;
      
      if (unit.isInRange(State.oppBase, State.BASE_TARGET_DIST)) continue; // already in base
      if (unit.willAttackOppBase()) continue; // will reach the base eventually
      
      if (unit.isInRange(hero, State.CONTROL_RANGE)) {
        return Action.doControl(unit.id, State.oppBase);
      }
    }

    return Action.WAIT;
  }
  
  
}
