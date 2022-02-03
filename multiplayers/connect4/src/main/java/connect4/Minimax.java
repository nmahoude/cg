package connect4;

public class Minimax {

  private static final int MAX_DEPTH = 5;
  private int bestCol;
  public double[] forbidenScore = new double[10];
  public int[] forbidenCols = new int[10];
  public int forbidenColsFE;
  private double currentForbidenScore;

  public int think(State root) {
    StateCache.reset();
    bestCol = -1;
    root.childsFE = 0;
    forbidenColsFE = 0;
    
    long start = System.currentTimeMillis();
    int value = alphaBeta(root, 0, 0, true, MAX_DEPTH);
    long end = System.currentTimeMillis();
    System.err.println("Minimax time : "+(end-start));
    
    return bestCol;
  }
  
  public int alphaBeta(State node, int alpha, int beta, boolean maximizingScore, int depth) {
    if (node.end()) {
      if (node.winner == 0) {
        return Integer.MAX_VALUE;
      } else if (node.winner == 1) {
        currentForbidenScore = Math.min(currentForbidenScore, MAX_DEPTH - depth);
        return -11_000;
      } else {
        return 0; // draw or not finished
      }
    }
    if (depth == 0) {
      return 0; // abandon
    }
    
    int bestScore = maximizingScore ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    for (int col=0;col<9;col++) {
      if (depth == MAX_DEPTH) currentForbidenScore = Double.MAX_VALUE;
      
      if (!node.canPutOn(col)) {
        continue;
      }

      State child = StateCache.getFrom(node);
      node.childs[node.childsFE++] = child;

      child.put(col, maximizingScore);
      int score = alphaBeta(child, alpha, beta, !maximizingScore, depth-1);
      
      if (maximizingScore) {
        bestScore = Math.max(bestScore, score);
        alpha = Math.max(alpha,  bestScore);
      } else {
        bestScore = Math.min(bestScore, score);
        beta = Math.min(beta,  bestScore);
      }

      if (depth == MAX_DEPTH) {
//        System.err.println("for col "+col+" score is "+score);
        if (score == Integer.MAX_VALUE) {
          bestCol = col;
        }
        if (score <= 10_000) {
          forbidenScore[forbidenColsFE] = currentForbidenScore;
          forbidenCols[forbidenColsFE++] = col;
        }
      }

    }
    return bestScore;
  }
  
}
