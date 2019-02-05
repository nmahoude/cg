package hypersonic.ai.bfsmc;

public class BFSMCNodeCache {

  private static final int SIZE = 60_000;
  public static BFSMCNode nodes[] = new BFSMCNode[SIZE];
  private static int nodesFE = SIZE-1;

  static {
    for (int i=0;i<SIZE;i++) {
      nodes[i] = new BFSMCNode();
    }
  }
  
  public static void reset() {
    nodesFE = 0;
  }
  
  public static BFSMCNode pop() {
    BFSMCNode node = nodes[nodesFE++];
    node.movesFE = -1;
    return node;
  }
  
  
  public static int reserve(int size) {
    int start = nodesFE;
    for (int i=0;i<size;i++) {
      BFSMCNode node = nodes[nodesFE++];
      node.movesFE = -1;
    }
    nodesFE+=size;
    return start;
  }

  public static boolean canReserve(int size) {
    return nodesFE+size < SIZE;
  }
}
