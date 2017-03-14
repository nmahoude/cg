package cvz;

import java.util.Arrays;
import java.util.Scanner;

import cvz.simulation.AGSolution;
import cvz.simulation.Action;
import cvz.simulation.Simulation;
import trigonometry.Point;

public class Player {
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);

    GameState state = new GameState();
    Simulation simulation = new Simulation();
    AGSolution solution = new AGSolution();
    // game loop
    while (true) {
      state.read(in);
      solution.setup(state);
      
      Action bestAction = null;
      double bestScore = -1_000_000_000.0;
      int slice = 360;
      for (int i=0;i<slice;i++) {
        double x = state.ash.p.x + (int)(2000*Math.cos(i * 2*Math.PI / slice));
        double y = state.ash.p.y + (int)(2000*Math.sin(i * 2*Math.PI / slice));
        
        Point p = new Point(x,y);
        if (isInLimit(p)) {
          solution.reset();
          Action a =  new Action(p);
          simulation.simulate(state, solution, Arrays.asList(a));
          System.err.println("ASH move to "+a.p+" score="+solution.energy);
          if (solution.energy > bestScore) {
            bestScore = solution.energy;
            bestAction = a;
          }
        }
      }

      // don"t move
      //bestAction = testAction(state, simulation, solution, bestAction, bestScore, state.ash.p);
      
      
      System.out.println(""+(int)bestAction.p.x+" "+(int)bestAction.p.y); // Your destination coordinates
    }
  }


  private static boolean isInLimit(Point p) {
    if (p.x < 0 || p.y < 0) { return false; }
    if (p.x > 16000 || p.y > 9000) { return false; }
    return true;
  }
}
