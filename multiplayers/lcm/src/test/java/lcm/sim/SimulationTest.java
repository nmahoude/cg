package lcm.sim;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Ignore;
import org.junit.Test;

import lcm.BattleMode;
import lcm.State;
import lcm.cards.Card;
import lcm.cards.Location;

public class SimulationTest extends BattleMode {

  @Test
  public void readCards() {
    String input =
        // players
        "30 4 22 25" + NL + "29 3 22 25" + NL + "8 8" + NL +
        // Cards
            "0 1  0 0 4 4 5 -      0 0 0" + NL +
            "0 5  0 0 6 4 7 -G     0 0 0" + NL +
            "0 7  0 0 3 3 3 -G     0 0 0" + NL +
            "0 11 0 0 6 7 5 -W     0 0 0" + NL +
            "0 13 0 0 2 1 1 -CDW   0 0 0" + NL +
            "0 15 0 0 5 5 6 -      0 0 0" + NL +
            "0 9  1 0 2 1 2 -GL    0 0 0" + NL +
            "0 3  1 0 3 2 4 -G     0 0 0" + NL +
            "";

    Scanner in = new Scanner(input);
    state.read(in);

    int hCount = 0;
    int bCount = 0;
    for (int i = 0; i < state.cardsFE; i++) {
      Card card = state.cards[i];
      if (card.location == Location.MY_BOARD)
        bCount++;
      if (card.location == Location.MY_HAND)
        hCount++;
    }
    assertThat(hCount, is(6));
    assertThat(bCount, is(2));
  }

  @Test
  @Ignore
  public void directAttack_dontAlterInitialCards() throws Exception {
    String input = "30 3 23 25" + NL + "30 2 23 25" + NL + "7 7" + NL +
        "0 1  0 0 4 4 5 -      0 0 0" + NL +
        "0 3  0 0 3 2 4 -G     0 0 0" + NL +
        "0 5  0 0 6 4 7 -G     0 0 0" + NL +
        "0 7  0 0 3 3 3 -G     0 0 0" + NL +
        "0 11 0 0 6 7 5 -W     0 0 0" + NL +
        "0 13 -1 0 2 2 2 -   0 0 0" + NL +
        "0 9  1 0 2 1 1 -    0 0 0" + NL +
        "";
    readInput(input);

    actions.add(Action.attack(state.card(9), state.card(13)));

    State result = sim.simulate(state, actions);

    assertThat(result.card(9).location, is(Location.GRAVEYARD));
    assertThat(result.card(13).defense, is(1));

    assertThat(state.card(9).location, is(Location.MY_BOARD));
    assertThat(state.card(13).defense, is(2));
  }

  @Test
  public void directAttack() throws Exception {
    String input = "30 3 23 25" + NL + "30 2 23 25" + NL + "7 7" + NL +
        "0 1  0 0 4 4 5 -      0 0 0" + NL +
        "0 3  0 0 3 2 4 -G     0 0 0" + NL +
        "0 5  0 0 6 4 7 -G     0 0 0" + NL +
        "0 7  0 0 3 3 3 -G     0 0 0" + NL +
        "0 11 0 0 6 7 5 -W     0 0 0" + NL +
        "0 13 0 0 2 1 1 -CDW   0 0 0" + NL +
        "0 9  1 0 2 1 2 -GL    0 0 0" + NL +
        "";
    readInput(input);

    actions.add(Action.attack(state.card(9), Card.opponent));

    State result = sim.simulate(state, actions);

    assertThat(result.card(9).location, is(Location.MY_BOARD));
    assertThat(result.opp.health, is(29));
  }

  @Test
  public void multipleAttacks() throws Exception {
    String input = "30 5 21 25" + NL + "26 4 22 25" + NL + "8 9" + NL +
        "0 5  0 0 6 4 7 -G     0 0 0" + NL +
        "0 7  0 0 3 3 3 -G     0 0 0" + NL +
        "0 11 0 0 6 7 5 -W     0 0 0" + NL +
        "0 13 0 0 2 1 1 -CDW   0 0 0" + NL +
        "0 15 0 0 5 5 6 -      0 0 0" + NL +
        "0 17 0 0 3 4 4 -B     0 0 0" + NL +
        "0 9  1 0 2 1 2 -GL    0 0 0" + NL +
        "0 3  1 0 3 2 4 -G     0 0 0" + NL +
        "0 1  1 0 4 4 5 -      0 0 0" + NL +
        "";
    readInput(input);

    actions.add(Action.attack(state.card(9), Card.opponent));
    actions.add(Action.attack(state.card(3), Card.opponent));
    actions.add(Action.attack(state.card(1), Card.opponent));

    State result = sim.simulate(state, actions);

    assertThat(result.opp.health, is(19));
  }

