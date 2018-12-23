package lcm.sim;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Test;

import lcm.BattleMode;
import lcm.cards.Card;
import lcm.cards.Location;

public class Simulation_FromOpp extends BattleMode {

  @Test
  public void fromHim_directAttackThenSummon() throws Exception {
    String input = "30 2 24 25"+NL+"30 2 24 25"+NL+"5 8"+NL+
        "18  1  0 0 4 7 4 -      0 0 0"+NL+
        "100 3  0 0 3 1 6 G      0 0 0"+NL+
        "5   5  0 0 2 4 1 -      0 0 0"+NL+
        "23  7  0 0 7 8 8 -      0 0 0"+NL+
        "17  9  0 0 4 4 5 -      0 0 0"+NL+
        "100 11 0 0 3 1 6 G      0 0 0"+NL+
        "48  2  -1 0 1 1 1 L      0 0 0"+NL+
        "0   6  -2 0 2 4 1 -      0 0 0"+NL+
        "";
        state.read(new Scanner(input));
            
        
        sim.simulate(state, Action.attack(state.card(2), Card.opponent), false);
        
        assertThat(state.me.health, is(29));
        
        sim.simulate(state, Action.summon(state.card(6)));
        
        assertThat(state.card(6).location, is(Location.MY_BOARD));
  }
}
