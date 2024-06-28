package ooc.ai.search.thinkers;

import java.util.ArrayList;
import java.util.List;

import ooc.Oracle;
import ooc.P;
import ooc.Player;
import ooc.State;
import ooc.charge.ChargeAI;
import ooc.orders.Order;

public class MoveThinker extends Thinker {

  public ChargeAI chargeAI;

  public void setChargeAI(ChargeAI chargeAI) {
    this.chargeAI = chargeAI;
  }
  @Override
  public List<Order> calculateOrders(Oracle oracle, State state) {
    List<Order> orders = new ArrayList<>();
    
    int randomDir= Player.random.nextInt(4);
    for (int dd=0;dd<4;dd++) {
      int d = (dd+randomDir)%4;
      P next = state.myPos.neighbors[d];
      
      if (state.map.isIsland(next)) continue;
      if (state.isVisitedCells(next)) continue;
      
      orders.add(Order.move(d, chargeAI.calculateCharge(state)));
    }
    return orders;
  }

}
