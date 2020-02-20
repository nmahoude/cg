package offline;

import java.util.Scanner;

import lcm.Agent;
import lcm.Player;
import lcm.State;
import lcm.ai.beam.BeamSearch;
import lcm.ai.beam.IBeamSearch;
import lcm.ai.eval.Eval8;
import lcm.cards.Card;
import lcm.predictors.AggressorDetector;
import lcm.sim.Cache;

public class PlayerRef extends Player {
  
  
  public PlayerRef() {
    picker = new CardPickerRef();
    in = new Scanner(System.in);
  }
  
  public static void main(String args[]) throws InterruptedException {
    Player p = new Player();
    
    p.init();
    
    while (true) {
      p.state.read(p.in);

      if (p.state.turn == 0 || p.state.turn == 31) {
        Player.start+=900; // 1st turn of battle let more time
      }

      if (p.state.isInDraft()) {
        p.playDraft();
        p.state.me.printActions();
      } else {
        
        boolean isAggro = new AggressorDetector().aggressor(p.state);
        System.err.println("*************************");
        System.err.println("I should "+(isAggro ? "Aggro" : "Board control"));
        System.err.println("*************************");
        
        p.playBattle();
        
        //hide();

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
      eval = new EvalRef(0.8, 1.2);
    } else {
      eval = new EvalRef(1.2, 0.8);
    }
  }

  private void updateOracleInBattle() {
    oracle.update(state);
  }

  public static void initZobrists() {
    // zobrist intiialisation is delegated to the first battle time to avod timeouts
    Card.initZobrist();
    Agent.initZobrist();
  }
}
