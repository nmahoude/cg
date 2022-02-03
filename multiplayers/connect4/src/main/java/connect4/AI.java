package connect4;

import cgutils.random.FastRandom;

public class AI {
  private static FastRandom random = new FastRandom(0); // System.currentTimeMillis());

  private State state;

  public int think(State root) {
    this.state = root;

    Minimax max = new Minimax();
    int col = max.think(state);
    if (col != -1) {
      System.err.println("Found on minimax ! " + col);
    } else {
      if (max.forbidenColsFE != 0) {
        System.err.println("Minimax found forbiden cols :");
        for (int i = 0; i < max.forbidenColsFE; i++) {
          System.err.println(max.forbidenCols[i] +" with score "+max.forbidenScore[i]);
        }
      }

      col = findCol(max.forbidenCols, max.forbidenColsFE);
    }

    if (col == -1) {
      // no solution found, check in best forbiden cells (with the loss further away)
      double bestScore = Double.NEGATIVE_INFINITY;
      for (int i=0;i<max.forbidenColsFE;i++) {
        if (max.forbidenScore[i] > bestScore) {
          bestScore = max.forbidenScore[i];
          col = max.forbidenCols[i];
        }
      }
      System.err.println("Seems like we lost :(  best shot is "+col);
    }

    
    
    return col;
  }

  public int findCol() {
    return findCol(null, 0);
  }

  public int findCol(int forbidenCols[], int forbidenColsFE) {
    
    double bestScore = Double.NEGATIVE_INFINITY;
    int bestCol = -1;
    
    if (state.possibleColumnsFE == 0) {
      System.err.println("Possible columns is 0 ???");
      state.debugColumns();
      
      return 0; // draw ?
    } else {
      // make the better choice !
      for (int c=0;c<9;c++) {

        if (state.firstEmptyCell(c) == 7) continue;
        //System.err.println("Col height is "+state.firstEmptyCell(c));
        
        boolean usable = true;
        for (int f=0;f<forbidenColsFE;f++) {
          if (forbidenCols[f] == c) {
            usable = false;
            break;
          }
        }
        if (!usable) continue; // this col is forbiden
        
        int y = state.firstEmptyCell(c);
        double score = 1.0 - 0.01 * Math.abs(4-c); // the more central the best
        
        for (int dy=-1;dy<=1;dy++) {
          for (int dx=-1;dx<=1;dx++) {
            if (state.getCellPlayerAt(c+dx, y+dy) == 0) {
              score += 1;
            }
          }
        }
        
        if (score > bestScore) {
          bestScore = score;
          bestCol = c;
        }
      }
      
      return bestCol;
    }

  }

}
