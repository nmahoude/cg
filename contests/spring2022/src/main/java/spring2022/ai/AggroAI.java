package spring2022.ai;

import spring2022.Action;
import spring2022.Player;
import spring2022.State;

public class AggroAI {
  private static final boolean USE_DEFENSE_WHEN_WINNING = false;
  
  private State state = new State();
  private TriAction action = new TriAction();
  private AttackerV2 attacker0 = new AttackerV2();
  private AttackerV2 attacker1 = new AttackerV2();
  private AttackerV2 attacker2 = new AttackerV2();
  private DefenderSimpleAI defender = new DefenderSimpleAI();
  
  public TriAction think(State originalState) {
    
    // let the defense use the mana in priority ... if (action.actions[2].isSpell()) state.mana[0]-=10;

    this.state.copyFrom(originalState);
    if (Player.turn <= 10) {
      // let the defense play without AG to explore (TODO in the evaluation !)
      action.actions[0].copyFrom(Patroler.think(state, state.myHeroes[0]));
      action.actions[1].copyFrom(Patroler.think(state, state.myHeroes[1]));
    } else {
      action.actions[0].copyFrom(defender.think(state, state.myHeroes[0]));
      action.actions[1].copyFrom(attacker1.think(state, state.myHeroes[1]));
    }
    if (action.actions[0].isSpell()) state.mana[0]-=10;
    if (action.actions[1].isSpell()) state.mana[0]-=10;
    
    if (USE_DEFENSE_WHEN_WINNING && originalState.health[0] > originalState.health[1]) {
      Action defend = defender.think(state, state.myHeroes[2]);
      action.actions[2].copyFrom(defend);
    } else {
      action.actions[2].copyFrom(attacker2.think(state, state.myHeroes[2]));
    }

    return action;
  }

  private void debugControl() {
    // on opp heroes
//    for (Hero h : state.oppHeroes) {
//      if (h.isInRange(state.myHeroes[0], State.CONTROL_RANGE)) {
//        action.actions[0].control(h.id, State.oppBase);
//        break;
//      }
//    }
    
    // on mobs
//    for (Unit unit : state.units) {
//      if (Player.turn % 4 == 0 && unit.isInRange(state.myHeroes[0].pos, State.CONTROL_RANGE)) {
//        action.actions[0].control(unit.id, State.oppBase);
//      }
//    }
    
    
  }

}
