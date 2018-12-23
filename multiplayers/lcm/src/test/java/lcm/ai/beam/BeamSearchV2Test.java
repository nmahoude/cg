package lcm.ai.beam;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import lcm.BattleMode;
import lcm.Player;
import lcm.ai.eval.Eval8;
import lcm.cards.Card;
import lcm.cards.Location;
import lcm.fixtures.CardFixture;
import lcm.predictors.Oracle;

public class BeamSearchV2Test extends BattleMode {

  private BeamSearchV2 bs;

  @Before
  public void setup() {
    super.setup();
    Player.DEBUG_BEAM = true;
    Player.DEBUG_CUT_BEAM = true;
    Player.USE_CUTOFF = false;

    Player.start = System.currentTimeMillis();
    bs = new BeamSearchV2(new Oracle(), new Eval8());
  }

  @Test
  public void test() throws Exception {
    String input =         
        "30 3 23 25"+NL+"30 2 23 25"+NL+"6 8"+NL+
        "147 1  0 2 2 0 -1 -      0 0 1"+NL+
        "82  3  0 0 7 5 5 BDW    0 0 0"+NL+
        "129 5  0 1 4 2 5 -      0 0 0"+NL+
        "33  7  0 0 4 4 3 -      0 0 1"+NL+
        "95  9  0 0 2 2 3 G      0 0 0"+NL+
        "29  11 0 0 2 2 1 -      0 0 1"+NL+
        "33  13 0 0 4 4 3 -      0 0 1"+NL+
        "47  4  -1 0 2 1 5 D      0 0 0"+NL+
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
