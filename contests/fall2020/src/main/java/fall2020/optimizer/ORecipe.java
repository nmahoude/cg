package fall2020.optimizer;

public class ORecipe {
  public final int id;
  public final long mask;
  public final int delta[] = new int[4];
  public int basePrice;
  
  public ORecipe(int a, int b, int c, int d, int basePrice) {
    super();
    this.id = OptiGraph.ORECIPE_ID++;
    this.delta[0] = a;
    this.delta[1] = b;
    this.delta[2] = c;
    this.delta[3] = d;
    
    this.basePrice = basePrice;
    this.mask = 1L << id;
  }
  
  @Override
  public String toString() {
    return String.format("R#%d[%d %d %d %d]", id, delta[0], delta[1], delta[2], delta[3]);
  }

}
