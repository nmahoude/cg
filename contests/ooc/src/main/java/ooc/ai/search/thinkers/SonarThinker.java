package ooc.ai.search.thinkers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ooc.Oracle;
import ooc.P;
import ooc.State;
import ooc.orders.Order;

public class SonarThinker extends Thinker {

  private static final int POSITIONS_ROI_LIMIT = 10;
  private static final double PERCENT_ROI_LIMIT = 0.5;

  @Override
  public List<Order> calculateOrders(Oracle oracle, State state) {
    double scoreBySector[] = new double[16];
    int total = oracle.oppActualPotentialPositions().size();
    if (total <= 1) return Collections.emptyList();
    
    int bestSector = 0;
    for (P pos : oracle.oppActualPotentialPositions()) {
      scoreBySector[pos.sector]++;
      if (state.map.isPosPossibleTorpedoLaunchPointToTarget(state.myPos, pos)) {
        scoreBySector[pos.sector] += 0.001;
      }
      if (scoreBySector[pos.sector] > scoreBySector[bestSector]) {
        bestSector = pos.sector;
      }
    }
    
    if (scoreBySector[bestSector] >= total) {
      return Collections.emptyList();
    }
    
    double percent = 1.0 * scoreBySector[bestSector] / total;
    if (scoreBySector[bestSector] > POSITIONS_ROI_LIMIT || percent > PERCENT_ROI_LIMIT) {
      return Arrays.asList(Order.sonar(bestSector));
    } else {
      return Collections.emptyList();
    }
  }
}
