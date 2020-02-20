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
        int L = in.nextInt();
        int C = in.nextInt();
        int N = in.nextInt();

        int groups[] = new int[N];        
        int dejavu[][] = new int[N][3];
        
        
        for (int i = 0; i < N; i++) {
            int pi = in.nextInt();
            groups[i] = pi;
            dejavu[i][0] = -1;
        }

        long cash = 0;
        int cursor = 0;
        int rideCursorBegin = 0;
        for (int ride=0;ride<C;ride++) {
            if (dejavu[cursor][0] != -1) {
                //System.err.println("Using a dejavu for "+cursor+" to "+dejavu[cursor][0]+" with $ "+dejavu[cursor][1]);
                cash+=dejavu[cursor][1];
                cursor = dejavu[cursor][0];
                continue;
            }
            int availablePlaces = L;
            int thisRideCash = 0;
            rideCursorBegin = cursor;
            while (true ) {
                int nextPlaces = groups[cursor];
                if (availablePlaces - nextPlaces < 0) {
                    break;
                }
                availablePlaces -= nextPlaces;
                thisRideCash+=nextPlaces;
                cursor++;
                if (cursor >= N) {
                    cursor = 0;
                }
                if (cursor == rideCursorBegin) {
                    break;
                }
            }
            cash+=thisRideCash;
            if (dejavu[rideCursorBegin][0] == -1) {
                //System.err.println("Adding a deja vu: from "+rideCursorBegin+" to "+cursor+" with cash : "+thisRideCash);
                dejavu[rideCursorBegin][0] = cursor;
                dejavu[rideCursorBegin][1] = thisRideCash;
            }
            
        }
        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");

        System.out.println(cash);
    }
}