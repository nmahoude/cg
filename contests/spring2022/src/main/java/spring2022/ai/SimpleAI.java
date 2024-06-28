package spring2022.ai;

import spring2022.Action;
import spring2022.Player;
import spring2022.State;
import spring2022.ag.AG;

public class SimpleAI {
  private static final boolean USE_DEFENSE_WHEN_WINNING = false;
  
  private State state = new State();
  private TriAction action = new TriAction();
  private AttackerV2 attacker = new AttackerV2();
  private DefenderSimpleAI defender = new DefenderSimpleAI();
  
  AG ag = new AG();
  
  public TriAction think(State originalState) {
    
    // let the defense use the mana in priority ... if (action.actions[2].isSpell()) state.mana[0]-=10;

    this.state.copyFrom(originalState);
    if (Player.turn <= 10) {
      // let the defense play without AG to explore (TODO in the evaluation !)
      action.actions[0].copyFrom(Patroler.think(state, state.myHeroes[0]));
      action.actions[1].copyFrom(Patroler.think(state, state.myHeroes[1]));

      TriAction best = ag.think(state); // warmup AG
    } else {
      // let the AG play
      TriAction best = ag.think(state);
      action.actions[0].copyFrom(best.actions[0]);
      action.actions[1].copyFrom(best.actions[1]);
    }

    
    this.state.copyFrom(originalState);
    if (action.actions[0].isSpell()) state.mana[0]-=10;
    if (action.actions[1].isSpell()) state.mana[0]-=10;
    
    if (USE_DEFENSE_WHEN_WINNING && originalState.health[0] > originalState.health[1]) {
      Action defend = defender.think(state, state.myHeroes[2]);
      action.actions[2].copyFrom(defend);
    } else {
      Action attack2 = attacker.think(state, state.myHeroes[2]);
      action.actions[2].copyFrom(attack2);
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
