package calm.desertmaker;

import java.util.HashSet;
import java.util.Set;

import calm.P;
import calm.Player;

public class DMNode {
  private static final int CACHE_MAX = 100_000;
  
  private static int cacheFE = 0;
  private static DMNode cache[] = new DMNode[CACHE_MAX];
  static {
    for (int i=0;i<CACHE_MAX;i++) {
      cache[i] = new DMNode();
    }
  }
  public static DMNode pop() {
    DMNode node = cache[cacheFE++];
    node.pos = null;
    node.pick = null;
    node.currentMask = 0;
    node.distance = 0;
    node.delta = 0;
    node.parent = null;
    node.visitedPos.clear();
    return node;
  }
  public static void resetCache() {
    cacheFE = 0;
  }
  
  
  
  P pos;
  P pick;
  int currentMask;
  int distance;
  int delta;
  int remaining;
  
  DMNode parent;
  public Set<P> visitedPos = new HashSet<>();
  
  DMNode() {
  }
  
  public int evaluateDistance() {
    return distance + (delta > 0 ? 1: 0 ) + remaining ;
  }

  P next() {
    P result = Player.map.bell.pos;
    DMNode current = this;
    while (current != null) {
      if (current.pick != null) {
        result = current.pick;
      } else if (current.parent != null) {
        if (current.delta == 0) {
          result = current.pos;
        } else {
          //actions = "Micro "+current.pos+" - "+actions;
        }
      }
      current = current.parent;
    }
    return result;
  }
  
  public void debug() {
    System.err.println("DMAS Actions :");
    String actions = " Bell";
    DMNode current = this;
    while (current != null) {
      if (current.pick != null) {
        actions = "\nPICK "+current.pick+" @"+current.pos+"("+current.distance+") - "+actions;
      } else if (current.parent != null) {
        if (current.delta == 0) {
          actions = "MOVE "+current.pos+"("+current.distance+") - "+actions;
        } else {
          //actions = "Micro "+current.pos+" - "+actions;
        }
      }
      current = current.parent;
    }
    System.err.println(actions);
  }
}
