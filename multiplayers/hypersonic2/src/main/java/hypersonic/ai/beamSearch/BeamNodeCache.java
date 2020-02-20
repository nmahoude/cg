package hypersonic.ai.beamSearch;

public class BeamNodeCache {

  private static final int SIZE = 50_000;
  public static BeamNode nodes[] = new BeamNode[SIZE];
  private static int nodesFE = SIZE-1;

  static {
    for (int i=0;i<SIZE;i++) {
      nodes[i] = new BeamNode();
    }
  }
  
  public static void reset() {
    nodesFE = 0;
  }
  
  public static BeamNode pop() {
    BeamNode node = nodes[nodesFE++];
    node.movesFE = -1;
    return node;
  }
  
  
  public static int reserve(int size) {
    int start = nodesFE;
    for (int i=0;i<size;i++) {
      BeamNode node = nodes[nodesFE++];
      node.movesFE = -1;
      node.cumulativeScore = 0;
    }
    nodesFE+=size;
    return start;
  }
}
