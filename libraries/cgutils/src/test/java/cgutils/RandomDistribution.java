package cgutils;

import cgutils.random.FastRandom;

public class RandomDistribution {

  public static void main(String[] args) {
    int count[] = new int[5];
    FastRandom random = new FastRandom(13);
    
    for (int i=0;i<1_000_000;i++) {
      int index = random.nextInt(count.length);
      count[index]++;
    }
    
    for (int i=0;i<count.length;i++) {
      System.out.printf("%10d\n", count[i]);
    }
  }
}
