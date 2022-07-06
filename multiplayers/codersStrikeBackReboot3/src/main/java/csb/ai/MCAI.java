package csb.ai;

import csb.Player;
import csb.State;

public class MCAI {
  AGSolution bestSolution = new AGSolution();
  AGSolution solution = new AGSolution();

  public void think(State state) {
    int iter = 0;
    double bestScore = Double.NEGATIVE_INFINITY;
    
    while(true) {
      iter++;
      if (iter >= 256) {
        if (System.currentTimeMillis() - Player.start > 70) {
          break;
        } else {
          iter = 0;
        }
      }
      
      solution.fullRandom();
      state.restore();
      double score = solution.apply(state);
      if (score > bestScore) {
        bestScore = score;
        bestSolution.copyFrom(solution);
      }
    }
    
    
    bestSolution.output(state);
  }

}
