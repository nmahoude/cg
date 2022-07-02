package connect4;


public class TranspositionTable {
  private static final int MAX_BITS = 18;
  private static final int MAX_ENCONDING = (int)Math.pow(2, MAX_BITS);
  private static final int MAX_BITS_MASK = (int)Math.pow(2, MAX_BITS)-1;
  
  static Node[] empty = new Node[MAX_ENCONDING];
  static Node[] nodes = new Node[MAX_ENCONDING];
  public static int totalCollisions = 0;
  
  public static void clear() {
    System.arraycopy(empty, 0, nodes, 0, MAX_ENCONDING);
    totalCollisions = 0;
  }
  
  
  public static void put(int hash, Node node) {
    nodes[hash & MAX_BITS_MASK] = node;
  }


  public static Node get(int hash, Node node) {
    
    Node cachedNode = nodes[hash & MAX_BITS_MASK];
    if (cachedNode != null) {
      if (!cachedNode.equals(node)) {
        // replace old node ?
        return null; 
      }
      
      totalCollisions++;
      return cachedNode;
    } else {
      return null;
    }
  }
}
