<<<<<<< Updated upstream
package hypersonic.ai.search;

public class SNodeCache {

  private static final int SIZE = 40_000;
  public static SNode nodes[] = new SNode[SIZE];
  private static int nodesFE = SIZE-1;
  public static boolean full = false;

  static {
    for (int i=0;i<SIZE;i++) {
      nodes[i] = new SNode();
    }
  }
  
  public static void reset() {
    nodesFE = 0;
    full = false;
  }
  
  public static SNode pop() {
    SNode node = nodes[nodesFE++];
    node.movesFE = -1;
    node.bestScore = Double.NEGATIVE_INFINITY;
    return node;
  }
  
  
  public static int reserve(int size) {
    if (full) {
      return -1;
    } else if (nodesFE + size >= SIZE) {
      full = true;
      return -1;
    } else {
      int start = nodesFE;
      for (int i=0;i<size;i++) {
        SNode node = nodes[nodesFE++];
        node.movesFE = -1;
        node.bestScore = Double.NEGATIVE_INFINITY;
      }
      nodesFE+=size;
      return start;
    }
  }
}
=======
package hypersonic.ai.search;

public class SNodeCache {

  private static final int SIZE = 60_000;
  public static SNode nodes[] = new SNode[SIZE];
  private static int nodesFE = SIZE-1;

  static {
    for (int i=0;i<SIZE;i++) {
      nodes[i] = new SNode();
    }
  }
  
  public static void reset() {
    nodesFE = 0;
  }
  
  public static SNode pop() {
    SNode node = nodes[nodesFE++];
    node.movesFE = -1;
    node.bestScore = Double.NEGATIVE_INFINITY;
    return node;
  }
  
  
  public static int reserve(int size) {
    int start = nodesFE;
    for (int i=0;i<size;i++) {
      SNode node = nodes[nodesFE++];
      node.movesFE = -1;
      node.bestScore = Double.NEGATIVE_INFINITY;
    }
    nodesFE+=size;
    return start;
  }
}
>>>>>>> Stashed changes
