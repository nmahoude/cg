package calm;

import java.util.Random;
import java.util.Scanner;

import calm.ai.AI;
import calm.state.State;

public class Player {
  public static final String DEBUG_EOL = "\\r\\n\"+";
  public static boolean DEBUG_INPUT = true;
  public static boolean DEBUG_PLANNER = false;
  
  public static final long TIME_LIMIT = 40;
  

  private static State currentState = new State();
  public static Random random = new Random(0); // TODO ThreadLocalRandom
  public static long start;

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);

    State.readWorldInit(in);

    // game loop
    AI ai = new AI();
    while (true) {
      currentState.read(in);
      System.err.println("Time to read input : " +  (System.currentTimeMillis()-start)+" ms");
      
      ai.think(currentState);
      ai.output(currentState);
    }
  }

}
