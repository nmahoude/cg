package hypersonic.montecarlo;

import java.util.Map.Entry;

import hypersonic.Move;
import hypersonic.Simulation;

public class MonteCarlo {

  private static final int SIMULATION_COUNT = 200;
  private static final int MAX_STEPS = 19;
  Node root = new Node();
  
  public void init() {
    root = new Node();
  }
  
  public void simulate(Simulation simulation) {
    root.simulation = new Simulation();
    root.simulation.copyFrom(simulation);
    
    for (int i=SIMULATION_COUNT;i>=0;i--) {
      root.simulate(MAX_STEPS);
    }
  }
  
  public Move findNextBestMove() {
    int bestScore = Integer.MIN_VALUE;
    Move bestMove = null;
    for (Entry<Move, Node> entry : root.childs.entrySet()) {
      int score = entry.getValue().getBestScore();
      System.err.println(""+entry.getKey()+" with score of "+entry.getValue().getScore()+" best is "+score);
      if (score > bestScore) {
        bestScore = score;
        bestMove = entry.getKey();
      }
    }
    return bestMove;
  }
}
