package gitc;

import java.util.Scanner;

import gitc.ag.AG;
import gitc.ag.AGParameters;
import gitc.entities.Factory;
import gitc.simulation.Simulation;

/**
 *
 * Only WAIT, for debug of current Player (need to submit this crap)
 */
public class Player_WAIT {
  private static Scanner in;
  private static int turn = 0;
  public static AG ag;
  public static GameState gameState;
  public static Simulation simulation;
  
  public static void main(String[] args) {
    System.err.println("Ready to go");
    gameState = new GameState();
    simulation = new Simulation(gameState);
    in = new Scanner(System.in);
    
    setupAG();
    gameState.readSetup(in);
    while(true) {
      gameState.read(in);
      long start = System.nanoTime();

      // find best action possible
      long timeLimit = start + turn != 0 ? 85_000_000L : 800_000_000L;
      //AGSolution solution = ag.evolution(timeLimit);

      //System.out.println(solution.output());
      
      /*
       * Some debug information
       */
      System.err.println("My forces : ["+gameState.units[0] + "/ prod:"+gameState.production[0]+"]" );
      System.err.println("Op forces : ["+gameState.units[1] + "/ prod:"+gameState.production[1]+"]" );

      /*
       * WAIT
       */
      String bestOutput ="WAIT";
      System.out.println(bestOutput);

      cleanUp();
    }
  }

  private static void setupAG() {
    AGParameters parameters = new AGParameters();
    ag = new AG(simulation, parameters);
  }
  
  private static void cleanUp() {
    turn ++;
  }
}
