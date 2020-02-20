package hypersonic.ai.bfs;

public class BFSCache {
  private static final int SIZE = 60_000;
  public static BFSNode nodes[] = new BFSNode[SIZE];
  private static int nodesFE = SIZE-1;

  static {
    for (int i=0;i<SIZE;i++) {
      nodes[i] = new BFSNode();
    }
  }
  
  public static void reset() {
    nodesFE = 0;
  }
  
  public static BFSNode pop() {
    BFSNode node = nodes[nodesFE++];
    node.movesFE = -1;
    return node;
  }
  
  
  public static int reserve(int size) {
    int start = nodesFE;
    for (int i=0;i<size;i++) {
      BFSNode node = nodes[nodesFE++];
      node.movesFE = -1;
    }
    nodesFE+=size;
    return start;
  }

  public static boolean canReserve(int size) {
    return nodesFE+size < SIZE;
  }
}
