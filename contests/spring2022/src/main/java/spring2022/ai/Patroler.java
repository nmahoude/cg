package spring2022.ai;

import spring2022.Action;
import spring2022.Hero;
import spring2022.Player;
import spring2022.Pos;
import spring2022.State;
import spring2022.ai.microai.AttackNearest;
import spring2022.ai.microai.InitPatrol;
import spring2022.ai.microai.TakeOpportunity;

public class Patroler {
  private static final AttackNearest ATTACK_NEAREST = new AttackNearest();
  private static final InitPatrol INIT_PATROL = new InitPatrol();
  private static State state;
  private static Hero hero;

  private static Pos[] targets = new Pos[] { 
      new Pos(6500, 7000),
      new Pos(8000, 1750),
      new Pos(9000, 4500),
      
  };
  
  
  public static Action think(State state, Hero hero) {
    Patroler.state = state;
    Patroler.hero = hero;

    Action action;
    if ((action = TakeOpportunity.i.think(state, hero)) != Action.WAIT) return action;

    
    if (Player.turn < 10) {
        if ((action = INIT_PATROL.think(state, hero)) != Action.WAIT) return action;
    }
    
    if ((action = ATTACK_NEAREST.think(state, hero)) != Action.WAIT) return action;
    if ((action = shouldReturnToBase()) != Action.WAIT) return action;
    if ((action = shouldPatrol()) != Action.WAIT) return action;
    return Action.WAIT;
  }
  
  
  private static Action shouldPatrol() {
    Action action = new Action();
    
    int offset = hero.id >= 3 ? (hero.id - 3) : hero.id;
    
    action.moveTo(targets[offset]);
    return action;
  }


  private static Action shouldReturnToBase() {
    return Action.WAIT;
  }

}
