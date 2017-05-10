package pokerChipRace.ai;

import java.util.Random;

public class FeatureWeight {
  public double weights[] = new double[Feature.LAST];
  
  public FeatureWeight() {
    soundValues();
  }
  public void soundValues() {
    weights[Feature.MY_BIGGEST_RADIUS] = 2.0;
    weights[Feature.MY_TOTAL_RADIUS] = 1.0;
    weights[Feature.ALL_OTHER_TOTAL_RADIUS] = -0.3;
    weights[Feature.DIST_TO_BIGGER_ENTITIES] = -1.0;
    weights[Feature.DIST_TO_SMALLER_ENTITIES] = 1.0;
    weights[Feature.DIST_BETWEEN_MINE] = 0.0;
    weights[Feature.DIST_CLOSEST_BIGGER] = 0.0;
    weights[Feature.DIST_CLOSEST_SMALLER] = 0.0;
    weights[Feature.SPEED] = 0.000;
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
