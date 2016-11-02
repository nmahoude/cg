package hypersonic.montecarlo;

import java.util.Map.Entry;

import hypersonic.Move;
import hypersonic.Simulation;

public class MonteCarlo {

  private static final int SIMULATION_COUNT = 5_000;
  public static final int MAX_DEPTH = 10 ;
  Node root = new Node();
  
  public void init() {
    root = new Node();
  }
  
  public void simulate(Simulation simulation) {
    root.childs.clear();
    root.simulation = new Simulation();
    root.simulation.copyFrom(simulation);
    
    for (int i=SIMULATION_COUNT;i>=0;i--) {
      root.simulate(0);
    }
  }
  
  public Move findNextBestMove() {
    int bestScore = Integer.MIN_VALUE;
    Move bestMove = null;
    for (Entry<Move, Node> entry : root.childs.entrySet()) {
      int score = entry.getValue().getBestScore();
      System.err.println(""+entry.getKey()+"("+entry.getValue().count+" sim) with score of "+entry.getValue().getScore()+" best is "+score);
      if (score > bestScore) {
        bestScore = score;
        bestMove = entry.getKey();
      }
    }
    return bestMove;
  }
}
