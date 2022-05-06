package spring2022.ai;

import spring2022.Action;
import spring2022.Hero;
import spring2022.Player;
import spring2022.State;
import spring2022.TriAction;
import spring2022.ai.micro.AttackNearest;
import spring2022.ai.micro.InitPatrol;
import spring2022.ai.micro.Tourel;
import spring2022.ai.state.AIState;

public class AI {
  public boolean ennemyAttacker = false;
  
  public TriAction think(State state) {
    TriAction actions = new TriAction();
    
    updateState(state);

    for (int i=0;i<3;i++) {
      state.myHeroes[i].role = AIState.FARM;
    }
    if (ennemyAttacker) {
      state.myHeroes[0].role = AIState.DEFEND;
    }
    
    for (int i=0;i<3;i++) {
      Hero hero = state.myHeroes[i];

      Action action = think(state, hero);
      
      actions.actions[i].copyFrom(action);
    }
    
    return actions;
  }

  private void updateState(State state) {
    if (!ennemyAttacker) {
      for (int i=0;i<3;i++) {
        Hero opp = state.oppHeroes[i];
        if (opp.isInFog()) continue;
        if (opp.isInRange(State.myBase, 8500)) {
          ennemyAttacker = true;
        }
      }
    }    
    System.err.println("Ennemy attacker : "+ennemyAttacker);
    
  }

  private Action think(State state, Hero hero) {
    Action action;
    
    if (Player.turn < 10) {
      if ((action = new InitPatrol().think(state, hero)) != Action.WAIT) return action;
    }
    
    if (hero.role == AIState.DEFEND) {
      return defend(state, hero);
    } else if (hero.role == AIState.FARM) {
      if ((action = new AttackNearest().think(state, hero)) != Action.WAIT) return action;
      if ((action = new InitPatrol().think(state, hero)) != Action.WAIT) return action;
      return Action.WAIT;
    } else {
      System.err.println("Unknwown role "+hero.role);
      return Action.WAIT;
    }
  }

  private Action defend(State state, Hero hero) {
    Action action;
    if ((action = new AttackNearest().think(state, hero, 6000)) != Action.WAIT) return action;
    return new Tourel().think(state, hero);
  }
}