  @Test
  public void drain() throws Exception {
    String input = "30 2 24 25" + NL + "30 1 24 25" + NL + "5 7" + NL +
        "0 1  0 0 5 4 6 -G     0 0 0" + NL +
        "0 3  0 2 0 -1 -1 -      0 0 0" + NL +
        "0 5  0 2 2 0 -2 -BCGDLW 0 0 0" + NL +
        "0 9  0 0 3 3 3 -G     0 0 0" + NL +
        "0 11 0 0 8 5 5 -GW    0 0 0" + NL +
        "0 7  1 0 1 2 1 -D     0 0 0" + NL +
        "0 4  -1 0 1 2 1 -D     0 0 0" + NL +
        "";
    readInput(input);

    actions.add(Action.attack(state.card(7), Card.opponent));
    State result = sim.simulate(state, actions);

    assertThat(result.me.health, is(32));
    assertThat(result.opp.health, is(28));
  }

  @Test
  public void illegalMove() throws Exception {
    String input = "30 3 23 25" + NL + "30 2 23 25" + NL + "7 7" + NL +
        "0 3  0 0 2 2 2 W      0 0 0" + NL +
        "0 5  0 0 3 2 2 CD     0 0 0" + NL +
        "0 7  0 0 2 3 2 G      0 0 0" + NL +
        "0 9  0 2 3 0 0 BCGDLW 0 0 1" + NL +
        "0 11 0 0 3 2 5 G      0 0 0" + NL +
        "0 13 0 0 3 2 2 CD     0 0 0" + NL +
        "0 1  1 0 2 2 3 G      0 0 0" + NL +
        "";
    state.read(new Scanner(input));
    List<Action> actions = new ArrayList<>();
    actions.add(Action.summon(state.card(5)));
    actions.add(Action.attack(state.card(5), Card.opponent));

    State result = sim.simulate(state, actions);

    assertThat(result.card(5).canAttack, is(false));
  }

  @Test
  public void updateCachesOfCardsCount() throws Exception {
    String input = "30 3 23 25" + NL + "30 2 23 25" + NL + "6 8" + NL +
        "12  1  0 0 3 2 5 -      0 0 0" + NL +
        "69  3  0 0 3 4 4 B      0 0 0" + NL +
        "103 5  0 0 4 3 6 G      0 0 0" + NL +
        "100 7  0 0 3 1 6 G      0 0 0" + NL +
        "39  11 0 0 1 2 1 D      0 0 0" + NL +
        "39  13 0 0 1 2 1 D      0 0 0" + NL +
        "55  9  1 0 2 0 5 G      0 0 0" + NL +
        "49  2  -1 0 2 1 2 GL     0 0 0" + NL +
        "";
    state.read(new Scanner(input));

    List<Action> actions = new ArrayList<>();
    actions.add(Action.summon(state.card(11)));
    actions.add(Action.attack(state.card(9), state.card(2)));

    for (Action action : actions) {
      sim.simulate(state, action);
    }

    assertThat(state.me.getBoardCardsCount(), is(1));
  }

  @Test
  public void breakHis25Rune() throws Exception {
    String input = "30 6 20 25" + NL + "29 5 20 25" + NL + "6 10" + NL +
        "64  3  0 0 2 1 1 GW     0 0 0" + NL +
        "20  9  0 0 5 8 2 -      0 0 0" + NL +
        "151 15 0 2 5 0 -99 BCGDLW 0 0 0" + NL +
        "36  17 0 0 6 4 4 -      0 0 2" + NL +
        "139 19 0 1 4 0 0 LW     0 0 0" + NL +
        "9   1  1 0 3 3 3 -      0 0 0" + NL +
        "7   13 1 0 2 2 2 -      0 0 0" + NL +
        "4   11 1 0 2 1 3 -      0 0 0" + NL +
        "109 7  1 0 5 5 6 -      0 0 0" + NL +
        "21  20 -1 0 5 6 5 -      0 0 0" + NL +
        "";
    state.read(new Scanner(input));
    
    sim.simulate(state, Action.attack(state.card(7), Card.opponent));
    
    assertThat(state.opp.rune, is(4));
  }
  
