package cgutils.random;

import java.util.Random;

public class FastRandom extends Random {
  private static final long serialVersionUID = 1L;

  private static final double DOUBLE_UNIT = 0x1.0p-53; // 1.0 / (1L << 53);
  private static final float  FLOAT_UNIT  = 0x1.0p-24f; // 1.0 / (1L << 24);

  private long s0, s1;

  public FastRandom(long seed) {
    // Must be here, the only Random constructor. Has side-effects on setSeed, see below.
    super(0);

    setSeed(seed);
  }

  @Override
  public void setSeed(long seed) {
    s0 = seed;
    s1 = seed >> 14;

    if (s0 == 0 && s1 == 0) {
      s0 = 17;
      s1 = 163;
    }
  }
  
  @Override
  public boolean nextBoolean() {
    return nextLong() >= 0;
  }

  @Override
  public void nextBytes(byte[] bytes) {
    for (int i = 0, len = bytes.length; i < len; ) {
      long rnd = nextInt();
      for (int n = Math.min(len - i, 8); n-- > 0; rnd >>>= 8) {
        bytes[i++] = (byte) rnd;
      }
    }
  }

  @Override
  public double nextDouble() {
    return (nextLong() >>> 11) * DOUBLE_UNIT; 
  }

  /**
   * 
   * @param borneMax
   * @return [0 ; borneMax]
   */
  public double nextDouble(double borneMax) {
    return nextDouble() * borneMax;
  }
  
  /**
   * 
   * Example : [-1, 1]
   * @param borneMin
   * @param borneMax
   * @return [borneMin ; borneMax]
   */
  public double nextDouble(double borneMin, double borneMax) {
    return borneMin + nextDouble() * (borneMax-borneMin);
  }
  
  
  @Override
  public float nextFloat() {
    return (nextInt() >>> 8) * FLOAT_UNIT; 
  }
  
  @Override
  public int nextInt() {
    return (int) nextLong();
  }
  
  @Override
  public int nextInt(int n) {
    // Leave superclass's implementation.
    return super.nextInt(n);
  }

  public int nextInt(int a, int b) {
    return a + nextInt(b-a);
  }
  @Override
  public double nextGaussian() {
    // Leave superclass's implementation.
    return super.nextGaussian();
  }

  @Override
  public long nextLong() {
    final long s0 = this.s0;
    long s1 = this.s1;
    final long result = s0 + s1;
    s1 ^= s0;
    this.s0 = Long.rotateLeft(s0, 55) ^ s1 ^ s1 << 14;
    this.s1 = Long.rotateLeft(s1, 36);
    return result; 
  }
  
  @Override
  protected int next(int bits) {
    return ((int) nextLong()) >>> (32 - bits);
  }  
}
