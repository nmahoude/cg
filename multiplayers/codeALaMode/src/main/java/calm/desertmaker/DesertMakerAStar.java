package calm.desertmaker;

import java.util.PriorityQueue;

import calm.Desert;
import calm.ItemMask;
import calm.P;
import calm.Player;
import calm.State;

public class DesertMakerAStar {

  private State state;
  private int target;
  private int ingredientsCount;
  
  public DesertMakerAStar(State state) {
    this.state = state;
  }
  
  
  public P find(Desert desert) {
    System.err.println("Launching astar with desert "+desert.item);
    target = desert.item.mask;
    ingredientsCount = Integer.bitCount(target);
    
    DMNode.resetCache();
    DMNode root = new DMNode();
    root.currentMask = state.me.hands.mask;
    root.pos = state.me.pos;
    root.pick = null;
    root.distance = 0;
    root.delta = 0;
    root.visitedPos.clear();
    root.visitedPos.add(root.pos);
    DMNode lastBestNode = find(root);
    System.err.println("lastBestNode "+ lastBestNode);
    if (lastBestNode != null) {
      lastBestNode.debug();
    }
    if (lastBestNode != null) {
      return lastBestNode.next();
    } else {
      return null;
    }
  }


  private DMNode find(DMNode root) {
    int bestDistance = 15;
    DMNode best = null;
    
    PriorityQueue<DMNode> pq = new PriorityQueue<>((node1, node2) -> Integer.compare(node1.evaluateDistance(), node2.evaluateDistance())) ;
    pq.add(root);

    while(!pq.isEmpty()) {
      DMNode current = pq.poll();
//      System.err.println("Current is "+current.pos);
//      System.err.println("  with visited cells : "+current.visitedPos);
      if (current.distance >= bestDistance) continue;
      
      if (current.currentMask == target && current.pos.manhattan(Player.map.bell.pos) <= 2) {
        if (current.distance < bestDistance) {
          System.err.println("Found a better node with distance "+current.distance);
          bestDistance = current.distance;
          best = current;
        }
        continue;
      }
      
      for (P sq : current.pos.squaredNeighbors) {
        // check what we can take
        if (Player.map.cells[sq.offset] == 0) {
          // floor
          if (current.visitedPos.contains(sq) || current.pos.manhattan(sq)>1) continue; // already visited
          if (sq == state.him.pos) continue; // can't pass through him
          
          DMNode child = new DMNode();
          child.currentMask = current.currentMask;
          child.distance = current.distance;
          child.delta = current.delta;
          child.remaining = current.remaining;
          child.delta++;
          if (child.delta == 4) {
            child.distance+=1;
            child.delta = 0;
          }
          child.parent = current;
          child.pos = sq;
          child.pick = null;
          child.visitedPos.clear();
          child.visitedPos.addAll(current.visitedPos);
          child.visitedPos.add(sq);
          pq.add(child);
        } else {
          // table
          int tableMask = state.tables[sq.offset];
          if (tableMask == 0) continue; // empty table
          if ((current.currentMask & tableMask ) != 0) continue; // double ingredient
          int newMask = current.currentMask | tableMask;
          if ((newMask & ~target) != 0) continue; // useless ingredients
          int bitCount = Integer.bitCount(newMask);
          if ((newMask & ItemMask.DISH) == 0 && bitCount>1) continue; // 2 ingredients without dish
          
          DMNode child = new DMNode();
          child.currentMask = newMask;
          child.distance = current.distance + 1 + (current.delta > 0 ? 1 : 0);
          child.delta = 0;
          child.remaining = (ingredientsCount - bitCount) * 3;
          child.parent = current;
          child.pos = current.pos;
          child.pick = sq;
          child.visitedPos.clear();
          child.visitedPos.add(current.pos);
          pq.add(child);
        }
      }
    }
    return best;
  }
}
