package sg22;

public class ApplicationCache {
  private static int MAX = 20;
  private static Application[] applications = new Application[MAX];
  private static int applicationFE;
  
  static {
    for (int i=0;i<MAX;i++) {
      applications[i] = new Application();
    }
  }
  
  public static void reset() {
    applicationFE = 0;
  }
  
  public static Application get() {
    return applications[applicationFE++];
  }
  
  public static int getCurrentUse() {
    return applicationFE;
  }
  
}
