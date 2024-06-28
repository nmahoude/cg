package utg2019.sim;

import trigonometryInt.Point;

public class Action {
  
  public Order order;
  public Point pos;
  public Item item;

  @Override
  public String toString() {
    return String.format("%s ( %s, %s)", order.toString(), pos.toString(), item.toString());
  }
  
  private Action(Order order, Point coord, Item item) {
    this.order = order;
    this.pos = coord;
    this.item = item;
  }
  
  public static Action doWait() {
    return new Action(Order.WAIT, Point.Invalid, Item.ANYTHING);
  }

  public static Action move(Point pos) {
    return new Action(Order.MOVE, pos, Item.ANYTHING);
  }

  public static Action request(Item item) {
    return new Action(Order.REQUEST, Point.Invalid, item);
  }
  public static Action dig(Point pos) {
    return new Action(Order.DIG, pos, Item.ANYTHING);
  }

  public String output() {
    switch(order) {
    case DIG:
      return "DIG "+pos.x+" "+pos.y;
    case MOVE:
      return "MOVE "+pos.x+" "+pos.y;
    case REQUEST:
      return "REQUEST "+item;
    case WAIT:
      return "WAIT";
    default:
      throw new RuntimeException("Unknown order : "+order);
    }
  }
  
  @Override
  public boolean equals(Object obj) {
    Action other= (Action)obj;
    if (other.order != this.order) return false;
    if (other.item != this.item) return false;
    return pos.equals(other.pos);
  }

  @Override
  public int hashCode() {
    return pos.hashCode();
  }
  
  public static Action move(int x, int y) {
    return move(Point.get(x, y));
  }
  public static Action dig(int x, int y) {
    return dig(Point.get(x, y));
  }

  public void copyFrom(Action model) {
    if (model == null) {
      return;
    }
    this.order = model.order;
    this.pos = model.pos;
    this.item = model.item;
  }
}
