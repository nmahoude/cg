package ag;

public class FastRand {
  int g_seed;
  
  public FastRand(int seed) {
    g_seed = seed;
  }
  void fast_srand(int seed) {
    //Seed the generator
    g_seed = seed;
  }
  int fastrand() {
    //fastrand routine returns one integer, similar output value range as C lib.
    g_seed = (214013*g_seed+2531011);
    return (g_seed>>16)&0x7FFF;
  }
  int fastRandInt(int maxSize) {
    return fastrand() % maxSize;
  }
  int fastRandInt(int a, int b) {
    return(a + fastRandInt(b - a));
  }
  double fastRandDouble() {
    return (fastrand()) / 0x7FFF;
  }
  double fastRandDouble(double a, double b) {
    return a + ((fastrand()) / 0x7FFF)*(b-a);
  }

}
