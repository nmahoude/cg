package oware;

public class StateCache {
  private static int MAX = 500_000;
  private static State[] nodes = new State[MAX];
  private static int nodesFE;
  
  static {
    for (int i=0;i<MAX;i++) {
      nodes[i] = new State();
    }
  }
  
  public static void reset() {
    nodesFE = 0;
  }
  
  public static State get() {
    return nodes[nodesFE++];
  }
  
  public static int getCurrentUse() {
    return nodesFE;
  }
  
}
