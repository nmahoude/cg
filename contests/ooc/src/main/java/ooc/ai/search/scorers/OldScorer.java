package ooc.ai.search.scorers;

import ooc.FreeCellsDetector2;
import ooc.Oracle;
import ooc.P;
import ooc.Player;
import ooc.State;
import ooc.ai.search.Node;

public class OldScorer extends Scorer {
  P target;
  
  public OldScorer(P target) {
    this.target = target;
  }

  @Override
  public void reset() {
  }

  @Override
  public double calculate(State initState, Node node, Oracle oracle) {
    State state = node.state;
    if (state.myLife <= 0)
      return Double.NEGATIVE_INFINITY;
    if (state.oppLife -state.torpedoHitStat.potentialDamage <= 0.5) return Double.POSITIVE_INFINITY;
    
    
    int cellAvailable = new FreeCellsDetector2().countFreeCellInDir(state, state.myPos);
    int potentialOppPositionsSize = oracle.oppActualPotentialPositions().size();
    int distanceToCenter = Math.max(Math.abs(state.myPos.x - target.x), Math.abs(state.myPos.y - target.y));
    
    double score = 0.0 
        + 100_000 * state.myLife 
        + 90_000 * (6 - (state.oppLife - state.torpedoHitStat.potentialDamage))
        + 0.001 * (100 - distanceToCenter) 
        + 0.1 * cellAvailable;
    
    if (Player.D_SCORE) System.err.println("---- distanceToCenter = " + distanceToCenter );
    if (Player.D_SCORE) System.err.println("---- distanceToCenter = " + cellAvailable);
    
    double avgDist = 0.0;
    if (potentialOppPositionsSize > 0) {
      for (P p : oracle.oppActualPotentialPositions()) {
        avgDist += p.manhattan(state.myPos);
      }
      avgDist /= potentialOppPositionsSize;
      
      if (Player.D_SCORE)  System.err.println("---- avgtDist = " + avgDist);
      score -= avgDist;
    }
    
    return score;
  }

}
