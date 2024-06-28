package calm.ai.astarai;

public class AStarCache {
  static AStarNode cache[] = new AStarNode[500_000];
  static int cacheFE = 0;
  static {
    for (int i=0;i<cache.length;i++) {
      cache[i] = new AStarNode();
    }
  }
  
  public static void reset() {
    cacheFE = 0;
  }
  
  public static AStarNode pop() {
    return cache[cacheFE++];
  }
}
