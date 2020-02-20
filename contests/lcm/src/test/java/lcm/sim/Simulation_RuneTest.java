package lcm.sim;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import lcm.BattleMode;
import lcm.State;
import lcm.cards.Card;

public class Simulation_RuneTest extends BattleMode {
  @Test
  public void oppUseA_25_Rune() throws Exception {
    String input = "32 6 20 25"+NL+"28 5 20 25"+NL+"5 8"+NL+
    "0 11 0 0 8 5 5 -GW    0 0 0"+NL+
    "0 13 0 0 2 1 5 -      0 0 0"+NL+
    "0 15 0 2 2 0 -2 -BCGDLW 0 0 0"+NL+
    "0 17 0 0 6 5 6 -B     0 0 0"+NL+
    "0 19 0 0 2 3 2 -G     0 0 0"+NL+
    "0 1  1 0 5 4 6 -G     0 0 0"+NL+
    "0 16 -1 0 1 2 1 -D     0 0 0"+NL+
    "0 18 -1 0 5 6 5 -      0 0 0"+NL+
    "";readInput(input);
    
    actions.add(Action.attack(state.card(1), Card.opponent));

    State result = sim.simulate(state, actions);
    
    assertThat(result.opp.health, is(24));
    assertThat(result.opp.rune, is(4));
  }
  
}
