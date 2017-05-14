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
  
  public GameState state = new GameState();
  public Robot me = state.robots[0];
  private MoleculeOptimizerNode root;

  void go(Scanner in) {
    state.readScienceProjects(in);

    // game loop
    FSM fsm = new FSM(state, me);
    while (true) {
      initRound();
      for (int i = 0; i < 2; i++) {
        state.robots[i].read(in);
      }
      
      state.readAvailables(in);
      state.readSamples(in);
  
      if (debug) {
        me.carriedSamples.forEach(sample -> sample.debug());
        debugStorage(me);
        debugExpertise(me);
        debugAvailables();
      }
      
      fsm.think();
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

  private void debugAvailables() {
    System.err.print("createAvailable(new int[]{");
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      System.err.print(state.availables[i]);
      if (i != GameState.MOLECULE_TYPE-1) {
        System.err.print(", ");
      } else {
        System.err.print("});");
      }
    }
    System.err.println();
  }

  private void debugExpertise(Robot me) {
    System.err.print("createExpertise(new int[]{");
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      System.err.print(me.expertise[i]);
      if (i != GameState.MOLECULE_TYPE-1) {
        System.err.print(", ");
      } else {
        System.err.print("});");
      }
    }
    System.err.println();
  }

  private void debugStorage(Robot me) {
    System.err.print("createStorage(  new int[]{");
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      System.err.print(me.storage[i]);
      if (i != GameState.MOLECULE_TYPE-1) {
        System.err.print(", ");
      } else {
        System.err.print("});");
      }
    }
    System.err.println();
  }
}
