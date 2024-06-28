package spring2022;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;

public class PosTest {
  static ThreadLocalRandom random = ThreadLocalRandom.current();
  
  @Test
  void decodePosition() throws Exception {
    
  }
  
  
  @Test
  void fastDistIsWithin2percentOfDist() throws Exception {
    
    for (int i=0;i<1;i++) {
      Pos pos1 = Pos.get(random.nextInt(State.WIDTH), random.nextInt(State.HEIGHT));
      Pos pos2 = Pos.get(random.nextInt(State.WIDTH), random.nextInt(State.HEIGHT));
      
      
      
      
      System.out.println("real dist="+pos1.dist(pos2));
      System.out.println("method1="+method1(pos1, pos2));
      // System.out.println("method2="+method2(pos1, pos2));
      System.out.println("method3="+method3(pos1, pos2));
      System.out.println("method3B="+method3Better(pos1, pos2));
      System.out.println("method3EB="+method3EventBetter(pos1, pos2));
      System.out.println("method4="+method4(pos1, pos2));
    }
  }
  
  private double method3(Pos pos1, Pos pos2) {
    double d = pos1.dist2(pos2);
    double sqrt = Double.longBitsToDouble( ( ( Double.doubleToLongBits( d )-(1l<<52) )>>1 ) + ( 1l<<61 ) );
    return sqrt;
  }

  private double method3Better(Pos pos1, Pos pos2) {
    double d = pos1.dist2(pos2);
    double sqrt = Double.longBitsToDouble( ( ( Double.doubleToLongBits( d )-(1l<<52) )>>1 ) + ( 1l<<61 ) );
    double better = (sqrt + d/sqrt)/2.0;
    return better;
  }

  private double method3EventBetter(Pos pos1, Pos pos2) {
    double d = pos1.dist2(pos2);
    double sqrt = Double.longBitsToDouble( ( ( Double.doubleToLongBits( d )-(1l<<52) )>>1 ) + ( 1l<<61 ) );
    double better = (sqrt + d/sqrt)/2.0;
    double evenbetter = (better + d/better)/2.0;
    return evenbetter;
  }

  private double method4(Pos pos1, Pos pos2) {
    int number = pos1.dist2(pos2);
    return Double.longBitsToDouble(((Double.doubleToRawLongBits(number) >> 32) + 1072632448 ) << 31);
    
  }
  
  
  private int method1(Pos pos1, Pos pos2) {
    int x = pos1.x - pos2.x;
    int y = pos1.y - pos2.y;
    
    
    if(x<0) x=-x;
    if(y<0) y=-y;
    if(x < y)
    {
      int t = x;
      x = y;
      y = t;      // ensures that x >= y
    }
    int z = (y < ((13107 * x)>>15)) ?        // * (.4)
              (x + ((y * 6310)>>15)) :       // * (.192582403)
              (((x * 27926)>>15)             // * (.852245894)
                 + ((y * 18414)>>15));      // * (.561967668)
    
    return z;
  }
}
