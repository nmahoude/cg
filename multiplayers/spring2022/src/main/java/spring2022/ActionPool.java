package spring2022;

public class ActionPool {
  public final static int MAX = 10_000;
  private static Action[] pool = new Action[MAX];
  private static int poolFE = 0;
  static {
    for (int i=0;i<MAX;i++) {
      pool[i] = new Action();
    }
  }
  
  public static void reset() {
    poolFE = 0;
  }
  
  public static Action get() {
    return pool[poolFE++];
  }
  
}