package spring2022.ai;

import spring2022.Action;
import spring2022.Hero;
import spring2022.Player;
import spring2022.State;
import spring2022.TriAction;
import spring2022.ai.micro.InitPatrol;
import spring2022.ai.state.Role;

public class AI {
  public boolean ennemyAttacker = false;
  
  public TriAction think(State state) {
    TriAction actions = new TriAction();
    
    updateState(state);

    for (int i=0;i<3;i++) {
      state.myHeroes[i].role = Role.FARM;
    }

    state.myHeroes[0].role = Role.DEFEND;
    if (ennemyAttacker) {
      state.myHeroes[1].role = Role.DEFEND;
    }
    
    if (Player.turn > 40) {
      state.myHeroes[2].role = Role.ATTACK;
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
    System.err.println("#"+hero.id+" role: "+hero.role);
    return hero.role.think(state, hero);
  }

}
