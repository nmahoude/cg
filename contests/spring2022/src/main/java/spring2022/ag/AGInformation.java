package spring2022.ag;

import spring2022.Hero;
import spring2022.Player;
import spring2022.State;
import spring2022.Unit;

public class AGInformation {

  boolean deadlyDanger = false;
  
  boolean controlAttacker = false;
  boolean[] canControl = new boolean[3];
  public int unitToControl;
  
  Hero oppAttacker = null;

  public boolean isInCorner[] = new boolean[3]; // angle better in 270-359 to go back
  public boolean shouldShield[] = new boolean[3]; // angle better in 270-359 to go back

  
  
  
  
  public void update(State state) {
    deadlyDanger = false;
    controlAttacker = false;
    for (int i=0;i<3;i++) {
      isInCorner[i] = false;
      canControl[i] = false;
      shouldShield[i] = false;
    }

    
    for (int i=0;i<2;i++) {
      if (state.myHeroes[i].hasShield()) continue; // pas de shield si on a déjà un shield
      if (!state.myHeroes[i].isInRange(State.myBase, State.BASE_TARGET_DIST + 1000)) continue; // pas de shield en dehors de la base

      if (Player.danger[i]) {
        // shouldShield[i] = true;
      }

      for (int u=0;u<3;u++) {
        if (state.myHeroes[i].isInRange(state.oppHeroes[u], State.SHIELD_RANGE)) {
          //shouldShield[i] = true;
        }
      }
      
    }

    
    
    
    // orienté les angles quand on est dans le coin
//    for (int i=0;i<2;i++) {
//      if (state.myHeroes[i].isInRange(State.myBase, 1000)) isInCorner[i] = true;
//    }
    
    
    /*
     * TODO redo work ?
     */
    /*
    int mobsInBase = 0;
    for (Unit unit : state.units) {
      if (unit.hasShield()) continue;
      if (unit.isInRange(State.myBase, State.BASE_TARGET_DIST + 1000)) {
        mobsInBase++;
      }
    }

    if( mobsInBase > 0) {
      for (Hero opp : state.oppHeroes) {
        if (opp.hasShield()) continue;
        if (!opp.isInRange(State.myBase, State.BASE_TARGET_DIST + 1000)) continue;
        for (int i = 0; i < 3; i++) {
          Hero me = state.myHeroes[i];
          if (opp.isInRange(me, State.SHIELD_RANGE)) {
            canControl[i] = true;
            controlAttacker = true;
            unitToControl = opp.id;
          }
        }
      }
      if (controlAttacker) {
        System.err.println(">>>>>>>>>>>>>>>>>>>>");
        System.err.println(">>   MIND CONTROL <<");
        System.err.println(">>>>>>>>>>>>>>>>>>>>");
      }
    }
    */
    
    for (int u= 0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];

      if (unit.isDead())
        continue;
      if (unit.isInRange(State.myBase, 1500)) {
        System.err.println("DEADLY DANGER");
        deadlyDanger = true;
      }
    }
    
    oppAttacker = null;
    for (Hero hero : state.oppHeroes) {
      if (hero.pos.dist(State.myBase) < State.BASE_TARGET_DIST+2000) {
        oppAttacker = hero;
        // TODO what if plusieurs attaquants ?
      }
    }
    
  }
}
