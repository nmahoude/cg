package lcm.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

import lcm.BattleMode;
import lcm.State;
import lcm.ai.eval.Eval5;

/**
 * Test to try out why some moves ....
 * 
 * @author nmahoude
 *
 */
public class DebugSituationTest extends BattleMode {

  @Test
  public void summonWrongCreature() throws Exception {
    String input = "28 4 21 25" + NL + "30 4 22 25" + NL + "4 10" + NL +
        "53  2  0 0 4 1 1 CL     0 0 0" + NL +
        "7   4  0 0 2 2 2 W      0 0 0" + NL +
        "52  6  0 0 4 2 4 L      0 0 0" + NL +
        "121 8  0 1 2 0 3 -      0 0 1" + NL +
        "34  10 0 0 5 3 5 -      0 0 1" + NL +
        "51  12 0 0 4 3 5 L      0 0 0" + NL +
        "48  18 0 0 1 1 1 L      0 0 0" + NL +
        "7   9  -1 0 2 2 2 -      0 0 0" + NL +
        "48  15 -1 0 1 1 1 L      0 0 0" + NL +
        "99  7  -1 0 3 2 5 G      0 0 0" + NL +
        "";
    state.read(new Scanner(input));
  }

  private void resetCardScore(State state) {
    for (int i=0;i<state.cardsFE;i++) {
      state.cards[i].score = -1;
    }
  }

  @Test
  public void WhyOhWhy() throws Exception {
    String input = "17 7 14 15" + NL + "34 7 19 25" + NL + "2 15" + NL +
        "44  4  0 0 6 3 7 DL     0 0 0" + NL +
        "116 8  0 0 12 8 8 BCGDLW 0 0 0" + NL +
        "32  14 0 0 3 3 2 -      0 0 1" + NL +
        "116 20 0 0 12 8 8 BCGDLW 0 0 0" + NL +
        "29  26 0 0 2 2 1 -      0 0 1" + NL +
        "69  28 0 0 3 4 4 B      0 0 0" + NL +
        "88  30 0 0 5 4 4 C      0 0 0" + NL +
        "67  32 0 0 6 5 5 W      0 -2 0" + NL +
        "51  6  1 0 4 3 5 L      0 0 0" + NL +
        "97  3  -1 0 3 1 1 G      0 0 0" + NL +
        "51  11 -1 0 4 3 2 L      0 0 0" + NL +
        "18  17 -1 0 4 7 2 -      0 0 0" + NL +
        "27  7  -1 0 2 2 2 -      0 0 0" + NL +
        "37  21 -1 0 6 5 7 -      0 0 1" + NL +
        "38  19 -1 0 1 1 3 D      0 0 0" + NL +
        "";
    state.read(new Scanner(input));
    Eval5 eval = new Eval5();

    // what he did
    List<Action> actions = new ArrayList<>();
    actions.add(Action.summon(state.card(30)));
    actions.add(Action.attack(state.card(30), state.card(3)));
    actions.add(Action.summon(state.card(26)));
    actions.add(Action.attack(state.card(6), state.card(19)));

    State result = sim.simulate(state, actions);
    double score = eval.eval(result, false);

    // what I would have done
    actions = new ArrayList<>();
    actions.add(Action.summon(state.card(30)));
    actions.add(Action.summon(state.card(26)));
    actions.add(Action.attack(state.card(30), state.card(3)));
    actions.add(Action.attack(state.card(6), state.card(21)));

    State result2 = sim.simulate(state, actions);
    double score2 = eval.eval(result2, false);

    // assertThat(score2- score > 0 , is(true));
  }

}
