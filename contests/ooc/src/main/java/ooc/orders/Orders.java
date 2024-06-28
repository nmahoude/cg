package ooc.orders;

import java.util.ArrayList;
import java.util.List;

public class Orders {
  private List<Order> orders = new ArrayList<>();
  
  public void clear() {
    orders.clear();
  }
  
  public void addOrder(Order order) {
    orders.add(order);
  }
  
  public void print(String message) {
    String output = "";
    for (Order order : orders) {
      output += order.output();
      output+="|";
    }
    if (!"".equals(message)) {
      System.out.println(output+"|MSG "+message);
    } else {
      System.out.println(output);
    }
  }

  public boolean hasSurface() {
    for (Order o : orders) {
      if (o.tag == OrderTag.SURFACE) {
        return true;
      }
    }
    return false;
  }

  public List<Order> getOrders() {
    return orders;
  }

  public boolean isTorpedoAfterMove() {
    boolean hasMove = false;
    for (Order o : orders) {
      if (o.tag == OrderTag.TORPEDO) return hasMove;
      if (o.tag == OrderTag.MOVE) hasMove = true;
    }
    return false;
  }
  
  public void debug() {
    for (Order o : orders) {
      System.err.println(o.output());
    }
  }

	public boolean isEmpty() {
		return orders.isEmpty();
	}
}
