package meanmax;

import meanmax.entities.Wreck;

public class HeatMap {
  public static int RESOLUTION = 1000;
  public static double step = 6000.0 / RESOLUTION;
  int heat[][] = new int[RESOLUTION][RESOLUTION];
  
  public void update(Wreck[] wrecks) {
    clear();
    for (Wreck wreck : wrecks) {
      
    }
  }

  private void clear() {
    for (int i=0;i<RESOLUTION;i++) {
      for (int j=0;j<RESOLUTION;j++) {
        heat[i][j] = 0;
      }
    }
  }
}
