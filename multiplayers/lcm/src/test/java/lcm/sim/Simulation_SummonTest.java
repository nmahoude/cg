package lcm.sim;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

import lcm.BattleMode;
import lcm.State;
import lcm.cards.Location;

public class Simulation_SummonTest extends BattleMode {

  @Test
  public void oneSummonActionWhenAllCardsInHand() throws Exception {
    String input = "30 2 24 25"+NL+"30 1 24 25"+NL+"6 6"+NL+
        "0 1  0 0 4 4 5 -      0 0 0"+NL+
        "0 3  0 0 3 2 4 -G     0 0 0"+NL+
        "0 5  0 0 6 4 7 -G     0 0 0"+NL+
        "0 7  0 0 3 3 3 -G     0 0 0"+NL+
        "0 9  0 0 2 1 2 -GL    0 0 0"+NL+
        "0 11 0 0 6 7 5 -W     0 0 0"+NL+
        "";
    readInput(input);
    
    Action action = Action.summon(state.card(9));

    State result = sim.simulate(state, Arrays.asList(action));
    
    assertThat(result.myHandCardsCount(), is(5));
    assertThat(result.myBoardCardsCount(), is (1));
    assertThat(result.card(9).location, is (Location.MY_BOARD));
  }

  @Test
  public void multipleSummonsActionWhenNotEnoughMana() throws Exception {
    String input = "30 2 24 25"+NL+"30 1 24 25"+NL+"6 6"+NL+
        "0 1  0 0 4 4 5 -      0 0 0"+NL+
        "0 3  0 0 3 2 4 -G     0 0 0"+NL+
        "0 5  0 0 6 4 7 -G     0 0 0"+NL+
        "0 7  0 0 3 3 3 -G     0 0 0"+NL+
        "0 9  0 0 2 1 2 -GL    0 0 0"+NL+
        "0 11 0 0 6 7 5 -W     0 0 0"+NL+
        "";
    readInput(input);
    
    actions.add(Action.summon(state.card(1)));
    actions.add(Action.summon(state.card(3)));
    actions.add(Action.summon(state.card(9)));
    
    State result = sim.simulate(state, actions);
    
    assertThat(result.myHandCardsCount(), is(5));
    assertThat(result.myBoardCardsCount(), is (1));
    assertThat(result.card(9).location, is( Location.MY_BOARD));
  }
  
}
