package cgfx.sample1.application.compartors;

import java.util.Arrays;
import java.util.List;

public class Evaluator {
  private static final int DEPTH = 10;
  public boolean cumulative = true;
  public boolean useDepthFactor = false;
  
  double[] depthFactor;
  {
    depthFactor = new double[DEPTH];
    for (int d=0;d<DEPTH;d++) {
      depthFactor[d] = Math.pow(0.7, d);
    }
  }
  
  double values[][] = new double[][] {
    {2.0, 3, 4, 5, 7, 6, 10, 10, 12, 15},
    {1.0, 3, 2, 3, 7, 2, 2, 4, 6, 7},
    {1.0, 0, 2, 2, 0, 4, 8, 6, 6, 8},
  };
  
  public List<String> names() {
    return Arrays.asList("total", "val1", "val2");
  }

  public int depth() {
    return DEPTH;
  }
  
  public double value(int index, int depth) {
    if (cumulative) {
      double value = 0;
      for (int d=0;d<=depth;d++) {
        value += ( useDepthFactor ? depthFactor[d] : 1.0 ) * values[index][d];
      }
      return value;
    } else {
      return ( useDepthFactor ? depthFactor[depth] : 1.0 ) *values[index][depth];
    }
  }
  
  
}
