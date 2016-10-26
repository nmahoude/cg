package theBridge;

import java.util.Arrays;
import java.util.Scanner;

import montecarlo.MonteCarlo;

public class Player {
  static MonteCarlo mc = new MonteCarlo();
  
  static Simulation simulation = new Simulation();
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int M = in.nextInt(); // the amount of motorbikes to control
    int V = in.nextInt(); // the minimum amount of motorbikes that must survive

    
    String L0 = in.next(); // L0 to L3 are lanes of the road. A dot character .
                           // represents a safe space, a zero 0 represents a
                           // hole in the road.
    String L1 = in.next();
    String L2 = in.next();
    String L3 = in.next();

    simulation.init(L0, L1, L2, L3);
    
    // game loop
    while (true) {
      simulation.reinit();
      int S = in.nextInt(); // the motorbikes' speed
      simulation.updateSpeed(S);
      for (int i = 0; i < M; i++) {
        int X = in.nextInt(); // x coordinate of the motorbike
        int Y = in.nextInt(); // y coordinate of the motorbike
        int A = in.nextInt(); // indicates whether the motorbike is activated
                              // "1" or detroyed "0"
        simulation.updateMoto(X, Y, A);
      }
      mc.init();
      mc.simulate(simulation);
      Move move = mc.findNextBestMove();
      
      System.out.println(move.toString().toUpperCase());
    }
  }
}