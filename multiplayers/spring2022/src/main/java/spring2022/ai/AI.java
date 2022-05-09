package spring2022.ai;

import spring2022.Action;
import spring2022.Hero;
import spring2022.Player;
import spring2022.State;
import spring2022.TriAction;
import spring2022.Unit;
import spring2022.ai.micro.InitPatrol;
import spring2022.ai.state.Role;

public class AI {
  public boolean ennemyAttacker = false;
  
  Role roles[] = new Role[] { Role.FARM, Role.FARM, Role.FARM };
  
  
  public TriAction think(State state) {
    TriAction actions = new TriAction();
    
    updateState(state);

    for (int i=0;i<3;i++) {
      state.myHeroes[i].role = roles[i];
    }

    state.myHeroes[0].role = Role.DEFEND; // always defend
    if (ennemyAttacker) {
      state.myHeroes[1].role = Role.DEFEND;
    }
    
    if (Player.turn == 40) {
      state.myHeroes[2].role = Role.ATTACK; // switch to attack @ 40
    }
    
    for (int i=0;i<3;i++) {
      Hero hero = state.myHeroes[i];

      Action action = think(state, hero);
      if (action.isSpell() && action.targetEntity != -1) { // only shield & control :/
        Unit unit = state.findUnitById(action.targetEntity);
        if (unit != null) unit.spellCast = true;
      }
      actions.actions[i].copyFrom(action);
    }
    
    // save roles
    for (int i=0;i<3;i++) {
      roles[i] = state.myHeroes[i].role;
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
