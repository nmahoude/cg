package random;

import java.util.Random;

public class FastRand {
  Random random;
  int g_seed;
  
  public FastRand(int seed) {
    g_seed = seed;
    random = new Random(g_seed);
  }
  public void fast_srand(int seed) {
    //Seed the generator
    g_seed = seed;
  }
  public int fastrand() {
    //fastrand routine returns one integer, similar output value range as C lib.
    g_seed = (214013*g_seed+2531011);
    return (g_seed>>16)&0x7FFF;
  }
  public int fastRandInt(int maxSize) {
    //return fastrand() % maxSize;
    return random.nextInt(maxSize);
  }
  public int fastRandInt(int a, int b) {
    return(a + fastRandInt(b - a));
  }
  public double fastRandDouble() {
    return (fastrand()) / 0x7FFF;
  }
  public double fastRandDouble(double a, double b) {
    return a + ((fastrand()) / 0x7FFF)*(b-a);
  }

}
