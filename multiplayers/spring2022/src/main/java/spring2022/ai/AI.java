package spring2022.ai;

import java.util.Arrays;

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
  Hero heroes[] = new Hero[3];
  
  public TriAction think(State state) {
    TriAction actions = new TriAction();
    
    updateState(state);

    for (int i=0;i<3;i++) {
      state.myHeroes[i].role = roles[i];
    }

    state.myHeroes[0].role = Role.DEFEND; // always defend
    if (ennemyAttacker) {
      state.myHeroes[1].role = Role.DEFEND_PUSHERS;
    }
    
    if (Player.turn == 40) {
      state.myHeroes[2].role = Role.ATTACK; // switch to attack @ 40
    }

    if (state.mana[0] < 40) {
      state.myHeroes[2].role = Role.FARM; // back to farm
    }
    if (state.myHeroes[2].role == Role.FARM && state.mana[0] > 60) {
      state.myHeroes[2].role = Role.ATTACK; // back to attack
    }
    
    
    // copy heroes
    for (int i=0;i<3;i++) {
      heroes[i] = state.myHeroes[i];
    }
    // sort heroes
    Arrays.sort(heroes, (h1, h2) -> Integer.compare(h1.pos.fastDist(State.myBase), h2.pos.fastDist(State.myBase)));
    
    
    for (int i=0;i<3;i++) {
      Hero hero = heroes[i];

      Action action = think(state, hero);
      if (action.isSpell()) {
        state.mana[0]-= 10;
        if (action.targetEntity != -1) { // only shield & control :/
          Unit unit = state.findUnitById(action.targetEntity);
          if (unit != null) unit.spellCast = true;
        }
      }
      actions.actions[hero.index()].copyFrom(action);
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
    System.err.println("#"+hero.id()+" role: "+hero.role);
    return hero.role.think(state, hero);
  }

}
