import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Solution {

  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);
    int A = in.nextInt();
    int B = in.nextInt();

    int pow = new Solution().find(A, B);
    System.out.println(pow);
  }

  int find(int A, long B) {

    System.err.println("" + A + "," + B);
    int pow = 0;

    // decompose A :
    int n = A;
    int max = n;
    int[][] primes = new int[max + 1][4];

    int maxPrime = 0;
    for (int i = 2; i <= max; i++) {
      primes[i][0] = i;
      while (n % i == 0) {
        primes[i][1]++; // count
        n /= i;
        if (i > maxPrime) {
          maxPrime = i;
        }
      }
    }
    int currentIndex = 0;
    for (int i=0;i<=max;i++) {
      if (primes[i][1] > 0) {
        primes[currentIndex][0] = i;
        primes[currentIndex][1] = primes[i][1];
        primes[currentIndex][2] = 0;
        currentIndex++;
      }
    }
    
    
    int fact = 1;
    for (int i = 2; i <= B; i++) {
      fact = i;
      for (int thePrime = 0; thePrime < currentIndex; thePrime++) {
        if (primes[thePrime][1]> 0 ) 
          while (fact % primes[thePrime][0] == 0) {
            fact /= primes[thePrime][0];
            primes[thePrime][2]++;
          }
      }
    }

    pow = 0;
    while (true) {
      boolean addPow = true;
      for (int x = 0; x < currentIndex; x++) {
        if (primes[x][1] > 0) {
          if (primes[x][2] >= primes[x][1]) {
            primes[x][2] -= primes[x][1];
          } else {
            addPow = false;
          }
        }
      }
      if (addPow) {
        pow++;
      } else {
        break;
      }
    }

    // Write an action using System.out.println()
    // To debug: System.err.println("Debug messages...");

    return pow;
  }
}
