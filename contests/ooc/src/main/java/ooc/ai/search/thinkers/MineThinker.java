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

public class MineThinker extends Thinker {
  private Map<P, List<Order>> memoMines = new HashMap<>();
  
  @Override
  public void reset() {
    memoMines.clear();
  }
  
  @Override
  public List<Order> calculateOrders(Oracle oracle, State state) {
    List<Order> memoOrders = memoMines.get(state.myPos);
    if (memoOrders != null) {
      return memoOrders;
    }
    ArrayList<Order> orders = new ArrayList<>();

    
    
    double bestScore = Double.NEGATIVE_INFINITY;
    int bestD = -1;
    for (int d=0;d<4;d++) {
      P current = state.myPos.neighbors[d];
      if (state.map.isIsland(current)) continue;
      if (state.myMines.get(current.o)) continue; // already a mine
      
      double score = 1000;
      for (P mine : state.myTrueMinesPos) {
        if (Player.map.blastDistance(mine, current) <= 1) {
          // another mine near
          score -= 100;
          break;
        }
      }
      score -= current.manhattan(OOCMap.CENTER);
      if (score > bestScore) {
      	bestScore = score;
      	bestD = d;
      }
    }
    if (bestD != -1) {
    	orders.add(Order.mine(bestD));
    }
    memoMines.put(state.myPos, orders);
    return orders;
  }
}
