package cotc.ai.ag;

import java.util.Random;

public class FeatureWeight {
  public static final int MY_HEALT_WEIGHT = 0;
  public static final int HIS_HEALTH_WEIGHT = 1;
  public static final int SPEED_WEIGHT = 2;
  public static final int DISTANCE_TO_CLOSEST_BARREL_WEIGHT = 3;
  public static final int DIST_TO_BARREL_WEIGHT = 4;
  public static final int BARREL_COUNT0_WEIGHT = 5;
  public static final int BARREL_COUNT1_WEIGHT = 6;
  public static final int RUM_COUNT0_WEIGHT = 7;
  public static final int RUM_COUNT1_WEIGHT = 8;
  public static final int DISTANCE_TO_CENTER_WEIGHT = 9;
  public static final int MY_MOBILITY_WEIGHT = 10;
  public static final int HIS_MOBILITY_WEIGHT = 11;
  
  public static final int LAST = 12;
  public double weights[] = new double[LAST];
  
  public FeatureWeight() {
    soundValues();
  }
  public void soundValues() {
    weights[MY_HEALT_WEIGHT] = 1.0;
    weights[HIS_HEALTH_WEIGHT] = -0.2;
    weights[SPEED_WEIGHT] = 1.0;
    weights[DISTANCE_TO_CLOSEST_BARREL_WEIGHT] = 0.2;
  }
  public void bestSoFar() {
    /*360*/weights=new double[]{1.0,-0.2,-0.062155685930777915,0.2,0.8232282797655155,0.0,0.0,0.0,0.0,0.0,0.0,0.0,};
  }
  public void someGoods() {

    /*383*/ /* boif, pkoi hisHealth>0 ? */weights=new double[]{1.0, 0.605909921743556, -0.4750970024079071, 0.2, 0.9411773000530537, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, };
    /*360*/weights=new double[]{1.0,-0.2,-0.062155685930777915,0.2,0.8232282797655155,0.0,0.0,0.0,0.0,0.0,0.0,0.0,};
    /*301*/weights=new double[]{1.0,-0.2,1.0,0.2,0.8661851714660467,0.0,0.0,0.0,0.0,0.0,0.0,0.0,};
    /*291*/weights=new double[]{1.0,-0.2,1.0,0.2,0.36883933943225267,0.0,0.0,0.0,0.0,0.0,0.0,0.0,};
    /*289*/weights=new double[]{1.0,-0.2,-0.010666765781631504,0.2,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,};
    /*283*/weights=new double[]{1.0,-0.2,1.0,0.2,0.7889258247009991,0.0,0.0,0.0,0.0,0.0,0.0,-0.11174957315434586,};
    /*283*/weights=new double[]{1.0,-0.2,-0.17764023438298104,0.2,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,};
    /*276*/weights=new double[]{1.0,-0.2,1.0,0.2,0.9211849363461877,0.0,0.0,0.0,0.0,0.0,0.0,0.0,};
    /*275*/weights=new double[]{1.0,-0.2,1.0,0.2,0.749192106535933,0.0,0.0,0.0,0.0,0.0,0.0,0.0,};
  }
  
  public void randomize() {
    Random rand = new Random(System.currentTimeMillis());
    for (int i=0;i<LAST;i++) {
      weights[i] = 1.0-2*rand.nextDouble();
    }
  }
  public void mutate() {
    Random rand = new Random(System.currentTimeMillis());
    boolean changed = false;
    while (!changed) {
      for (int i=0;i<LAST;i++) {
        if (rand.nextInt(10) == 0) {
          weights[i] = 1.0-2*rand.nextDouble();
          changed = true;
        }
      }
    }
  }

  public void output() {
    System.out.print("weights=new double[]{");
    for (int i=0;i<LAST;i++) {
      System.out.print(""+weights[i]+",");
    }
    System.out.println("};");
  }
}
