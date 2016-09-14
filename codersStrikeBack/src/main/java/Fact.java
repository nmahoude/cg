import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Solution {

  
  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);
    long A = in.nextInt();
    long B = in.nextInt();
    
    System.err.println(""+A+","+B);
    int pow = 0;
    
    List<Integer> primes = new ArrayList<Integer>();
    
    long fact = 1;
    for (int i = 2; i <= B; i++) {
        long value = i;
        if (BigInteger.valueOf(i).isProbablePrime(1)) {
          if ( A % i != 0) {
            primes.add(new Integer(i));
            //System.err.println("Adding prime:" +i);
          }
        }
        for (Integer prime : primes) {
          if (i % prime == 0) {
            value = value / prime;
          }
        }
        fact = fact * value;
        //System.err.println("reducing fact:"+fact+" after mult by "+value);
        for (Integer prime : primes) {
          if (fact % prime == 0) {
            fact /=prime;
          }
        }
        //System.err.println("new fact: "+fact.toString());
        
        boolean cont = true;
        do {
          if (fact % A == 0) {
            //System.err.println("adding a pow!");
            pow+=1;
            fact /=A;
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
