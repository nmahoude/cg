package ooc.ai.search.thinkers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ooc.Oracle;
import ooc.P;
import ooc.Player;
import ooc.State;
import ooc.orders.Order;
import ooc.trailmapper.TorpedoHitStat;

public class TorpedoThinker extends Thinker {
  private static final int UNKNOWN_POSITIONS_SIZE_LIMIT = 10;
  private Map<P, List<Order>> memoTorpedo = new HashMap<>();
  private Oracle oracle;
  TorpedoHitStat stat = new TorpedoHitStat();
  
  @Override
  public void reset() {
    memoTorpedo.clear();
  }
  
  @Override
  public List<Order> calculateOrders(Oracle oracle, State state) {
    List<Order> calculatedOrders = memoTorpedo.get(state.myPos);
    if (calculatedOrders != null) {
      return calculatedOrders;
    }

    List<Order> orders = new ArrayList<>();

    Set<P> targets = state.map.torpedoTargetsByPos.get(state.myPos);

    double bestScore[] = new double[] { 0.0, 0.0};
    P bestTarget[] = new P[] {P.I, P.I};
    
    
    for (P target :targets) {
      if (Player.map.isIsland(target)) continue;
      int distTo = target.blastDistance(state.myPos);
      if (distTo == 0) continue; 
      int index = distTo == 1 ? 0 : 1;

      
      
      double score = oracle.oppMapper.heatMap[target.o];
      if (score > 0.5 && score > bestScore[index]) {
        bestScore[index] = score;
        bestTarget[index] = target;
      }
    }
    if (bestTarget[0] != P.I) {
      Order torpedo = Order.torpedo(bestTarget[0], false);
      orders.add(torpedo);
    }
    if (bestTarget[1] != P.I) {
      Order torpedo = Order.torpedo(bestTarget[1], false);
      orders.add(torpedo);
    }
    
    memoTorpedo.put(state.myPos, orders);
    return orders;
  }
}
