package lcm.ai.eval;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import lcm.fixtures.CardFixture;

public class Eval8Test extends EvalTest {

  Eval8 eval;

  @Before
  public void setup() {
    super.setup();
    eval = new Eval8();

    eval1 = eval;
    eval2 = new Eval8();
  }

  @Test
  @Ignore
  public void cardValues() throws Exception {
    for (int i = 1; i < 160; i++) {
      System.out.println(eval.evaluateCard(CardFixture.card(i, -1)));
    }
  }

  @Test
  public void test() throws Exception {
    eval1 = new Eval8(1.1, 0.8)
        .withHisHealthBonus(2.0);
    
    eval2 = new Eval8(1.1, 0.8)
        .withHisHealthBonus(2.0);
    
    String input =         
        "26 9 16 25"+NL+"26 8 17 25"+NL+"2 7"+NL+
        "80  3  0 0 8 8 8 BG     0 0 1"+NL+
        "61  15 0 0 9 10 10 -      0 0 0"+NL+
        "80  19 0 0 8 8 8 BG     0 0 1"+NL+
        "23  27 0 0 7 8 8 -      0 0 0"+NL+
        "54  23 1 0 3 2 2 L      0 0 0"+NL+
        "54  24 -1 0 3 2 1 L      0 0 0"+NL+
        "80  16 -1 0 8 8 8 BG     0 0 1"+NL+
        "";
    state.read(new Scanner(input));
    debug = true;

    double real = doActions1(
        SUMMON(15,-1), ATTACK(23,16));

    double whatIThink = doActions2(
        ATTACK(23,16));

    assertThat(whatIThink, greaterThan(real));
  }

}
