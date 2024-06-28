package ooc.ai.search.scorers;

import java.util.HashMap;
import java.util.Map;

import ooc.FreeCellsDetector2;
import ooc.Oracle;
import ooc.P;
import ooc.Player;
import ooc.State;
import ooc.ai.search.Node;
import ooc.orders.OrderTag;

public class OldMinimaxScorer extends Scorer {
  private Map<P, Integer> memoFreeCellsInDir = new HashMap<>();
  private P target;

  public OldMinimaxScorer(P target) {
    this.target = target;
  }

  @Override
  public void reset() {
    memoFreeCellsInDir.clear();
  }
  
  @Override
  public double calculate(State initState, Node node, Oracle oracle) {
    State state = node.state;
    if (state.myLife <= 0) {
        return Double.NEGATIVE_INFINITY;
    }
    if (state.oppLife - state.torpedoHitStat.minimalDamage <= 0.5) {
        return 1_000_000 + state.myLife;
    }
    int cellAvailable = new FreeCellsDetector2().countFreeCellInDir(state, state.myPos);
    int potentialOppPositionsSize = oracle.oppActualPotentialPositions().size();
    int distanceToCenter = Math.max(Math.abs(state.myPos.x - target.x), Math.abs(state.myPos.y - target.y));
    
    /*
    if (Player.turn == 81) {
      System.err.println("describe score ...");
      System.err.println("cell available : "+cellAvailable);
      System.err.println("Pot opp position "+potentialOppPositionsSize);
    }
    */
    double score = 0.0 
        + 30 * state.myLife 
        + 10 * (6 - (state.oppLife - state.torpedoHitStat.potentialDamage)) 
        + 0.00001 * (100 - distanceToCenter) 
        + 1.5 * state.fastDetector.count() 
        - 30.0 * (oracle.oppMapper.minesHeatMap2[state.myPos.o] > 0 ? 1 : 0);
    
    if (cellAvailable > 0) {
      score += 0.1 * cellAvailable;
      score -= 1 * 1.0 / cellAvailable;
    }

    if (state.fastDetector.count() == 1) {
      score -= 10000.0 * (oracle.oppMapper.minesHeatMap2[state.myPos.o] > 0.10 ? 1 : 0);
    }
    
    if (node.order.tag == OrderTag.TORPEDO && state.torpedoHitStat.minimalDamage < 2 && oracle.oppMapper.potentialPositions.size() == 1) {
      double result = Player.minimax.searchMinimizing(node.state, initState.oppLife);
      if (result >= 900) {
        score -= 10_00_000;
      }
    }
    if (node.hasMove()) {
      score += 5.0;
    }
    if (node.hasSilence()) {
      if (initState.fastDetector.count() >= state.fastDetector.count()) {
        score -= 200_000;
      }
    }
    double avgDist = 0.0;
    if (potentialOppPositionsSize > 0) {
      for (P p : oracle.oppActualPotentialPositions()) {
        avgDist += p.manhattan(state.myPos);
      }
      avgDist /= potentialOppPositionsSize;
      score -= 0.1 * avgDist;
    }
    return score;
  }
}
