package spring2022.ai.micro;

import spring2022.Action;
import spring2022.Hero;
import spring2022.State;
import spring2022.Unit;

public class Defense {

  /** ennemy in vicinity & one unit to protect 
   * */
  public static Action panicShield(State state, Hero hero) {
    if (state.mana[0] < 10) return Action.WAIT;
    
    for (int u=0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];
      if (unit.hasShield()) continue;
      if (unit.spellCast) continue; // already somebody
      if (!unit.isInRange(State.myBase, State.BASE_VIEW_DIST)) continue;
      if (!unit.isInRange(hero, State.SHIELD_RANGE)) continue;
      

      if (state.isOppInRange(unit.pos, State.SHIELD_RANGE)) {
        System.err.println("Doing panic shield to protect unit ");
        return Action.doShield(unit.id);
      }      
      
    }
    
    return Action.WAIT;
  }

  /**
   * if opp is near & shield less, defend with wind more liberaly
   * @param state
   * @param hero
   * @return
   */
  public static Action defenseWind(State state, Hero hero) {
    if (state.mana[0] < 10) return Action.WAIT;
    
    int monsterInWindRange = 0;
    
    Hero opp = state.getOppInRange(hero.pos, State.WIND_RANGE);
    if (opp == null) return Action.WAIT;
    if (opp.hasShield()) return Action.WAIT;
    
    for (int u= 0;u<state.unitsFE;u++) {
      Unit monster = state.fastUnits[u];

      if (monster.isDead()) continue;
      if (monster.hasShield()) continue;
      if (!monster.isInRange(hero, State.WIND_RANGE))  continue;
      
        monsterInWindRange++;
    }
    
    if (monsterInWindRange > 0) {
      // TODO do better angle ... ?
      System.err.println("Monsters in my base & opp hero too, defense wind");
      return Action.doWind(State.oppBase);
    }
    
    
    return Action.WAIT;
  
  }

}
