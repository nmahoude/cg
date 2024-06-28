package planner.actionplanner;

public class NodeCache {
  static Node cache[] = new Node[100_000];
  static int nodesFE;
  static {
    for (int i=0;i<cache.length;i++) {
      cache[i] = new Node();
    }
    nodesFE = 0;
  }
  
  public static void reset() {
    nodesFE = 0;
  }
  public static Node pop() {
    return cache[nodesFE++];
  }
}
