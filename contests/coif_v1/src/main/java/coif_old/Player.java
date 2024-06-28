package coif_old;

import java.util.Scanner;

import coif_old.ai.AI;
import coif_old.ai.DefenseAI;
import coif_old.ai.QuickWinAI;
import coif_old.ai.SimpleAi;
import coif_old.ai.Simulation;

public class Player {
  public static boolean DEBUG_INPUT = true;
  public static boolean DEBUG_AI = true;
  
  State state = new State();

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    Player player = new Player();
    player.play(in);
  }

  public void play(Scanner in) {
    state.readInit(in);

    // game loop
    while (true) {
      state.readTurn(in);

      Simulation sim = new Simulation(state);
      
      QuickWinAI quick = new QuickWinAI(state);
      quick.think();
      if (quick.wannaPlay) {
        quick.output();
        continue;
      }
      
      AI defense = new DefenseAI(sim, state);
      defense.think();
      
      AI ai = new SimpleAi(sim, state);
      ai.think();
      
      System.out.println(sim.output());
    }
  }
}
