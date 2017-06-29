package ww.think;

import ww.GameState;
import ww.sim.Move;
import ww.sim.Simulation;

public class Think {
  Simulation simulation = new Simulation();
  GameState state;
  Move bestMove = new Move(null);
  private int maxDepth;

  public Think(GameState state) {
    this.state = state;
  }

  public Move think(int maxDepth) {
    this.maxDepth = maxDepth;
    Node.state = state;
    Node node0 = new Node(0);
    alphaBeta(node0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true);
    return bestMove;
  }

  public double alphaBeta(Node node, double alpha, double beta, boolean maximizingScore) {
    // timeout condition
    if (System.currentTimeMillis() - GameState.startTime > GameState.MAX_TIME) {
      bestMove = null;
      return Double.NEGATIVE_INFINITY;
    }
    
    if (node.depth == maxDepth) {
      return node.evaluate();
    }

    double bestScore = maximizingScore ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
    boolean oneValidMove = false;
    for (Node child : node.getChildren()) {
      if (child.move == null) {
        return alphaBeta(child, alpha, beta, !maximizingScore);
      } else {
        simulation.simulate(child.move, true);
        if (!child.move.isValid()) {
          continue;
        }
        oneValidMove = true;
        double score = alphaBeta(child, alpha, beta, !maximizingScore);
        if (maximizingScore) {
          if (score > bestScore) {
            bestScore = score;
            if (node.depth == 0) {
              child.move.copyTo(bestMove);
            }
          }
          alpha = Math.max(alpha, bestScore);
        } else {
          bestScore = Math.min(bestScore, score);
          beta = Math.min(beta, bestScore);
        }

        simulation.undo(child.move);

        if (beta <= alpha) {
          break;
        }
      }
    }
    if (!oneValidMove) {
      return node.evaluate();
    }
    return bestScore;
  }
}
