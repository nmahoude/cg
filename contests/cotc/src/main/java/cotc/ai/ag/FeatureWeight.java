package cotc.ai.ag;

import java.util.Random;

public class FeatureWeight {
  public double weights[] = new double[Feature.LAST];
  
  public FeatureWeight() {
    soundValues();
  }
  public void soundValues() {
    weights[Feature.MY_HEALTH_FEATURE] = 1.0;
    weights[Feature.HIS_HEALTH_FEATURE] = -0.2;
    weights[Feature.SPEED_FEATURE] = 1.0;
    weights[Feature.DISTANCE_TO_CLOSEST_BARREL_FEATURE] = -0.2;
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