  @Test
  public void breakAllRunesOfP2() throws Exception {
    String input = "30 6 20 25" + NL + "29 5 20 25" + NL + "6 10" + NL +
        "64  3  0 0 2 1 1 GW     0 0 0" + NL +
        "20  9  0 0 5 8 2 -      0 0 0" + NL +
        "151 15 0 2 5 0 -99 BCGDLW 0 0 0" + NL +
        "36  17 0 0 6 4 4 -      0 0 2" + NL +
        "139 19 0 1 4 0 0 LW     0 0 0" + NL +
        "9   1  1 0 3 3 3 -      0 0 0" + NL +
        "7   13 1 0 2 2 2 -      0 0 0" + NL +
        "4   11 1 0 2 1 3 -      0 0 0" + NL +
        "109 7  1 0 5 5 6 -      0 0 0" + NL +
        "21  20 -1 0 5 6 5 -      0 0 0" + NL +
        "";
    state.read(new Scanner(input));
    state.opp.nextTurnDraw = 10;
    state.opp.deck = 2;
    state.opp.rune = 5;
        
    sim.simulate(state, Action.attack(state.card(7), Card.opponent));
    sim.simulate(state, Action.endTurn());
    
    assertThat(state.opp.rune, is(0));
    assertThat(state.opp.health, is(0));
  }
  
  @Test
  public void break2RunesOfP2() throws Exception {
    String input = "30 6 20 25" + NL + "29 5 20 25" + NL + "6 10" + NL +
        "64  3  0 0 2 1 1 GW     0 0 0" + NL +
        "20  9  0 0 5 8 2 -      0 0 0" + NL +
        "151 15 0 2 5 0 -99 BCGDLW 0 0 0" + NL +
        "36  17 0 0 6 4 4 -      0 0 2" + NL +
        "139 19 0 1 4 0 0 LW     0 0 0" + NL +
        "9   1  1 0 3 3 3 -      0 0 0" + NL +
        "7   13 1 0 2 2 2 -      0 0 0" + NL +
        "4   11 1 0 2 1 3 -      0 0 0" + NL +
        "109 7  1 0 5 5 6 -      0 0 0" + NL +
        "21  20 -1 0 5 6 5 -      0 0 0" + NL +
        "";
    state.read(new Scanner(input));
    state.opp.nextTurnDraw = 3;
    state.opp.deck = 1;
    state.opp.rune = 4;
    state.opp.health = 31;
    
    sim.simulate(state, Action.endTurn());
    
    assertThat(state.opp.rune, is(2));
    assertThat(state.opp.health, is(10));
  }
  
  @Test
  public void break2RunesOfP1() throws Exception {
    String input = "30 6 20 25" + NL + "29 5 20 25" + NL + "6 10" + NL +
        "64  3  0 0 2 1 1 GW     0 0 0" + NL +
        "20  9  0 0 5 8 2 -      0 0 0" + NL +
        "151 15 0 2 5 0 -99 BCGDLW 0 0 0" + NL +
        "36  17 0 0 6 4 4 -      0 0 2" + NL +
        "139 19 0 1 4 0 0 LW     0 0 0" + NL +
        "9   1  1 0 3 3 3 -      0 0 0" + NL +
        "7   13 1 0 2 2 2 -      0 0 0" + NL +
        "4   11 1 0 2 1 3 -      0 0 0" + NL +
        "109 7  1 0 5 5 6 -      0 0 0" + NL +
        "21  20 -1 0 5 6 5 -      0 0 0" + NL +
        "";
    state.read(new Scanner(input));
    state.me.nextTurnDraw = 3;
    state.me.deck = 1;
    state.me.rune = 4;
    state.me.health = 31;
    
    sim.simulate(state, Action.endTurn(), false);
    
    assertThat(state.me.rune, is(2));
    assertThat(state.me.health, is(10));
  }
}
