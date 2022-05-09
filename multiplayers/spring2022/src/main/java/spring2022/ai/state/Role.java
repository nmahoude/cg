package spring2022.ai.state;

import spring2022.Action;
import spring2022.Hero;
import spring2022.Pos;
import spring2022.State;
import spring2022.ai.micro.Attack;
import spring2022.ai.micro.AttackNearest;
import spring2022.ai.micro.Defense;
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
      final Pos centralPos = Pos.get(14000, 6000);
          
      System.err.println("I don't know how attacking well ....");
      if ((action = Attack.shieldUnit(state, hero)) != Action.WAIT) return action;
      if ((action = Attack.controlToOppBase(state, hero)) != Action.WAIT) return action;
      if ((action = Attack.windToOppBase(state, hero)) != Action.WAIT) return action;

      if (hero.isInRange(centralPos, 800) && state.countUnitNear(hero.pos, 1000) == 0) {
        return changeState(state, hero, ATTACK_PATROLDOWN);
      } 
      if ((action = Action.doMove(centralPos)) != Action.WAIT) return action;
      return Action.WAIT;
    }

  },
  ATTACK_PATROLUP {
    @Override
    public Action think(State state, Hero hero) {
      final Pos patrolPosUP = new Pos(15000, 4000);
      
      Action action;
      if ((action = Attack.controlToOppBase(state, hero)) != Action.WAIT) return action;
      if ((action = Attack.windToOppBase(state, hero)) != Action.WAIT) return action;

      if (state.countUnitNear(hero.pos, 1000) > 0) {
        return changeState(state, hero, ATTACK);
      } else if (hero.isInRange(patrolPosUP, 200)) {
        return changeState(state, hero, ATTACK_PATROLDOWN);
      } else {
        return Action.doMove(patrolPosUP);
      }
    }  
  },
  ATTACK_PATROLDOWN {
    @Override
    public Action think(State state, Hero hero) {
      final Pos patrolPosDown = new Pos(12000 , 7500);
      
      Action action;
      if ((action = Attack.controlToOppBase(state, hero)) != Action.WAIT) return action;
      if ((action = Attack.windToOppBase(state, hero)) != Action.WAIT) return action;

      if (state.countUnitNear(hero.pos, 1000) > 0) {
        return changeState(state, hero, ATTACK);
      } else if (hero.isInRange(patrolPosDown, 200)) {
        return changeState(state, hero, ATTACK_PATROLUP);
      } else {
        return Action.doMove(patrolPosDown);
      }
    
    }  
  },
  DEFEND {
    @Override
    public Action think(State state, Hero hero) {
      Action action;
      
      if ((action = Defense.defenseWind(state, hero)) != Action.WAIT) return action;
      if ((action = new PanicWind().think(state, hero)) != Action.WAIT) return action;
      if ((action = Defense.panicShield(state, hero)) != Action.WAIT) return action;
      
      
      
      if ((action = new AttackNearest().think(state, hero, State.myBase, 7000)) != Action.WAIT) return action;
      return new Tourel().think(state, hero);
    }
  };
  
  
  public abstract Action think(State state, Hero hero);

  Action changeState(State state, Hero hero, Role role) {
    hero.role = role;
    return role.think(state, hero);
  }

}
