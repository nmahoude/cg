package c4l;


import java.util.Random;
import java.util.Scanner;

import c4l.entities.Robot;
import c4l.fsm.FSM;
import c4l.molecule.MoleculeOptimizerNode;
import cgutils.random.FastRandom;

/**
 * Bring data on patient samples from the diagnosis machine to the laboratory
 * with enough molecules to produce medicine!
 **/
public class Player {
  public static final Random rand = new FastRandom(17);//System.currentTimeMillis());
  public static Player player = new Player();
  public static boolean debug = true;
  
  private static final int MAX_CARRIED = 10;
  private static final int MAX_CARRIED_SAMPLES = 3;
  public GameState state = new GameState();
  public Robot me = state.robots[0];
  private MoleculeOptimizerNode root;

  void go(Scanner in) {
    state.readScienceProjects(in);

    // game loop
    FSM fsm = new FSM();
    while (true) {
      initRound();
      for (int i = 0; i < 2; i++) {
        state.robots[i].read(in);
      }
      
      state.readAvailables(in);
      state.readSamples(in);
  
      fsm.think(state, me);
    }
  }
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    player.go(in);
  }
  private void initRound() {
    state.initRound();
    root = null;
  }

}
