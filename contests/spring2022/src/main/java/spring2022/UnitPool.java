package spring2022;

public class UnitPool {
  public final static int MAX = 10_000;
  private static Unit[] units = new Unit[MAX];
  private static int unitsFE = 0;
  static {
    for (int i=0;i<MAX;i++) {
      units[i] = new Unit();
    }
  }
  
  public static void reset() {
    unitsFE = 0;
  }
   
  public static Unit get() {
    return units[unitsFE++];
  }
  
}
