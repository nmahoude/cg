package spring2022.ai.state;

import spring2022.Action;
import spring2022.Hero;
import spring2022.Pos;
import spring2022.State;
import spring2022.ai.micro.AttackNearest;
import spring2022.ai.micro.InitPatrol;
import spring2022.ai.micro.PanicWind;
import spring2022.ai.micro.Tourel;

public enum Role {
  FARM() {
    @Override
    public Action think(State state, Hero hero) {
      Action action;

      if ((action = new AttackNearest().think(state, hero)) != Action.WAIT) return action;
      if ((action = new InitPatrol().think(state, hero)) != Action.WAIT) return action;
      return Action.WAIT;
    }
  }, 
  ATTACK {
    @Override
    public Action think(State state, Hero hero) {
      Action action;
      System.err.println("I don't know how attacking ....");
      if (true) return Action.doMove(Pos.get(14000, 6000));
      return Action.WAIT;
    }
  },
  DEFEND {
    @Override
    public Action think(State state, Hero hero) {
      Action action;
      
      if ((action = new PanicWind().think(state, hero)) != Action.WAIT) return action;
      
      
      
      if ((action = new AttackNearest().think(state, hero, State.myBase, 7000)) != Action.WAIT) return action;
      return new Tourel().think(state, hero);
    }
  };
  
  
  public abstract Action think(State state, Hero hero);
  
}
