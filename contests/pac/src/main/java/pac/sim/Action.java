package pac.sim;

import pac.agents.PacmanType;

public enum Action {
  MOVE,
  WAIT,

  SPEED,
  SWITCH_PAPER,
  SWITCH_ROCK,
  SWITCH_SCISSOR,
  ;
  

  public static final Action[] SWITCHES = new Action[] { SWITCH_PAPER, SWITCH_ROCK, SWITCH_SCISSOR};

  Action() {
  }
  
  public static Action switchNemesis(PacmanType type) {
    switch(type) {
    case PAPER : return SWITCH_SCISSOR;
    case ROCK : return SWITCH_PAPER;
    case SCISSORS : return SWITCH_ROCK;
    default: return null;
    }
  }
}
