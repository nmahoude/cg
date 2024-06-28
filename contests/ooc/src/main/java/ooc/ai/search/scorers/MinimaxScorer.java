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

public class MinimaxScorer extends Scorer {
  private Map<P, Integer> memoFreeCellsInDir = new HashMap<>();
  private P target;

  public MinimaxScorer(P target) {
    this.target = target;
  }

  @Override
  public void reset() {
    memoFreeCellsInDir.clear();
  }
  
  @Override
  public double calculate(State initState, Node node, Oracle oracle) {
    State state = node.state;
    if (state.oppLife - state.torpedoHitStat.minimalDamage <= 0.5) {
      return 1_000_000 + state.myLife;
    }
    if (state.myLife <= 0) {
      return Double.NEGATIVE_INFINITY;
    }
    int cellAvailable = new FreeCellsDetector2().countFreeCellInDir(state, state.myPos);
    int potentialOppPositionsSize = oracle.oppActualPotentialPositions().size();
    int distanceToCenter = Math.max(Math.abs(state.myPos.x - target.x), Math.abs(state.myPos.y - target.y));
    double score = 0.0 
        + 1000 * state.myLife 
        + 5 * state.torpedoHitStat.minimalDamage 
        + 5 * state.torpedoHitStat.potentialDamage 
        + 0.00001 * (100 - distanceToCenter) 
        + 1.5 * state.fastDetector.count();
    
    if (hasMoveBeforeTorpedo(node)) {
      score -= 0.00001;
    }
    
    score += 0.01 * oracle.myMapper.minesHeatMap2[state.myPos.o];
    
    int furtivite = state.fastDetector.count();

    if (furtivite == 1) {
      if (oracle.oppMapper.allMines.get(state.myPos.o)) {
        score -= 1000;
      }
    }
    
    if (furtivite > 15) {
    } else {
      score -= (15 - furtivite) * 5.0 * oracle.oppMapper.minesHeatMap2[state.myPos.o];
    }
    
    P current = state.myPos;
    for (P next : current.neighbors) {
      if (Player.map.isIsland(next))
        continue;
      if (state.isVisitedCells(next))
        score += 1.0;
    }
    if (cellAvailable > 0) {
      score += 1.0 * cellAvailable;
      score -= 1 * 1.0 / cellAvailable;
    }
    if (node.hasTorpedo() && state.torpedoHitStat.minimalDamage < 2 && oracle.oppMapper.potentialPositions.size() == 1) {
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
      score -= 0.01 * avgDist;
    }
    return score;
  }

  public boolean hasMoveBeforeTorpedo(Node node) {
    Node current = node;
    boolean hasTorpedo = false;
    while (current != null) {
      if (current.order.tag == OrderTag.TORPEDO) {
        hasTorpedo = true;
      }
      if (current.order.tag == OrderTag.MOVE) {
        return hasTorpedo;
      }
      current = current.parent;
    }
    
    return false;
  }
}
