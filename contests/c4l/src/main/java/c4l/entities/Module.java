package c4l.entities;

public enum Module {
  SAMPLES, DIAGNOSIS, MOLECULES, LABORATORY, START_POS;
  
  private static int distances[][] = new int [][] {
    {0, 3, 4, 3, 0},
    {3, 0, 3, 4, 0},
    {4, 3, 0, 3, 0},
    {3, 4, 3, 0, 0},
    {2, 2, 2, 2, 0},
  };
  
  public static int distance(Module module1, Module module2) {
    return distances[module1.ordinal()][module2.ordinal()];
  }
}
