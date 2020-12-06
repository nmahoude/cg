package calm.ai;

import calm.Item;
import calm.P;

public class Order {
  public static final Order WAIT = new Order(OrderTag.WAIT, P.INVALID);
  public OrderTag tag;
  public P pos;
  
  private Order(OrderTag tag, P pos) {
    this.tag = tag;
    this.pos = pos;
  }

  public String output() {
    if (tag == OrderTag.WAIT) {
      return tag.toString();
    } else {
      return tag.toString()+" "+pos.output();
    }
  }

  public static Order move(Item item) {
    return new Order(OrderTag.MOVE, item.pos);
  }
  public static Order move(P pos) {
    return new Order(OrderTag.MOVE, pos);
  }

  public static Order use(P pos) {
    return new Order(OrderTag.USE, pos);
  }

  public static Order use(Item item) {
    return use(item.pos);
  }

}
