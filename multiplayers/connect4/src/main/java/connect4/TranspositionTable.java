package connect4;

import java.util.Arrays;

public class TranspositionTable {
  private static final int MAX_BITS = 24;
  private static final int MAX_ENCONDING = (int)Math.pow(2, MAX_BITS);
  private static final int MAX_BITS_MASK = (int)Math.pow(2, MAX_BITS)-1;
  
  static Node[] nodes = new Node[MAX_ENCONDING];
  public static int totalCollisions = 0;
  
  public static void clear() {
    Arrays.fill(nodes, null);
    totalCollisions = 0;
  }
  
  
  public static void put(int hash, Node node) {
    nodes[hash & MAX_BITS_MASK] = node;
  }


  public static Node get(int hash, Node node) {
    
    Node cachedNode = nodes[hash & MAX_BITS_MASK];
    if (cachedNode != null) {
      if (!cachedNode.isEquals(node)) {
        // replace old node or keep them ?
        // put(hash, node);
        return null; 
      }
      
      totalCollisions++;
      return cachedNode;
    } else {
      return null;
    }
  }
}
