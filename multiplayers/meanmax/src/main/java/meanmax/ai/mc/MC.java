package meanmax.ai.mc;

import meanmax.Game;
import meanmax.Player;
import meanmax.ai.ag.AGSolution;
import meanmax.ai.eval.EvalV2;
import meanmax.simulation.Simulation;

public class MC {
  public int MAX_TIME = 35_000_000;
  public EvalV2 eval = new EvalV2();
      
  public AGSolution bestSolution = new AGSolution(eval); // not used directly, will be swapped in place
  private AGSolution solution = new AGSolution(eval);

  Simulation simulation = new Simulation();

  double patience[] = new double[] { 1.0, 0.9, 0.8, 0.6, 0.4, 0.2, 0.1 };

  public void think(Player player) {
    setup();
    
    while (System.nanoTime() - Game.start < MAX_TIME) {
      
      oneGeneration();
      
      if (solution.energy > bestSolution.energy) {
        AGSolution tmp = bestSolution;
        bestSolution = solution;
        solution = tmp;
      }
    }
  }

  public void setup() {
    bestSolution.energy = Double.NEGATIVE_INFINITY;
  }

  public void oneGeneration() {
    solution.energy = 0.0;
    solution.random();
    for (int j=0;j<AGSolution.DEPTH;j++) {
      solution.crashTestDummies(j);
      simulation.simulate(solution.actions[j]);
      solution.energy = patience[j] * eval.eval();
    }
    Game.restore();
  }

  public void output() {
    //System.err.println("ai "+simulations+" simulations in "+ (end - Game.start) + " ms");
    if (Game.DEBUG_AI) {
      debugSolution(bestSolution);
    }
    bestSolution.output();
  }

  private void debugSolution(AGSolution solution) {
    System.err.println("best solution : ");
    for (int j=0;j<AGSolution.DEPTH;j++) {
      simulation.simulate(solution.actions[j]);
      for (int i=0;i<3;i++) {
        solution.actions[j][i].debug();
      }
      eval.eval();
      eval.debug();
    }
    System.err.println("total score : "+Game.players[0].score);
    System.err.println("total rage : "+Game.players[0].rage);
    
    Game.restore();
  }
}
