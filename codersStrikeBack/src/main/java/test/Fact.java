package test;

import java.math.BigInteger;

public class Fact {

  
  public static void main(String[] args) {
    BigInteger A = BigInteger.valueOf(7);
    long B = 14;
    
    System.out.println(""+A+","+B);
    int pow = 0;

    BigInteger fact = BigInteger.valueOf(1);
    for (int i = 2; i <= B; i++) {
        fact = fact.multiply(BigInteger.valueOf(i));
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
