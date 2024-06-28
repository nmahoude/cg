package perf;

public class LightState2 {
  private static final int MAX_UNIT = 50;
  public static final int DECAL = 8; // int count to store unit
  
  public static final int HEALTH = 0;
  public static final int X = 1;
  public static final int Y = 2;
  
  int[] v = new int[8 * MAX_UNIT]; 
  
  
  
  public void copyFrom(LightState2 model) {
    System.arraycopy(model.v, 0, v, 0, MAX_UNIT * 8);
  }
}
