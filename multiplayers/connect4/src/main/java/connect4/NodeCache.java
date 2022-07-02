package connect4;

public class NodeCache {
  private static int MAX = 150_000;
  private static Node[] nodes = new Node[MAX];
  private static int nodesFE;
  
  static {
    for (int i=0;i<MAX;i++) {
      nodes[i] = new Node();
    }
  }
  
  public static void reset() {
    nodesFE = 0;
  }
  
  public static Node get() {
    return nodes[nodesFE++];
  }
  
  public static Node getWithParent(Node parent) {
    Node node = nodes[nodesFE++];
    return node;
  }

  public static int getCurrentUse() {
    return nodesFE;
  }
  
}
