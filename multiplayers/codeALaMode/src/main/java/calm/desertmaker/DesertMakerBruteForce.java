package calm.desertmaker;

import java.util.ArrayList;
import java.util.List;

import calm.Desert;
import calm.Item;
import calm.P;
import calm.Player;
import calm.State;

public class DesertMakerBruteForce {

  private State state;
  private int target;

  private int bestDistance;
  private List<Item> bestPath = new ArrayList<>();
  
  public DesertMakerBruteForce(State state) {
    this.state = state;
  }
  
  public Item bruteForce(Desert desert) {
    bestDistance = Integer.MAX_VALUE;
    bestPath.clear();
    
    target = desert.item.mask;
    
    List<Item> all = new ArrayList<>();
    all.add(Player.map.dishwasher);
    for (int i=0;i<state.itemsFE;i++) {
      Item item = state.items[i];
      if (item.mask == 0) continue;
      if ((item.mask & ~desert.item.mask) != 0) continue;
      all.add(item);
    }
    
    for (Item item : all) {
      System.err.println("Debug distance to "+item+"  = "+Player.map.distanceFromTo(state.me.pos, item.pos, state.him.pos));
    }
    
    Item item = find(state.me.hands.mask, all, new ArrayList<>(), state.me.pos, 0);
    return bestPath.get(0);
  }

  private Item find(int current, List<Item> all, List<Item> visited, P last, int distance) {
    if (current == target) {
      distance += Player.map.distanceFromTo(last, Player.map.bell.pos, state.him.pos);
      if (distance == Integer.MAX_VALUE) distance = 10000; // hack when I'm trapped
      if (distance < bestDistance) {
        System.err.println("New best Solution is ("+distance+")"+visited);
        P c = state.me.pos;
        for (Item item : visited) {
          System.err.println("   distance from "+c+" to "+item+" = "+Player.map.distanceFromTo(c, item.pos, state.him.pos));
          c =  item.pos;

        }
        bestDistance = distance;
        bestPath.clear();
        bestPath.addAll(visited);
        bestPath.add(Player.map.bell);
      }
    }

    
    for (Item item : all) {
      if (visited.contains(item)) continue;
      if ((current & item.mask) != 0) continue;
      if (((current | item.mask) & ~target) != 0) continue;

      visited.add(item);
      for (P sq : item.pos.squaredNeighbors) {
        if (Player.map.cells[sq.offset] != 0) continue;
        int distanceToNextItem = distance+Math.min(10000, Player.map.distanceFromTo(last, sq, state.him.pos));
        Item result = find(current|item.mask, all, visited, sq, 1+distanceToNextItem);
      }
      visited.remove(item);
    }
    
    
    return null;
  }
}
