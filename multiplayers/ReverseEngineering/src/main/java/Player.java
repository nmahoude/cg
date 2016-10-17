import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.io.*;
import java.math.*;

class Player {
  // Observations
  // A fait augmenter le dernier chiffre [0][3], E le fait baisser
  // E ne semble pas pouvoir faire baisser en dessous de 6, 'A' permet de le faire remonter si il est à '6'
  // A le fait remonter jusque 21
  // si [0][3] est en dessous de 6, 'E' : il reste à sa valeur; 'A' : il reste à sa valeur
  
    private static char[] letter = { 'A', 'B', 'C', 'D', 'E'};

    private static char[] predefined = { 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', ' ' };
    
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int firstInitInput = in.nextInt();
        int secondInitInput = in.nextInt();
        int thirdInitInput = in.nextInt();

        System.err.println(""+firstInitInput+" / "+secondInitInput+ " / "+thirdInitInput);
        
        // game loop
        //for (int loop=0;loop<predefined.length;loop++) {
        while(true) {
            String firstInput = in.next();
            String secondInput = in.next();
            String thirdInput = in.next();
            String fourthInput = in.next();

            System.err.println(""+firstInput+" / "+secondInput+ " / "+thirdInput+ " / "+fourthInput);

            for (int i = 0; i < thirdInitInput; i++) {
                int fifthInput = in.nextInt();
                int sixthInput = in.nextInt();
                System.err.println(""+fifthInput+" | "+sixthInput);
            }
            
            System.out.println(letter [ThreadLocalRandom.current().nextInt(5)]);
            //System.out.println(predefined[loop]);
        }
    }
}