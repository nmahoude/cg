package minimax;

public class Minimax {

  public int alphaBeta(Node node, int alpha, int beta, boolean maximizingScore) {
    if (node.isEndNode()) {
      return node.evaluate();
    }
    
    int bestScore = maximizingScore ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    for (Node child : node.getChildren()) {
      int score = alphaBeta(child, alpha, beta, !maximizingScore);
      if (maximizingScore) {
        bestScore = Math.max(bestScore, score);
        alpha = Math.max(alpha,  bestScore);
      } else {
        bestScore = Math.min(bestScore, score);
        beta = Math.min(beta,  bestScore);
      }
      if (beta <= alpha) {
        break;
      }
    }
    return bestScore;
  }
  
  public int alphaBetaPositionAware(Node node, int alpha, int beta, boolean maximizingScore) {
    if (node.isEndNode()) {
      return node.evaluate();
    }
    
    int bestScore = maximizingScore ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    for (Node child : node.getChildren(maximizingScore)) {
      int score = alphaBetaPositionAware(child, alpha, beta, !maximizingScore);
      if (maximizingScore) {
        if (score > bestScore) {
          bestScore = score;
          node.setBestChild( child);
        }
        alpha = Math.max(alpha,  bestScore);
      } else {
        if (score < bestScore) {
          bestScore = score;
          node.setBestChild( child);
        }
        beta = Math.min(beta,  bestScore);
      }
      if (beta <= alpha) {
        break;
      }
    }
    return bestScore;
  }

}
