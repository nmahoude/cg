package ooc.ai.search.thinkers;

import java.util.ArrayList;
import java.util.List;

import ooc.Direction;
import ooc.Oracle;
import ooc.P;
import ooc.State;
import ooc.orders.Order;

public class SilenceThinker extends Thinker {

  @Override
  public List<Order> calculateOrders(Oracle oracle, State state) {
    ArrayList<Order> orders = new ArrayList<>();

    // flee
    findFarthest(orders, state);

    int accessibleCells = calculateAccessibleCells(state);
    if (accessibleCells > 4) {
      orders.add(Order.silence(Direction.EAST, 0));
    }
    return orders;
  }

  private void findFarthest(List<Order> orders, State state) {
    int bestDir = 0;
    int bestLength = 0;
  
    for (int d=0;d<4;d++) {
      P n = state.myPos;
      int r = 0;
      for (r = 1; r <= 4; r++) {
        n = n.neighbors[d];
        if (state.map.isIsland(n)) break;
        if (state.isVisitedCells(n)) break;
      }
      //orders.add(Order.silence(Direction.from(d), r-1));
      if (r-1 > bestLength) {
        bestLength = r-1;
        bestDir = d;
      }
    }
    if (bestLength > 0) {
      orders.add(Order.silence(Direction.from(bestDir), bestLength));
    }
  }

  private int calculateAccessibleCells(State state) {
    int cells = 0;
    for (int d=0;d<4;d++) {
      P n = state.myPos;
      for (int r = 1; r <= 4; r++) {
        n = n.neighbors[d];
        if (state.map.isIsland(n)) break;
        if (state.isVisitedCells(n)) break;
        cells++;
      }
    }
    return cells;
  }

}
