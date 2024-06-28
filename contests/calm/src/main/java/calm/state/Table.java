package calm.state;

import calmBronze.Item;
import util.P;

public class Table {
  public final P pos;
  public int item;

  public Table(int x, int y) {
    this.pos = P.get(x, y);
    this.item = 0;
  }

  public boolean hasDish() {
    return (item & Item.DISH) != 0 || (item == Item.EQUIPMENT_DISH);
  }

  public int getDish() {
    return item;
  }

  public boolean hasBlueberries() {
    return (item == Item.BLUEBERRIES || item == Item.EQUIPMENT_BLUEBERRIES);
  }
  public boolean hasIceCream() {
    return (item == Item.ICE_CREAM || item == Item.EQUIPMENT_ICE_CREAM);
  }

  public double dist(int playerX, int playerY) {
    return Math.abs(this.pos.x-playerX)+Math.abs(this.pos.y-playerY);
  }
  @Override
  public String toString() {
    return "at "+pos+" item :"+Item.toString(item);
  }
}
