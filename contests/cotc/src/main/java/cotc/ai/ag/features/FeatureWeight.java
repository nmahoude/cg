package cotc.ai.ag.features;

import java.util.Random;

public class FeatureWeight {
  public double weights[] = new double[Feature.LAST];
  public ShipWeight shipWeights[] = new ShipWeight[3];
  
  public FeatureWeight() {
    for (int i=0;i<3;i++) {
      shipWeights[i] = new ShipWeight();
    }
    soundValues();
  }
  public void soundValues() {
    weights[Feature.HIS_HEALTH_FEATURE] = -0.2;
  }
  
  public void randomize() {
    Random rand = new Random(System.currentTimeMillis());
    for (int i=0;i<Feature.LAST;i++) {
      weights[i] = 1.0-2*rand.nextDouble();
    }
  }
  public void mutate() {
    Random rand = new Random(System.currentTimeMillis());
    boolean changed = false;
    while (!changed) {
      for (int i=0;i<Feature.LAST;i++) {
        if (rand.nextInt(10) == 0) {
          weights[i] = 1.0-2*rand.nextDouble();
          changed = true;
        }
      }
    }
  }

  public void output() {
    System.out.print("weights=new double[]{");
    for (int i=0;i<Feature.LAST;i++) {
      System.out.print(""+weights[i]+",");
    }
    System.out.println("};");
  }
}
