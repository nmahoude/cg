package connect4;

import java.util.Arrays;

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

     	int y = state.firstEmptyCell(c);
        if (y == 7) continue;
        if (isForbiden(forbidenCols, forbidenColsFE, c)) continue; // this col is forbiden
        
        
        state.put(c, true);
        
        ThreatAnalyser threatAnalyser = new ThreatAnalyser();
        threatAnalyser.analyse(state.mine, state.opp);

        double score = 0.01 - 0.001 * Math.abs(4-c); // the more central the best
        score += -0.01*y;

        double hisCoeff = 1.1;
        score += 5.0 * (threatAnalyser.myThreats[3] - hisCoeff*threatAnalyser.oppThreats[3]); 
        score += 1.0 * (threatAnalyser.myThreats[2] - hisCoeff*threatAnalyser.oppThreats[2]);
        score += 0.1 * (threatAnalyser.myThreats[1] - hisCoeff*threatAnalyser.oppThreats[1]);
        
        for (int dy=-1;dy<=1;dy++) {
          for (int dx=-1;dx<=1;dx++) {
            if (state.getCellPlayerAt(c+dx, y+dy) == 0) {
              score += 1.0;
            }
          }
        }

        
        state.remove(c, true);
        System.err.println("AI - col "+c +" => score = "+score);
        System.err.println("     my threats : "+Arrays.toString(threatAnalyser.myThreats));
        System.err.println("     opp threats : "+Arrays.toString(threatAnalyser.oppThreats));
        if (score > bestScore) {
          bestScore = score;
          bestCol = c;
        }
      }
      
      return bestCol;
    }

  }

	private boolean isForbiden(int[] forbidenCols, int forbidenColsFE, int col) {
		for (int f = 0; f < forbidenColsFE; f++) {
			if (forbidenCols[f] == col) {
				return true;
			}
		}
		return false;
	}

}
