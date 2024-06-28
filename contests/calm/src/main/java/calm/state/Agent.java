package calm.state;

import java.util.Scanner;

import calm.Player;
import calmBronze.Item;
import util.P;

public class Agent {
  public P pos = P.get(0, 0); // current position
  public int items; // hold items
  
  public void read(Scanner in) {
    pos = P.get(in.nextInt(), in.nextInt());
    
    String playerItem = in.next();
    if (Player.DEBUG_INPUT) {
      System.err.println("\""+pos.x+" "+pos.y+" "+playerItem+Player.DEBUG_EOL);
    }
    items = Item.getFromString(playerItem);
  }

  public void copyFrom(Agent model) {
    this.pos = model.pos;
    this.items = model.items;
  }

  public boolean hasDish() {
    return (items | Item.DISH) != 0;
  }

  public boolean fullDish() {
    return hasDish() && Integer.bitCount(items) == 5; // 4 ingredient + dish
  }

  public boolean canUseRelativeToDistance(Table table) {
    return this.pos.neighborDistance(table.pos) <= 1;
  }
}
