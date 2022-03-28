package searchrace;

import java.util.Scanner;

import searchrace.ag.AG;

public class Player {
  public static long start;
  public static int turn;
  public static int MAX_SPEED = 200;
  
  State state = new State();
  State work = new State();
  
  AG ai = new AG();
  AISolution tentativeSolution = new AISolution();
  
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
      
//      System.err.println("*** Reapplying last one decaled ***");
//      work.copyFrom(state);
//      AISolution lastOne = new AISolution();
//      lastOne.reinitFromLast(ai.best);
//      lastOne.debug(work);

      
      System.err.println("*** Optimize ***");
      
      ai.think(state);
      
      
      double distToCheckPoint = Math.sqrt(Math.pow((State.checkpointX[state.checkpointIndex] - state.x), 2) + Math.pow((State.checkpointY[state.checkpointIndex] - state.y), 2)); 
      if (distToCheckPoint < 1500 && ! ai.best.hasCrossedCheckpoint()) {
        Player.MAX_SPEED = 0;
        System.err.println("OUPS ! STOP ! ");
      } else if (state.angleToNextCheckpoint() < 0.90 && Math.abs(state.vx) <= 0.01 && Math.abs(state.vy) <= 0.01) {
        Player.MAX_SPEED = 0;
      } else {
        Player.MAX_SPEED  = 200;
      }
      System.err.println("MAX_SPEED is "+MAX_SPEED);

      
      System.err.println("*** Best sol : ***");
      work.copyFrom(state);
      ai.best.debug(work);
      
      
      if (Math.abs(state.vx) <= 0.1 && Math.abs(state.vy) <= 0.1) {
        // try moving around
        for (int i=0;i<10;i++) {
          tentativeSolution.angles[i] = 10;
          tentativeSolution.thrusts[i] = 0;
        }
        work.copyFrom(state);
        System.err.println("Trying only turning");
        tentativeSolution.debug(work);
      }
      
      System.err.println(" *** Collision debug ");
      work.copyFrom(state);
      work.apply(ai.bestAngle, ai.bestThrust, true);
      
      
      
      System.out.println("EXPERT "+ai.bestAngle+" "+ai.bestThrust);

    }
  }
}
