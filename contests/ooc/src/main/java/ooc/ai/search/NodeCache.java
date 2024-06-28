package ooc.ai.search;

public class NodeCache {

  private static final int MAX_NODES = 3_000;
  static Node nodes[] = new Node[MAX_NODES];
  static int currentIndex = MAX_NODES-1;
  
  static {
    for (int i=0;i<MAX_NODES;i++) {
      nodes[i] = new Node(0);
    }
  }
  
  public static Node pop() {
    Node node = nodes[currentIndex--];
    return node;
  }
  
  public static void push(Node node) {
    nodes[++currentIndex] = node;
  }
  
  public static void restitute(Node toRestitute[], int size) {
    for (int i=0;i<size;i++) {
      nodes[++currentIndex] = toRestitute[i];
    }
  }
  
}
