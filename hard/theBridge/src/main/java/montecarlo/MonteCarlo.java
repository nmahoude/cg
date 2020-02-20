package montecarlo;

import java.util.Map.Entry;

import theBridge.Move;
import theBridge.Simulation;

public class MonteCarlo {

  Node root = new Node();
  
  public void init() {
    root = new Node();
  }
  
  public void simulate(Simulation simulation) {
    root.simulation = simulation;
    for (int i = 0;i<1;i++) {
      root.simulate(5);
    }
  }
  
  public Move findNextBestMove() {
    int bestScore = Integer.MIN_VALUE;
    Move bestMove = Move.Wait;
    for (Entry<Move, Node> entry : root.childs.entrySet()) {
      int score = entry.getValue().getBestScore();
      System.err.println("Root has a child move of : "+entry.getKey()+" with score of "+entry.getValue().getScore()+" best is "+score);
      if (score > bestScore) {
        bestScore = score;
        bestMove = entry.getKey();
      }
    }
    return bestMove;
  }
}
