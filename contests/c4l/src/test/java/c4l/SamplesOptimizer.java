package c4l;

/**
 * Find the best repartition of samples to optimize score
 * 
 * @author nmahoude
 *
 */
public class SamplesOptimizer {
  private static final int DELAY1 = 0;
  private static final int DELAY2 = 10;
  private static final int DELAY3 = 20;
  
  static long Trank1 = 0;
  static long Trank2 = 0;
  static long Trank3 = 0;
  static double Prank1 = 0;
  static double Prank2 = 0;
  static double Prank3 = 0;
      
  public static void main(String[] args) {
    calculateTranks();
    
    System.out.println("Rank 1 : "+Trank1 + " -> "+Prank1);
    System.out.println("Rank 2 : "+Trank2 + " -> "+Prank2);
    System.out.println("Rank 3 : "+Trank3 + " -> "+Prank3);

    double TbestPoint = 0;
    int TbestT1 = 0;
    int TbestT2 = 0;
    int Tbdorank1 = 0;
    int Tbdorank2 = 0;
    int Tbdorank3 = 0;
    
    for (int t1=0;t1<=200;t1++) {
      double bestPoint = 0;
      int bestT1 = 0;
      int bestT2 = 0;
      int bdorank1 = 0;
      int bdorank2 = 0;
      int bdorank3 = 0;

      
      for (int t2=t1;t2<=200;t2++) {
        double point = 0;
        int xp = 0;
        int i=0;
        int dorank1 = 0;
        int dorank2 = 0;
        int dorank3 = 0;
        
        while (i<t1) {
          // do rank 1
          i += (7*3 + DELAY1 + 3*Math.max(0, Trank1-xp));
          point+=3*Prank1;
          xp+=3;
          dorank1 += 3;
        }
        while (i<t2) {
          // do rank 2
          i += (7*3 + DELAY2 + 3*Math.max(0, Trank2-xp));
          xp+=3;
          point+=3*Prank2;
          dorank2 += 3;
        }
      
        while (i<200) {
          // do rank 3
          i += (7*3 + DELAY3 + 3*Math.max(0, Trank3-xp));
          xp+=3;
          point+=3*Prank3;
          dorank3 += 3;
        }
      
        if (point > bestPoint) {
          bestPoint = point;
          bestT1 = t1;
          bestT2 = t2;
          bdorank1 = dorank1;
          bdorank2 = dorank2;
          bdorank3 = dorank3;
        }
      }
      System.out.println("T1 = "+t1);
      System.out.println("BEST : "+bestT1+" / "+bestT2+ " ==> "+bestPoint);
      System.out.println("samples/rank : "+bdorank1 +" / "+bdorank2+" / "+bdorank3);
      if (bestPoint> TbestPoint) {
        TbestPoint = bestPoint ;
        TbestT1 = bestT1 ;
        TbestT2 = bestT2;
        Tbdorank1 = bdorank1;
        Tbdorank2 = bdorank2;
        Tbdorank3 = bdorank3;
      }
    }

    System.out.println("BEST : "+TbestT1+" / "+TbestT2+ " ==> "+TbestPoint);
    System.out.println("samples/rank : "+Tbdorank1 +" / "+Tbdorank2+" / "+Tbdorank3);
  }
  
