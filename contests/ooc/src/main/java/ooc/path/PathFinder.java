package ooc.path;

import java.util.BitSet;

import ooc.P;
import ooc.Player;

/*
 * Find the optimal path (longest without surface)
 * 
 * TODO limit to a certain depth ?
 */
public class PathFinder {

  static class Node {
    Node parent;
    int length;
    P current;
    BitSet trail = new BitSet();
    
    
    public void find() {
      if (length > 5) return;
      
      for (int d=0;d<4;d++) {
        P next = current.neighbors[d];
        if (Player.map.isIsland(next)) continue;
        if (trail.get(next.o)) continue; 
        
        Node node = new Node();
        node.parent = this;
        node.length = this.length+1;
        node.current = next;
        node.trail.or(trail);
        node.trail.set(next.o);
        
        node.find();
      }
    }
  }
  
  public P next(P start, BitSet trail) {
    
    Node root = new Node();
    root.parent = null;
    root.length = 0;
    root.current = start;
    root.trail.or(trail);
    
    root.find();
    
    return P.I;
  }
}
