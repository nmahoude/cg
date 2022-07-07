package connect4;

public class Minimax {

  private static final int MAX_DEPTH = 6;

  Node fakeNode = new Node();
  
  public int think(State root) {
    NodeCache.reset();
    TranspositionTable.clear();
    
    long start = System.currentTimeMillis();

    int bestCol = -1;
    double bestScore = Double.NEGATIVE_INFINITY;
    for (int col : columnsOrder) {

      if (root.checkAndPut(col, true)) {
        double score = alphaBeta(root, Integer.MIN_VALUE, Integer.MAX_VALUE, false, MAX_DEPTH - 1);
        System.err.println("New way " + col + " => " + score);
        if (score > bestScore) {
          bestScore = score;
          bestCol = col;
        }
        root.remove(col, true);
      }

    }

    long end = System.currentTimeMillis();
    System.err.println("Minimax time : " + (end - start));
    System.err.println("Detected collision : " + TranspositionTable.totalCollisions);
    return bestCol;
  }

  int columnsOrder[] = new int[] { 4, 5, 3, 6, 2, 7, 1, 8, 0 };

  public double alphaBeta(State node, double alpha, double beta, boolean maximizingScore, int depth) {
    if (node.end() || depth == 0) {
      return evaluate(node, depth);
    }
    double bestScore = maximizingScore ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    for (int cc = 0; cc < 9; cc++) {
      int col = columnsOrder[cc];

      if (! node.checkAndPut(col, maximizingScore)) continue;
      
      fakeNode.me = node.mine;
      fakeNode.opp = node.opp;
      Node cached = TranspositionTable.get((int)node.zobrist, fakeNode);
      
      double score;
      if (cached != null) {
        score = cached.score;
      } else {
        score = alphaBeta(node, alpha, beta, !maximizingScore, depth - 1);
        Node tnode = NodeCache.get(node.mine, node.opp, score);
        TranspositionTable.put((int)node.zobrist, tnode);
      }
      node.remove(col, maximizingScore);
      
      if (maximizingScore) {
        bestScore = Math.max(bestScore, score);
        alpha = Math.max(alpha, bestScore);
        if (bestScore > beta)
          break;
      } else {
        bestScore = Math.min(bestScore, score);
        beta = Math.min(beta, bestScore);
        if (bestScore < alpha)
          break;
      }
    }
    return bestScore;
  }

  private double evaluate(State node, int depth) {
    if (node.winner == 0) {
      return 100_000.0 + depth;
    } else if (node.winner == 1) {
      return -100_000.0 - depth;
    } else if (node.winner == 2) {
      return 0.0;
    } else {
      return evaluateNonFinishedBoard(node);
    }
  }

  ThreatAnalyser threatAnalyser = new ThreatAnalyser();

  private double evaluateNonFinishedBoard(State node) {
    threatAnalyser.analyse(node.mine, node.opp);

    double score = 0.0;

    double hisCoeff;
    if (Player.attack) {
      hisCoeff = 0.9;
    } else {
      hisCoeff = 1.1;
    }
    score += 10.0 * (threatAnalyser.myThreats[3] - hisCoeff * threatAnalyser.oppThreats[3]);
    score += 1.0 * (threatAnalyser.myThreats[2] - hisCoeff * threatAnalyser.oppThreats[2]);
    score += 0.1 * (threatAnalyser.myThreats[1] - hisCoeff * threatAnalyser.oppThreats[1]);

    // check double threat vertically
    for (int x = 0; x < 9; x++) {
      for (int y = 0; y < 6; y++) {
        long mask = 0b11L << (7 * x + y);
        if ((threatAnalyser.myThreatMask[3] & mask) == mask) {
          score += 100.0;
          break;
        }
        if ((threatAnalyser.oppThreatMask[3] & mask) == mask) {
          score -= 100.0;
          break;
        }
      }
    }

    return score;
  }

}
