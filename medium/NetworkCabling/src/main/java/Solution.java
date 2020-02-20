import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Solution {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int N = in.nextInt();
        
        List<Integer> ys = new ArrayList<>();
        int sum = 0;
        
        int xmin = Integer.MAX_VALUE; 
        int xmax = Integer.MIN_VALUE;
        for (int i = 0; i < N; i++) {
            int X = in.nextInt();
            int Y = in.nextInt();
            System.err.println("X,Y: "+X+","+Y);
            if (xmin > X) {
                xmin = X;
            }
            if (xmax < X) {
                xmax = X;
            }
            ys.add(Y);
            sum+=Y;
        }
        Collections.sort(ys);
        
        int calcFrom = ys.get(ys.size()/2);
        
        System.err.println("min,max : "+xmin+","+xmax);
        
        // mean
        System.err.println("Middle line: "+calcFrom);
        long length = 0;
        for (int i=0;i<N;i++) {
            length += Math.abs(calcFrom - ys.get(i));
        }
        

        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");

        System.out.println(""+((xmax-xmin)+length));
    }
}