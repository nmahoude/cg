package lcm.sim;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

import lcm.BattleMode;
import lcm.State;
import lcm.cards.Card;

public class Simulation_ZobristTest extends BattleMode {

  @Test
  public void same_zobrist() throws Exception {
    String input = "30 3 23 25" + NL + "29 2 23 25" + NL + "6 8" + NL +
        "28  1  0 0 2 1 2 -      0 0 1" + NL +
        "28  3  0 0 2 1 2 -      0 0 1" + NL +
        "3   7  0 0 1 2 2 -      0 0 0" + NL +
        "99  11 0 0 3 2 5 G      0 0 0" + NL +
        "11  13 0 0 3 5 2 -      0 0 0" + NL +
        "48  5  1 0 1 1 1 L      0 0 0" + NL +
        "94  9  1 0 2 1 4 G      0 0 0" + NL +
        "94  4  -1 0 2 1 4 G      0 0 0" + NL +
        "";
    state.read(new Scanner(input));

    List<Action> actions = new ArrayList<>();
    actions.add(Action.attack(state.card(5),state.card(4)));
    actions.add(Action.summon(state.card(11)));
    actions.add(Action.attack(state.card(9),Card.opponent));
    actions.add(Action.pass());
    
    State first = new State();
    first.copyFrom(state);
    
    sim.simulate(first, actions);

    State second = new State();
    second.copyFrom(state);
    
    actions = new ArrayList<>();
    actions.add(Action.attack(state.card(5),state.card(4)));
    actions.add(Action.attack(state.card(9),Card.opponent));
    actions.add(Action.summon(state.card(11)));
    actions.add(Action.pass());
    
    sim.simulate(second, actions);
    assertThat(second.getHash(), is(first.getHash()));
  }

  @Test
  public void differentZobrist() throws Exception {
    String input = "30 3 23 25" + NL + "29 2 23 25" + NL + "6 8" + NL +
        "28  1  0 0 2 1 2 -      0 0 1" + NL +
        "28  3  0 0 2 1 2 -      0 0 1" + NL +
        "3   7  0 0 1 2 2 -      0 0 0" + NL +
        "99  11 0 0 3 2 5 G      0 0 0" + NL +
        "11  13 0 0 3 5 2 -      0 0 0" + NL +
        "48  5  1 0 1 1 1 L      0 0 0" + NL +
        "94  9  1 0 2 1 4 G      0 0 0" + NL +
        "94  4  -1 0 2 1 4 G      0 0 0" + NL +
        "";
    state.read(new Scanner(input));

    List<Action> actions = new ArrayList<>();
    actions.add(Action.attack(state.card(5),state.card(4)));
    actions.add(Action.summon(state.card(11)));
    actions.add(Action.attack(state.card(9),Card.opponent));
    actions.add(Action.pass());
    
    State first = sim.simulate(state, actions);

    actions = new ArrayList<>();
    actions.add(Action.attack(state.card(9),state.card(4)));
    actions.add(Action.attack(state.card(5),state.card(4)));
    actions.add(Action.summon(state.card(11)));
    actions.add(Action.pass());
    State second = sim.simulate(state, actions);

    
    Card firstCard4 = first.card(4);
    Card secondCard4 = second.card(4);
    // card is kill diferently but end killed and in graveyard, hash should be the same
    assertThat(firstCard4.getHash(), is(secondCard4.getHash()));
    
    assertThat(second.getHash(), is(not(first.getHash())));
  }
  
}
