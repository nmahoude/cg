package sg22.nodes;

public class NodeCache {
  private static int MAX = 150_000;
  private static Node[] nodes = new Node[MAX];
  private static int nodesFE;
  
  static {
    for (int i=0;i<MAX;i++) {
      nodes[i] = Node.init();
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
    node.parent = parent;
    if (parent != null) {
      node.copyFrom(parent);
      node.children.clear();
      node.action.doWait();
    }
    return node;
  }

  public static int getCurrentUse() {
    return nodesFE;
  }
  
}
