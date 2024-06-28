package ooc.ai.search.thinkers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ooc.OOCMap;
import ooc.Oracle;
import ooc.P;
import ooc.Player;
import ooc.State;
import ooc.orders.Order;
import ooc.trailmapper.TorpedoHitStat;
import ooc.trailmapper.TrailNode;

public class TriggerThinker extends Thinker {
  private static final double TRIGGER_EXPECTATION_RATE = 0.75; // la certitude de faire au moins 1 dégat avec un trigger

  private Map<P, List<Order>> memoTriggers = new HashMap<>();
  TorpedoHitStat stat = new TorpedoHitStat();
  
  @Override
  public void reset() {
    memoTriggers.clear();
  }
  
  @Override
  public List<Order> calculateOrders(Oracle oracle, State state) {
    List<Order> calculatedOrders = memoTriggers.get(state.myPos);
    if (calculatedOrders != null) {
      return calculatedOrders;
    }
    
    ArrayList<Order> orders = new ArrayList<>();
    double bestScore[] = new double[] { 0.0, 0.0};
    P best[] = new P[] {P.I, P.I};
    
    for (int i = 0; i < OOCMap.S2; i++) {
      if (state.myMines.get(i) == false) continue; // no mine here

      P minePos = P.getFromOffset(i);

      int myDistanceToTorpedo = minePos.blastDistance(state.myPos);
      if (myDistanceToTorpedo == 0) continue;
      int index;
      if(myDistanceToTorpedo == 1) {
        index = 0;
      } else {
        index = 1;
      }

      stat.reset();
      oracle.oppMapper.updateStatisticalDamageAt(stat, minePos);
      
      if (Player.D_TRIGGERS) {
        System.err.println("Potential damage @ "+minePos+" = "+stat.potentialDamage);
        System.err.println("heat vs pot => "+oracle.oppMapper.heatMap[minePos.o]+" <=> "+stat.potentialDamage);
      }
      if (stat.minimalDamage == 2) {
        if (Player.D_TRIGGERS) System.err.println("   !!! Direct hit @ "+minePos);
      } else if (stat.minimalDamage == 1) {
        if (Player.D_TRIGGERS) System.err.println("   !!! Neighbor hit @ "+minePos);
        if (oracle.oppMapper.potentialPositions.size() == 1 && state.oppLife > 1) {
          boolean result = waitToTrigger(oracle, minePos);
          if (result) {
            if (Player.D_TRIGGERS) System.err.println("   yes we can wait !");
            Player.MSG = "@@@";
            continue; // pass
          } else {
            if (Player.D_TRIGGERS) System.err.println("   no, we must act");
          }
        }
      }
        
      
      double score = 0.0 
          + stat.potentialDamage
          ;
      
      
      if (score > 0.02 && score > bestScore[index]) {
        bestScore[index] = score;
        best[index] = minePos;
      }
    }

    for (int i=0;i<2;i++) {
      if (best[i] != P.I) {
        Order trigger = Order.trigger(best[i]);
        orders.add(trigger);
      }
    }

    memoTriggers.put(state.myPos, orders);
    return orders;
  }

  private boolean waitToTrigger(Oracle oracle, P minePos) {
    TrailNode trailNode = oracle.oppMapper.currentLayer[0];
    P current = trailNode.currentPos;
    for (int d=0;d<4;d++) {
      P next = current.neighbors[d];
      if (Player.map.isIsland(next)) continue;
      if (trailNode.trail.get(next.o)) continue; // Ok, he can surface & move, but that one hp anyway
      if (next.blastDistance(minePos) > 1) return false;
    }
    return true;
  }

  private void debugTrail(P pos) {
    System.err.println("Triggering "+pos+" myMapper says "+Player.oracle.myMapper.trailCountByMineAtPosFE[pos.o]);
  }

}
