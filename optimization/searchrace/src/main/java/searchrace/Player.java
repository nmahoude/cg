package searchrace;

import java.util.Scanner;

import searchrace.ag.AG;

public class Player {
  public static long start;
  public static int turn;
  
  State state = new State();
  State work = new State();
  
  AG ai = new AG();
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    new Player().play(in);
  }

  private void play(Scanner in) {

    State.readInit(in);

    // game loop
    while (true) {
      turn++;
      
      state.read(in);
      start = System.currentTimeMillis();
      if (turn == 1) {
        start -= 500;
      }
      
      state.debug();

      ai.think(state);
  
      
      work.copyFrom(state);
      ai.best.debug(work);
      
      
      System.err.println("************");
      work.copyFrom(state);
      work.apply(ai.bestAngle, ai.bestThrust, true);
      
      
      
      System.out.println("EXPERT "+ai.bestAngle+" "+ai.bestThrust);

    }
  }
}
