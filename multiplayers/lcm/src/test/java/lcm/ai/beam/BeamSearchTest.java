package lcm.ai.beam;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import lcm.BattleMode;
import lcm.Player;
import lcm.ai.eval.Eval8;
import lcm.ai.eval.Eval9;
import lcm.cards.Card;
import lcm.cards.Location;
import lcm.fixtures.CardFixture;
import lcm.predictors.Oracle;

public class BeamSearchTest extends BattleMode {

  private IBeamSearch bs;

  @Before
  public void setup() {
    super.setup();
    Player.DEBUG_BEAM = true;
    Player.DEBUG_CUT_BEAM = true;
    Player.USE_CUTOFF = false;

    Player.start = System.currentTimeMillis();
    bs = new BeamSearch(new Oracle(), new Eval9());
  }

  @Test
  public void test() throws Exception {
    String input =         
        "30 4 21 25"+NL+"27 3 22 25"+NL+"6 10"+NL+
        "54  1  0 0 3 2 2 L      0 0 0"+NL+
        "62  5  0 0 12 12 12 BG     0 0 0"+NL+
        "50  7  0 0 3 3 2 L      0 0 0"+NL+
        "49  13 0 0 2 1 2 GL     0 0 0"+NL+
        "50  15 0 0 3 3 2 L      0 0 0"+NL+
        "96  17 0 0 2 3 2 G      0 0 0"+NL+
        "29  3  1 0 2 2 1 -      0 0 0"+NL+
        "99  11 1 0 3 2 5 G      0 0 0"+NL+
        "65  6  -1 0 2 2 2 -      0 0 0"+NL+
        "99  10 -1 0 3 2 5 G      0 0 0"+NL+
        "";
    state.read(new Scanner(input));
    bs.think(state);
    bs.output(state);
  }
  
  private void hisHandCard(int instanceId, int id) {
    Card card = CardFixture.card(instanceId, id);
    card.location = Location.HIS_HAND;
    state.addCard(card);
  }
}
