<<<<<<< Updated upstream
package lcm;

import java.util.Scanner;
import java.util.SplittableRandom;

import direct_ai.CardPickerV6;
import direct_ai.Picker;
import lcm.ai.AI;
import lcm.ai.eval.Eval8;
import lcm.ai.eval.IEval;
import lcm.ai.mc.MCAI;
import lcm.cards.Card;
import lcm.predictors.AggressorDetector;
import lcm.predictors.Oracle;
import lcm.sim.Cache;
/**
 * test de valeur pour vérifier que c'est en italique ...
 * @author nmahoude
 *
 */
public class PlayerOld {
  public static boolean USE_CUTOFF = true;

  public static boolean DEBUG_MINIMIZATION = false;
  public static boolean DEBUG_CUT_BEAM = false;
  public static boolean DEBUG = false;
  public static boolean SIM_CHECK_LEGAL_ACTIONS = false;
  public static boolean ILLEGAL_ACTION_CRASH = false;
  public static boolean DEBUG_OUTPUT = false;
  public static boolean DEBUG_INPUT = true;
  public static boolean DEBUG_SIM = false;
  public static boolean DEBUG_BEAM = false;
  public static boolean DEBUG_BEAM_RESULT = false;
  public static boolean DEBUG_ORACLE = false;
  public static boolean DEBUG_PICKER = false;
  public static boolean DEBUG_PERF = true;

  static String out = "";
  public static long start;

  public static SplittableRandom random = new SplittableRandom(DEBUG ? 0 : System.currentTimeMillis());
  public static int CARD_CACHE = 640_000;
  public static int NODE_CACHE = 0;
  public static int STATE_CACHE = 20_000;
  public static int MMNODE_CACHE = 0;

  public Scanner in;
  public AI ai;
  public final State state = new State();
  protected IEval eval;
  protected GameMetric metric = new GameMetric();
  protected final Oracle oracle = new Oracle();

  public Picker picker;

  public PlayerOld() {
    picker = new CardPickerV6();
    in = new Scanner(System.in);
  }

  public static void main(String args[]) throws InterruptedException {
    PlayerOld p = new PlayerOld();

    p.init();

    while (true) {
      p.state.read(p.in);

      if (p.state.turn == 0 || p.state.turn == 31) {
        PlayerOld.start += 900; // 1st & 32th turn of battle let more time
      }

      if (p.state.isInDraft()) {
        p.playDraft();
        p.state.me.printActions();
      } else {

        boolean isAggro = new AggressorDetector().aggressor(p.state);
        System.err.println("*************************");
        System.err.println("I should " + (isAggro ? "Aggro" : "Board control"));
        System.err.println("*************************");

        p.playBattle();

        // hide();

        p.ai.output(p.state);
      }
    }
  }
  
  private static void hide() throws InterruptedException {
    while (System.currentTimeMillis() - PlayerOld.start < 80) {
      Thread.sleep(5);
    }
    if (DEBUG_PERF) {
      System.err.println("Shown Perf : " + (System.currentTimeMillis() - start) + " ms");
    }
  }

  public void init() {
    Cache.initCache();
    initZobrists();
  }

  public void playDraft() {
    state.me.pickCard(picker);
    oracle.feed(State.triplet, state.me.actions.get(0).from);
  }

  public void playBattle() {

    updateOracleInBattle();

    metric.updateMetrics(state);
    metric.predictWinner(state);

    chooseStyleOfPlay();

    ai = new MCAI(oracle, eval);
    ai.think(state);

    if (DEBUG_PERF) {
      System.err.println("Real Perf : " + (System.currentTimeMillis() - start) + " ms");
    }
  }

  private void chooseStyleOfPlay() {
    if (state.side == State.FIRST) {
      eval = new Eval8(0.8, 1.2);
    } else {
      eval = new Eval8(1.2, 0.8);
    }
  }

  private void updateOracleInBattle() {
    oracle.update(state);
  }

  public static void initZobrists() {
    // zobrist intiialisation is delegated to the first battle time to avod
    // timeouts
    Card.initZobrist();
    Agent.initZobrist();
  }
}
=======
package lcm;

