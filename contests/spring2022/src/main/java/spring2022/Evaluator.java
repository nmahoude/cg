package spring2022;

public class Evaluator {
  public static final int MAX_DEPTH = 10;
  
  public static double[] depthFactor;
  static {
    depthFactor = new double[MAX_DEPTH];
    for (int d=0;d<MAX_DEPTH;d++) {
      depthFactor[d] = Math.pow(0.9, d);
    }
  }

  private double total;

  public static int MAX_SCORES = 3;
  
  public static enum Values {
    TOTAL(0, "%4.2f"),
    VAL1(1, "%4.2f"),
    VAL2(2, "%4.2f");
    
    public int ordinal;
    private String format;
    
    Values(int ordinal, String format) {
      this.ordinal = ordinal;
      this.format = format;
    }
  }

  double weigths[] = new double[MAX_SCORES];
  double scores[] = new double[MAX_SCORES];

  public void reset() {
    total = 0.0;
  }
  
  public double score() { 
    return total;
  }
  
  public void evaluate(State current, int depth) {
    total += depthFactor[depth] * evaluate(current);
  }
  
  /**
   * evaluate the current state of the game 
   * 
   * save values in temporary values (for debug purpose)
   * and return the global score 
   * @param current
   * @return
   */
  public double evaluate(State current) {
    scores[Values.VAL1.ordinal] = 1.0;
    scores[Values.VAL2.ordinal] = 2.0;
    
    
    double score = 0.0;
    for (int i=1;i<MAX_SCORES;i++) {
      score += scores[i];
    }
    scores[Values.TOTAL.ordinal] = score;
    return score;
  }

  public void debug(State current, int depth) {
    evaluate(current, depth);
    
    for (int i=0;i<MAX_SCORES;i++) {
      System.err.printf("%s = "+Values.values()[i].format+ " / ", Values.values()[i].name() ,scores[i]);
    }
  }

  public double total() {
    return total;
  }
  
}
