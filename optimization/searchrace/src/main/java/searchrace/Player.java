package searchrace;

import java.util.Scanner;

import searchrace.ag.AG;

public class Player {
  public static long start;
  
  State state = new State();
  State work = new State();
  
  AG mc = new AG();
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    new Player().play(in);
  }

  private void play(Scanner in) {

    State.readInit(in);

    // game loop
    while (true) {
      state.read(in);
      start = System.currentTimeMillis();
      
      state.debug();

      mc.think(state);
  
      work.copyFrom(state);
      work.apply(mc.bestAngle, mc.bestThrust);
      System.err.println("nex turn : ");
      work.debug();
      if (work.checkpointIndex != state.checkpointIndex) {
        System.err.println("Validating checkpoint ************");
      }
      
      System.out.println("EXPERT "+mc.bestAngle+" "+mc.bestThrust);

    }
  }
}
