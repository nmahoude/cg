package uttt.mcts;

public class NodeCache {
  public static final int NODE_CACHE_SIZE = 100_000;
  
  static final Node[] nodes = new Node[NODE_CACHE_SIZE+1000];
  static int nodesIndex = nodes.length;
  static {
    for (int i = 0; i < nodes.length; i++) {
      nodes[i] = new Node();
    }
  }

  public static void resetCache() {
    nodesIndex = nodes.length;
  }

  public static Node pop() {
    Node node = nodes[--nodesIndex];
    node.parent = null;
    node.childArrayFE = -1;
    return node;
  }

  public static void push(Node node) {
    nodes[nodesIndex++] = node;
  }

  public static void init() {
  }
}
