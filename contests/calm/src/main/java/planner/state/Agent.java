package planner.state;

import java.util.Scanner;

import planner.PlannerPlayer;
import util.P;

public class Agent {
  public P pos = P.get(0, 0); // current position
  public int items; // hold items
  
  public void read(Scanner in) {
    pos = P.get(in.nextInt(), in.nextInt());
    
    String playerItem = in.next();
    if (PlannerPlayer.DEBUG_INPUT) {
      System.err.println("\""+pos.x+" "+pos.y+" "+playerItem+PlannerPlayer.DEBUG_EOL);
    }
    items = Item.getFromString(playerItem);
  }

  public void copyFrom(Agent model) {
    this.pos = model.pos;
    this.items = model.items;
  }
}
