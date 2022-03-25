package searchrace;

import java.util.Scanner;

public class Player {
  public static long start;
  
  State state = new State();
  State work = new State();
  
  MC mc = new MC();
  
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
      work.apply(mc.bestAngle, mc.bestThrust, true);
      System.err.println("nex turn : ");
      work.debug();
      if (work.finished) {
        System.err.println("Race is finished ! ");
      }
      
      if (work.checkpointIndex != state.checkpointIndex) {
        System.err.println("Validating checkpoint ************");
        System.err.println(state.checkpointIndex+" => "+work.checkpointIndex);
        System.err.println(State.checkpointX[state.checkpointIndex]+" "+State.checkpointY[state.checkpointIndex]);
        System.err.println(State.checkpointX[work.checkpointIndex]+" "+State.checkpointY[work.checkpointIndex]);
        
        double distToCp2 = 1.0 * (State.checkpointX[work.checkpointIndex] - work.x)*(State.checkpointX[work.checkpointIndex] - work.x) / 600*600+ 
            1.0 * (State.checkpointY[work.checkpointIndex] - work.y)*(State.checkpointY[work.checkpointIndex] - work.y)  / 600*600;
        
        System.err.println("Dist to cp squared : "+distToCp2);
        System.err.println("along x : "+(State.checkpointX[work.checkpointIndex] - work.x));
        System.err.println("along y : "+(State.checkpointY[work.checkpointIndex] - work.y));
        if (distToCp2 < 1.0) {
          System.err.println("inside ! "+(600*600));
        }
      }
      
      System.out.println("EXPERT "+mc.bestAngle+" "+mc.bestThrust);

    }
  }
}
