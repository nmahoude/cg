package ooc.ai.start;

import ooc.FreeCellsDetector2;
import ooc.P;
import ooc.State;

public class BasicStartPositionAI {
  public P start = P.get(7, 7);

  public void outputStartingPos(State state) {
    int bestX = -1, bestY = -1;
    double bestScore = Double.NEGATIVE_INFINITY;
    for (int y=0;y<15;y++) {
      for (int x=0;x<15;x++) {
        P current = P.get(x, y);
        if (state.map.isIsland(current)) continue;
        double score = 0.0;
        
        int freeCells = new FreeCellsDetector2().countFreeCellInDir(state, current);
        score += freeCells;
        score -= 0.01 * Math.abs(x - 7) + Math.abs(y -7);
        
        if (score > bestScore) {
          bestScore = score;
          bestX = x;
          bestY = y;
        }
      }
    }
    System.out.println(String.format("%d %d", bestX, bestY));
  }

}
