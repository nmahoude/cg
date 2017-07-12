package tge.minimax;

import java.util.List;

import tge.Player;
import tge.simulation.Action;
import tge.simulation.Simulation;


public class Minimax {
  boolean timeout = false;
  
  Simulation simulation;
  Action bestAction = new Action();
  private int maxDepth;

  public Minimax() {
    simulation = new Simulation();
  }

  public Action think(int maxDepth) {
    this.maxDepth = maxDepth;
    Node node0 = new Node(0);
    
    timeout = false;
    alphaBeta(node0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true);
    if (timeout) {
      return bestAction;
    }
    return bestAction;
  }

  public double alphaBeta(Node node, double alpha, double beta, boolean maximizingScore) {
    // timeout condition
    if (timeout) return Double.NEGATIVE_INFINITY;
    if (System.currentTimeMillis() - Player.startTime > Player.MAX_TIME) {
      timeout = true;
      System.err.println("Timeout");
      return Double.NEGATIVE_INFINITY;
    }

    if (node.depth == maxDepth) {
      return node.evaluate_old();
    }

    double bestScore = maximizingScore ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
    int validActions = 0;
    for (Node child : node.getChildren()) {
      double score;
      if (!simulation.play(child.action)) {
        System.err.println(""+child.action.toOutput()+" impossible");
        continue;
      }
      validActions++;
      score = alphaBeta(child, alpha, beta, !maximizingScore);
      simulation.unplay(child.action);
      child.score = score;
      if (Double.isInfinite(score)) continue;
      if (maximizingScore) {
        if (score > bestScore) {
          bestScore = score;
          if (node.depth == 0) {
            child.action.copyTo(bestAction);
          }
        }
        alpha = Math.max(alpha, bestScore);
      } else {
        bestScore = Math.min(bestScore, score);
        beta = Math.min(beta, bestScore);
      }

      if (beta <= alpha) {
        break;
      }
    }
    if (validActions == 0) {
      return node.evaluate();
    }
    return bestScore;
  }
}
