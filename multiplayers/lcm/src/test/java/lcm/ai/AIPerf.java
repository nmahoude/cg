package lcm.ai;

import java.util.Scanner;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import lcm.Player;
import lcm.State;
import lcm.ai.beam.BeamSearchV2;
import lcm.ai.eval.Eval8;
import lcm.fixtures.StateFixture;
import lcm.sim.Cache;

public class AIPerf {
  static final String NL = " ";
  State state;
  private BeamSearchV2 ai;

  @BeforeClass
  public static void init() {
    Cache.initCache();
    Player.initZobrists();

  }

  @Before
  public void setup() {
    state = StateFixture.createBattleState();
  }

  @Test
  public void badPerf() throws Exception {
    String input = "30 2 24 25" + NL + "30 1 24 25" + NL + "5 7" + NL +
        "0 3  0 0 1 1 1 -      0 -1 0" + NL +
        "0 5  0 0 1 2 1 G      0 0 0" + NL +
        "0 7  0 0 4 4 5 -      0 0 0" + NL +
        "0 9  0 0 2 1 4 G      0 0 0" + NL +
        "0 11 0 0 2 1 2 GL     0 0 0" + NL +
        "0 1  1 0 1 2 1 D      0 0 0" + NL +
        "0 10 -1 0 1 2 2 -      0 0 0" + NL +
        "";
    state.read(new Scanner(input));

    ai.think(state);

  }

  @Test
  public void perfs() throws Exception {
    String input = 
        "19 11 7 15"+NL+"16 11 8 10"+NL+"5 13"+NL+
        "62  2  0 0 12 12 12 BG     0 0 0"+NL+
        "116 12 0 0 12 8 8 BCGDLW 0 0 0"+NL+
        "50  40 0 0 3 3 2 L      0 0 0"+NL+
        "116 42 0 0 12 8 8 BCGDLW 0 0 0"+NL+
        "32  44 0 0 3 3 2 -      0 0 1"+NL+
        "26  46 0 0 2 3 2 -      0 -1 0"+NL+
        "28  30 1 0 2 1 2 -      0 0 1"+NL+
        "28  36 1 0 2 1 2 -      0 0 1"+NL+
        "67  11 -1 0 6 5 2 -      0 0 0"+NL+
        "28  33 -1 0 2 1 1 -      0 0 0"+NL+
        "32  31 -1 0 3 3 2 -      0 0 1"+NL+
        "13  39 -1 0 4 5 3 -      1 -1 0"+NL+
        "32  37 -1 0 3 3 2 -      0 0 1"+NL+
        "";
    state.read(new Scanner(input));

    long start = System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) {
      Player.start = System.currentTimeMillis();
      ai = new BeamSearchV2(null, new Eval8());
      State tmp = new State();
      tmp.copyFrom(state);
      ai.think(tmp);
    }
    long end = System.currentTimeMillis();
    System.out.println("time : " + (end - start));
  }

  void readInput(String input) {
    state.read(new Scanner(input));
  }
}