import java.util.Scanner;
import java.util.SplittableRandom;

import direct_ai.CardPickerV6;
import direct_ai.Picker;
import lcm.ai.beam.BeamSearch;
import lcm.ai.beam.IBeamSearch;
import lcm.ai.eval.Eval8;
import lcm.ai.eval.IEval;
import lcm.cards.Card;
import lcm.predictors.AggressorDetector;
import lcm.predictors.Oracle;
import lcm.sim.Cache;

public class Player {
  public static boolean USE_CUTOFF = true;

  public static boolean DEBUG_MINIMIZATION = false;
  public static boolean DEBUG_CUT_BEAM = false;
  public static boolean DEBUG = false;
  public static boolean SIM_CHECK_LEGAL_ACTIONS = false;
  public static boolean ILLEGAL_ACTION_CRASH = false;
  public static boolean DEBUG_OUTPUT = false;
  public static boolean DEBUG_INPUT = true;
  public static boolean DEBUG_SIM = false;
  public static boolean DEBUG_BEAM = false;
  public static boolean DEBUG_BEAM_RESULT = false;
  public static boolean DEBUG_ORACLE = false;
  public static boolean DEBUG_PICKER = false;
  public static boolean DEBUG_PERF = true;

  static String out = "";
  public static long start;

  public static SplittableRandom random = new SplittableRandom(DEBUG ? 0 : System.currentTimeMillis());
  public static int CARD_CACHE = 640_000;
  public static int NODE_CACHE = 0;
  public static int STATE_CACHE = 20_000;
  public static int MMNODE_CACHE = 0;

  public Scanner in;
  public IBeamSearch ai;
  public final State state = new State();
  protected IEval eval;
  protected GameMetric metric = new GameMetric();
  protected final Oracle oracle = new Oracle();

  public Picker picker;

  public Player() {
    picker = new CardPickerV6();
    in = new Scanner(System.in);
  }

  public static void main(String args[]) throws InterruptedException {
    Player p = new Player();

    p.init();

    while (true) {
      p.state.read(p.in);

      if (p.state.turn == 0 || p.state.turn == 31) {
        Player.start += 900; // 1st turn of battle let more time
      }

      if (p.state.isInDraft()) {
        p.playDraft();
        p.state.me.printActions();
      } else {

        boolean isAggro = new AggressorDetector().aggressor(p.state);
        System.err.println("*************************");
        System.err.println("I should " + (isAggro ? "Aggro" : "Board control"));
        System.err.println("*************************");

        p.playBattle();

        // hide();

        p.ai.output(p.state);
      }
    }
  }

  private static void hide() throws InterruptedException {
    while (System.currentTimeMillis() - Player.start < 80) {
      Thread.sleep(5);
    }
    if (DEBUG_PERF) {
      System.err.println("Shown Perf : " + (System.currentTimeMillis() - start) + " ms");
    }
  }

  public void init() {
    Cache.initCache();
    initZobrists();
  }

  public void playDraft() {
    state.me.pickCard(picker);
    oracle.feed(State.triplet, state.me.actions.get(0).from);
  }

  public void playBattle() {

    updateOracleInBattle();

    metric.updateMetrics(state);
    metric.predictWinner(state);

    chooseStyleOfPlay();

    ai = new BeamSearch(oracle, eval);
    ai.think(state);

    if (DEBUG_PERF) {
      System.err.println("Real Perf : " + (System.currentTimeMillis() - start) + " ms");
    }
  }

  private void chooseStyleOfPlay() {
    if (state.side == State.FIRST) {
      eval = new Eval8(0.8, 1.2);
    } else {
      eval = new Eval8(1.2, 0.8);
    }
  }

  private void updateOracleInBattle() {
    oracle.update(state);
  }

  public static void initZobrists() {
    // zobrist intiialisation is delegated to the first battle time to avod
    // timeouts
    Card.initZobrist();
    Agent.initZobrist();
  }
}
>>>>>>> Stashed changes
