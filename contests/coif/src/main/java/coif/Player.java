package coif;

import java.util.Scanner;

import coif.ai.CreateUnitAI;
import coif.ai.ExplorerAI;
import coif.ai.LongCutAI;
import coif.ai.QuickWinAI;
import coif.ai.RushToHQAI;
import coif.ai.SimpleDefenseAI;
import coif.ai.Simulation;

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
      
      QuickWinAI quickwin = new QuickWinAI(sim, state);
      quickwin.think();
      if (quickwin.wannaPlay) {
        System.out.println(sim.output());
        continue;
      }
      new LongCutAI(sim, state).think();
      //new StationDefenseAI(sim, state).think();
      new ExplorerAI(sim, state).think();
      //new DefenseAI(sim, state).think();
      new RushToHQAI(sim, state).think();
      new CreateUnitAI(sim,state).think();
      //new BuildMineAI(sim, state).think();
      new SimpleDefenseAI(sim, state).think();
      
      System.err.println("Output .... ");
      System.out.println(sim.output());
    }
  }
}
