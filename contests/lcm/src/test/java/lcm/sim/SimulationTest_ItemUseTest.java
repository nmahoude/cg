package lcm.sim;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import lcm.BattleMode;
import lcm.State;
import lcm.cards.Location;

public class SimulationTest_ItemUseTest extends BattleMode {

  @Test
  public void itemUseWithoutTargetDestruction() throws Exception {
    String input = "32 3 23 25"+NL+"28 2 23 25"+NL+"5 7"+NL+
        "0 1  0 0 5 4 6 -G     0 0 0"+NL+
        "0 5  0 2 2 0 -2 -BCGDLW 0 0 0"+NL+
        "0 9  0 0 3 3 3 -G     0 0 0"+NL+
        "0 11 0 0 8 5 5 -GW    0 0 0"+NL+
        "0 13 0 0 2 1 5 -      0 0 0"+NL+
        "0 7  1 0 1 2 1 -D     0 0 0"+NL+
        "0 6  -1 0 1 1 3 -D     0 0 0"+NL+
        ""; readInput(input);
    
    actions.add(Action.use(state.card(5), state.card(6)));
    
    State result = sim.simulate(state, actions);
    
    assertThat(result.hisBoardCardsCount(), is(1));
    assertThat(result.card(6).defense, is(3-2));
    
    assertThat(result.card(5).location, not(is(Location.MY_BOARD)));
    assertThat(result.card(5).location, not(is(Location.MY_HAND)));
  }
  
  @Test
  public void itemUseWithTargetDestruction() throws Exception {
    String input = "32 6 20 25"+NL+"28 5 20 25"+NL+"5 8"+NL+
        "0 11 0 0 8 5 5 -GW    0 0 0"+NL+
        "0 13 0 0 2 1 5 -      0 0 0"+NL+
        "0 15 0 2 2 0 -2 -BCGDLW 0 0 0"+NL+
        "0 17 0 0 6 5 6 -B     0 0 0"+NL+
        "0 19 0 0 2 3 2 -G     0 0 0"+NL+
        "0 1  1 0 5 4 6 -G     0 0 0"+NL+
        "0 16 -1 0 1 2 1 -D     0 0 0"+NL+
        "0 18 -1 0 5 6 5 -      0 0 0"+NL+
        ""; readInput(input);
    
    actions.add(Action.use(state.card(15), state.card(16)));
    
    State result = sim.simulate(state, actions);
    
    assertThat(result.card(16).location, is(Location.GRAVEYARD));
  }
}
