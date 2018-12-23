package lcm;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Before;

import lcm.cards.Card;
import lcm.fixtures.StateFixture;
import lcm.sim.Action;
import lcm.sim.Cache;
import lcm.sim.Simulation;

public class BattleMode {
  protected static final String NL = " ";
  protected State state;
  protected Simulation sim;
  protected List<Action> actions;
  
  @Before
  public void setup() {
    Cache.initCache();
    Card.initZobrist();
    Agent.initZobrist();
    
    
    Player.SIM_CHECK_LEGAL_ACTIONS = true;
    Player.STATE_CACHE = 100;

    Player.ILLEGAL_ACTION_CRASH = false;
    Player.DEBUG_INPUT = false; // don't reoutput the state

    state = StateFixture.createBattleState();
    sim = new Simulation();
    actions = new ArrayList<Action>();
  }
  
  protected void readInput(String input) {
    state.read(new Scanner(input));
  }
}
