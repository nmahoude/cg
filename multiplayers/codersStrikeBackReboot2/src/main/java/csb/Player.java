package csb;

import java.util.Random;
import java.util.Scanner;

import csb.ai.AISolution;
import csb.ai.ag.AGAI;
import trigonometry.Vector;

public class Player {
  private static final Vector DIR_X = new Vector(1,0);
  public static final Random rand = new Random();
  
  public static GameState state = new GameState();
  public static int totalLaps;
  private static int round;
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    totalLaps = in.nextInt();
    state.readCheckpoints(in);
    round = 0;
    
    while (true) {
      round++;

      for (int i = 0; i < 4; i++) {
        int x = in.nextInt(); // x position of your pod
        int y = in.nextInt(); // y position of your pod
        int vx = in.nextInt(); // x speed of your pod
        int vy = in.nextInt(); // y speed of your pod
        int angle = in.nextInt(); // angle of your pod
        int nextCheckPointId = in.nextInt(); // next check point id of your pod
        if (nextCheckPointId == -1) nextCheckPointId = 1;
        
        if (round == 1) {
          // get the angle as it pleases us, it's first turn
          Vector dir = new Vector(state.checkPoints[1].x - x, state.checkPoints[1].y - y).normalize();
          angle = (int) (Math.signum(dir.ortho().dot(DIR_X)) * Math.acos(dir.dot(DIR_X)) * 180 / Math.PI);
        }
        state.pods[i].readInput(x, y, vx, vy, angle, nextCheckPointId);
      }
      long start = System.currentTimeMillis();
      state.backup();

      if (round== 1) {
        System.out.println(""+(int)state.checkPoints[1].x+" "+(int)state.checkPoints[1].y+" BOOST");
        System.out.println(""+(int)state.checkPoints[1].x+" "+(int)state.checkPoints[1].y+" BOOST");
        continue;
      }
      AGAI ag = new AGAI(start+145);
      ag.setState(state);
      
      AISolution best = ag.evolve();
      String[] output = best.output();

      long end= System.currentTimeMillis();
      System.err.println("Time : "+(end-start)+" ms");
      System.out.println(output[0]);
      System.out.println(output[1]);
    }
  }
}
