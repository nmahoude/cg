package c4l.sample;

import java.util.List;

import c4l.entities.Sample;

public class BruteForceSampleOptimizer {
  List<Sample> samples;
  
  public void calculate() {
  for (int i=0;i<samples.size();i++) {
    for (int j=0;j<samples.size();j++) {
      if (i==j) continue;
      for (int k=0;k<samples.size();k++) {
        if (k==i || k==j) continue;
        
        // TODO how many points ? ROI?
      }
    }
  }
  }
}
