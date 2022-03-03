package botg;

import java.util.ArrayList;
import java.util.List;

import botg.units.Hero;
import botg.units.Unit;

public class SimpleAI {
  List<Unit> deadUnits = new ArrayList<>();
  
  
  List<Action> bestActions = new ArrayList<>();


  private State state;
  private Agent my;
  private Agent opp;



  public void think(State state) {
    bestActions.clear();
    
    this.state = state;
    deadUnits.clear();
    
    if (state.roundType < 0) {
      decideHero(state);
      return;
    }
      my = state.me;
      opp = state.opp;
      
      int actionIndex = 0;
      for (Hero hero : my.heroes) {
        bestActions.add(hero.think(state, actionIndex));
      }      
  }

  private void decideHero(State state2) {
    if (state.roundType == -2) {
      bestActions.add(Action.CHOOSE_IRONMAN);
    } else {
      bestActions.add(Action.CHOOSE_STRANGE);
    }
    return;
  }

  public void output() {
    for (Action action : bestActions) {
      System.out.println(action);
    }
  }
}
