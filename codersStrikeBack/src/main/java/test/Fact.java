import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

class Solution {

  
  public static void main(String[] args) {
    BigInteger A = BigInteger.valueOf(7);
    long B = 14;
    
    System.out.println(""+A+","+B);
    int pow = 0;
    
    List<BigInteger> primes = new ArrayList<BigInteger>();
    
    BigInteger fact = BigInteger.valueOf(1);
    for (int i = 2; i <= B; i++) {
        BigInteger value = BigInteger.valueOf(i);
        if (value.isProbablePrime(1)) {
          if (! A.divideAndRemainder(value)[1].equals(BigInteger.ZERO)) {
            primes.add(value);
            System.err.println("adding prime:"+i);
          }
        }
        fact = fact.multiply(value);
        for (BigInteger prime : primes) {
          BigInteger[] result = fact.divideAndRemainder(prime);
          if (result[1].equals(BigInteger.ZERO)) {
            fact = result[0];
          }
        }
        System.out.println("fact: "+fact.toString());
        
        boolean cont = true;
        do {
          BigInteger[] result = fact.divideAndRemainder(A);
          if (result[1].equals(BigInteger.ZERO)) {
            pow+=1;
            fact = result[0];
          } else {
            cont = false;
          }
        } while(cont);
    }
    
    // Write an action using System.out.println()
    // To debug: System.err.println("Debug messages...");

    System.out.println(pow);
  }
}
