package lcm.ai.beam;

public class BSCache {
  static BSNode nodes[] = new BSNode[500_000];
  static int nodesFE;
  
  static {
    for (int i=0;i<nodes.length;i++) {
      nodes[i] = new BSNode();
    }
  }
  
  public static void init() {
    nodesFE = 0;
  }
  
  public static BSNode pop() {
    BSNode bsNode = nodes[nodesFE++];
    bsNode.isTerminal = false;
    bsNode.action = null;
    bsNode.parent = null;
    return bsNode;
  }

  public static void printInfo() {
    System.err.println("Use of cache : " + nodesFE + "  => " + (100.0 * nodesFE / 500_000)+ " %");
  }
}
