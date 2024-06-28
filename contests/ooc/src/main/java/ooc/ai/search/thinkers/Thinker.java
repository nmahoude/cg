package ooc.ai.search.thinkers;

import java.util.List;

import ooc.Oracle;
import ooc.State;
import ooc.orders.Order;

public abstract class Thinker {
  public void reset() {}
  public abstract List<Order> calculateOrders(Oracle oracle, State state);
}