package uttt.mcts;

public class NodeCache {
  static final Node[] nodes = new Node[200_100];
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
    node.childArrayFE = -1;
    node.parent = null;
    return node;
  }

  public static void push(Node node) {
    nodes[nodesIndex++] = node;
  }

  public static void init() {
    
  }
}
