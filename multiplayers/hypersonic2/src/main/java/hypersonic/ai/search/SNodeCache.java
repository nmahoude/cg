package hypersonic.ai.search;

public class SNodeCache {

  private static final int SIZE = 50_000;
  private static SNode nodes[] = new SNode[SIZE];
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
    node.score = 0;
    return node;
  }
  
}
