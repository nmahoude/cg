import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Solution {
  static final AtomicIntegerFieldUpdater<Solution> UP =
      AtomicIntegerFieldUpdater.newUpdater(Solution.class, "v");

    volatile int v;

    public int atomicIncrement() {
      return UP.getAndAdd(this, 1);
    }
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int N = in.nextInt();
        in.nextLine();
        for (int i = 0; i < N; i++) {
            String expression = in.nextLine();
        }
        
        Solution sol = new Solution();
        long t1 = System.currentTimeMillis();
        for (int j=0;j<50;j++) {
          sol.test();
        }
        long t2 = System.currentTimeMillis();
        for (int j=0;j<50;j++) {
          sol.test();
        }
        long t3 = System.currentTimeMillis();
        
        System.err.println("warmup: "+(t2-t1));
        System.err.println("next: "+(t3-t2));
        
        
        for (int i = 0; i < N; i++) {

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            System.out.println("true");
        }
    }

    private int static_int;

    private void test() {
      Map<String, String> map = new HashMap<>();
      for (int i=0;i<1000;i++) {
        map.put(""+i, ""+i);
      }
      static_int++;
    }
}