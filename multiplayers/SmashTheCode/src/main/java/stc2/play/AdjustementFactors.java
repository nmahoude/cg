package stc2.play;

import java.util.concurrent.ThreadLocalRandom;

import stc2.mcts.Node;

public class AdjustementFactors {
  double COL_HEIGHT_1 = -1;
  double COL_HEIGHT_2 =  0;
  double COL_HEIGHT_3 = +1;
  double COL_HEIGHT_4 = +1;
  double COL_HEIGHT_5 =  0;
  double COL_HEIGHT_6 = -1;
  
  double GROUP_COUNT_1 = -40;
  double GROUP_COUNT_2 = 10;
  double GROUP_COUNT_3 = 40;

  double POINTS_BONUS = 1; 
  double COLOR_GROUP_BONUS = 1;
  double COLUMN_BONUS = 1;
  double SKULLS_BONUS = 1;

  public void apply() {
    Node.COL_HEIGHT_1 = COL_HEIGHT_1;
    Node.COL_HEIGHT_2 = COL_HEIGHT_2;
    Node.COL_HEIGHT_3 = COL_HEIGHT_3;
    Node.COL_HEIGHT_4 = COL_HEIGHT_4;
    Node.COL_HEIGHT_5 = COL_HEIGHT_5;
    Node.COL_HEIGHT_6 = COL_HEIGHT_6;
    
    Node.GROUP_COUNT_1 = GROUP_COUNT_1;
    Node.GROUP_COUNT_2 = GROUP_COUNT_2;
    Node.GROUP_COUNT_3 = GROUP_COUNT_3;

    Node.POINTS_BONUS = POINTS_BONUS; 
    Node.COLOR_GROUP_BONUS = COLOR_GROUP_BONUS ;
    Node.COLUMN_BONUS = COLUMN_BONUS ;
    Node.SKULLS_BONUS = SKULLS_BONUS ;
  }
  
  public static AdjustementFactors random() {
    AdjustementFactors af = new AdjustementFactors();
    af.randomize();
    return af;
  }
  private void randomize() {
    COL_HEIGHT_1 = between(-5, 5);
    COL_HEIGHT_2 = between(-5, 5);
    COL_HEIGHT_3 = between(-5, 5);
    COL_HEIGHT_4 = between(-5, 5);
    COL_HEIGHT_5 = between(-5, 5);
    COL_HEIGHT_6 = between(-5, 5);
    
    GROUP_COUNT_1 = between(-5, 5);
    GROUP_COUNT_2 = between(-5, 5);
    GROUP_COUNT_3 = between(-5, 5);

    POINTS_BONUS = between(-1, 5);
    COLOR_GROUP_BONUS  = between(-5, 5);
    COLUMN_BONUS  = between(-5, 5);
    SKULLS_BONUS  = between(-5, 5);
  }

  public void mutate(AdjustementFactors af1, AdjustementFactors af2) {
    COL_HEIGHT_1 = between(af1.COL_HEIGHT_1, af2.COL_HEIGHT_1);
    COL_HEIGHT_2 = between(af1.COL_HEIGHT_2, af2.COL_HEIGHT_2);
    COL_HEIGHT_3 = between(af1.COL_HEIGHT_3, af2.COL_HEIGHT_3);
    COL_HEIGHT_4 = between(af1.COL_HEIGHT_4, af2.COL_HEIGHT_4);
    COL_HEIGHT_5 = between(af1.COL_HEIGHT_5, af2.COL_HEIGHT_5);
    COL_HEIGHT_6 = between(af1.COL_HEIGHT_6, af2.COL_HEIGHT_6);
    
    GROUP_COUNT_1 = between(af1.GROUP_COUNT_1, af2.GROUP_COUNT_1);
    GROUP_COUNT_2 = between(af1.GROUP_COUNT_2, af2.GROUP_COUNT_2);
    GROUP_COUNT_3 = between(af1.GROUP_COUNT_3, af2.GROUP_COUNT_3);

    POINTS_BONUS = between(af1.POINTS_BONUS, af2.POINTS_BONUS);
    COLOR_GROUP_BONUS  = between(af1.COLOR_GROUP_BONUS, af2.COLOR_GROUP_BONUS);
    COLUMN_BONUS  = between(af1.COLUMN_BONUS, af2.COLUMN_BONUS);
    SKULLS_BONUS  = between(af1.SKULLS_BONUS, af2.SKULLS_BONUS);
  }
  
  
  static ThreadLocalRandom random = ThreadLocalRandom.current();
  private double between(double i, double j) {
    if (j>i)
      return random.nextDouble(i,j);
    else 
      return random.nextDouble(j, i);
  }
  
  public void print() {
    System.out.println("COL_HEIGHT_1: "+COL_HEIGHT_1);
    System.out.println("COL_HEIGHT_2: "+COL_HEIGHT_2);
    System.out.println("COL_HEIGHT_3: "+COL_HEIGHT_3);
    System.out.println("COL_HEIGHT_4: "+COL_HEIGHT_4);
    System.out.println("COL_HEIGHT_5: "+COL_HEIGHT_5);
    System.out.println("COL_HEIGHT_6: "+COL_HEIGHT_6);
    
    System.out.println("GROUP_COUNT_1: "+GROUP_COUNT_1);
    System.out.println("GROUP_COUNT_2: "+GROUP_COUNT_2);
    System.out.println("GROUP_COUNT_3: "+GROUP_COUNT_3);

    System.out.println("POINTS_BONUS: "+POINTS_BONUS); 
    System.out.println("COLOR_GROUP_BONUS: "+COLOR_GROUP_BONUS) ;
    System.out.println("COLUMN_BONUS: "+COLUMN_BONUS) ;
    System.out.println("SKULLS_BONUS: "+SKULLS_BONUS) ;
    
  }
}
