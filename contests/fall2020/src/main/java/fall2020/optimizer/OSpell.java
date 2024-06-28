package fall2020.optimizer;

public class OSpell {
  public final int id;
  public final long mask;
  public final int delta[] = new int[4];
  public final boolean repeatable; // fix
  public final int allTier1plus;
  public double score;

  public OSpell(int a, int b, int c, int d) {
    this(a, b, c, d, 
        (a < 0 || b < 0 || c < 0 || d < 0));
  
  }
  public OSpell(int a, int b, int c, int d, boolean repeatable) {
    super();
    this.id = OptiGraph.OSPELL_ID++;
    this.mask = 1L << id;
    this.delta[0] = a;
    this.delta[1] = b;
    this.delta[2] = c;
    this.delta[3] = d;
    this.allTier1plus = b+c+d;
    this.repeatable = repeatable;
  }

  @Override
  public String toString() {
    return String.format("S#%d[%d %d %d %d]", id, delta[0], delta[1], delta[2], delta[3]);
  }
  
  
}