  public static void calculateTranks() {
    Trank1= (long) (1+Math.round((0+ 3+ 0+ 0+ 0 +
    0+ 0+ 0+ 2+ 1 +
    0+ 1+ 1+ 1+ 1 +
    0+ 2+ 0+ 0+ 2 +
    0+ 0+ 4+ 0+ 0 +
    0+ 1+ 2+ 1+ 1 +
    0+ 2+ 2+ 0+ 1 +
    3+ 1+ 0+ 0+ 1 +
    1+ 0+ 0+ 0+ 2 +
    0+ 0+ 0+ 0+ 3 +
    1+ 0+ 1+ 1+ 1 +
    0+ 0+ 2+ 0+ 2 +
    0+ 0+ 0+ 4+ 0 +
    1+ 0+ 1+ 2+ 1 +
    1+ 0+ 2+ 2+ 0 +
    0+ 1+ 3+ 1+ 0 +
    2+ 1+ 0+ 0+ 0 +
    0+ 0+ 0+ 3+ 0 +
    1+ 1+ 0+ 1+ 1 +
    0+ 2+ 0+ 2+ 0 +
    0+ 0+ 0+ 0+ 4 +
    1+ 1+ 0+ 1+ 2 +
    0+ 1+ 0+ 2+ 2 +
    1+ 3+ 1+ 0+ 0 +
    0+ 2+ 1+ 0+ 0 +
    3+ 0+ 0+ 0+ 0 +
    1+ 1+ 1+ 0+ 1 +
    2+ 0+ 0+ 2+ 0 +
    4+ 0+ 0+ 0+ 0 +
    2+ 1+ 1+ 0+ 1 +
    2+ 0+ 1+ 0+ 2 +
    1+ 0+ 0+ 1+ 3 +
    0+ 0+ 2+ 1+ 0 +
    0+ 0+ 3+ 0+ 0 +
    1+ 1+ 1+ 1+ 0 +
    2+ 0+ 2+ 0+ 0 +
    0+ 4+ 0+ 0+ 0 +
    1+ 2+ 1+ 1+ 0 +
    2+ 2+ 0+ 1+ 0 +
    0+ 0+ 1+ 3+ 1)) / 40.0);
    
    Prank1 = (1+Math.ceil((
        01+
        01+
        01+
        01+
        10+
        01+
        01+
        01+
        01+
        01+
        01+
        01+
        10+
        01+
        01+
        01+
        01+
        01+
        01+
        01+
        10+
        01+
        01+
        01+
        01+
        01+
        01+
        01+
        10+
        01+
        01+
        01+
        01+
        01+
        01+
        01+
        10+
        01+
        01+
        01) / 40.0));
    
    Trank2 =(long) ((
        0+ 0+ 0+ 5+ 0+
        6+ 0+ 0+ 0+ 0+
        0+ 0+ 3+ 2+ 2+
        0+ 0+ 1+ 4+ 2+
        2+ 3+ 0+ 3+ 0+
        0+ 0+ 0+ 5+ 3+
        0+ 5+ 0+ 0+ 0+
        0+ 6+ 0+ 0+ 0+
        0+ 2+ 2+ 3+ 0+
        2+ 0+ 0+ 1+ 4+
        5+ 3+ 0+ 0+ 0+
        0+ 0+ 5+ 0+ 0+
        0+ 0+ 6+ 0+ 0+
        2+ 3+ 0+ 0+ 2+
        3+ 0+ 2+ 2+ 0+
        4+ 2+ 0+ 0+ 1+
        0+ 5+ 3+ 0+ 0+
        0+ 0+ 0+ 0+ 5+
        0+ 0+ 0+ 6+ 0+
        2+ 0+ 0+ 2+ 3+
        1+ 4+ 2+ 0+ 0+
        0+ 3+ 0+ 2+ 3+
        3+ 0+ 0+ 0+ 5+
        0+ 0+ 0+ 0+ 5+
        0+ 0+ 0+ 0+ 6+
        3+ 2+ 2+ 0+ 0+
        0+ 1+ 4+ 2+ 0+
        3+ 0+ 3+ 0+ 2+
        0+ 0+ 5+ 3+ 0
        ) / 29.0); 
        
    Prank2=  ((
        20+
        30+
        10+
        20+
        10+
        20+
        20+
        30+
        10+
        20+
        20+
        20+
        30+
        10+
        10+
        20+
        20+
        20+
        30+
        10+
        20+
        10+
        20+
        20+
        30+
        10+
        20+
        10+
        20  
        ) / 29.0);
    
    Trank3 = (long) ((
        0+ 0+ 0+ 0+ 7+
        3+ 0+ 0+ 0+ 7+
        3+ 0+ 0+ 3+ 6+
        0+ 3+ 3+ 5+ 3+
        7+ 0+ 0+ 0+ 0+
        7+ 3+ 0+ 0+ 0+
        6+ 3+ 0+ 0+ 3+
        3+ 0+ 3+ 3+ 5+
        0+ 7+ 0+ 0+ 0+
        0+ 7+ 3+ 0+ 0+
        3+ 6+ 3+ 0+ 0+
        5+ 3+ 0+ 3+ 3+
        0+ 0+ 7+ 0+ 0+
        0+ 0+ 7+ 3+ 0+
        0+ 3+ 6+ 3+ 0+
        3+ 5+ 3+ 0+ 3+
        0+ 0+ 0+ 7+ 0+
        0+ 0+ 0+ 7+ 3+
        0+ 0+ 3+ 6+ 3+
        3+ 3+ 5+ 3+ 0
        ) / 20.0);
    
    Prank3 = ((
        40+
        50+
        40+
        30+
        40+
        50+
        40+
        30+
        40+
        50+
        40+
        30+
        40+
        50+
        40+
        30+
        40+
        50+
        40+
        30
        ) / 20.0);
  }
}
