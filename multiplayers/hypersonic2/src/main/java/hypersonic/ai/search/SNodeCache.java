package hypersonic.ai.search;

public class SNodeCache {

  private static final int SIZE = 50_000;
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
    return node;
  }
  
  
  public static int reserve(int size) {
    int start = nodesFE;
    for (int i=0;i<size;i++) {
      SNode node = nodes[nodesFE++];
      node.movesFE = -1;
      node.score = 0;
    }
    nodesFE+=size;
    return start;
  }
}
