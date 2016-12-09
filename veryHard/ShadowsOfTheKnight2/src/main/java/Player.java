import java.util.*;

import org.omg.PortableInterceptor.HOLDING;

import java.io.*;
import java.math.*;

class Player {
  private static final String UNKNOWN = "UNKNWON";
  private static final String COLDER = "COLDER";
  private static final String WARMER = "WARMER";
  private static final String SAME = "SAME";

  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int W = in.nextInt(); // width of the building.
    int H = in.nextInt(); // height of the building.
    int N = in.nextInt(); // maximum number of turns before game over.

    int L = 0;
    int R = W-1;
    int oldL = L;
    int oldR = R;
    
    int Bx = in.nextInt();
    int oldBx = Bx;
    int nextBx = 0;
    int By = in.nextInt();

    int lastMoveType = 0; // 0 in between
                      // 1 > R
                      // 2 < L
    
    while (true) {
      boolean bypass = false;
      System.err.println("L="+L+" , R="+R+" , batX="+Bx);
      String bombTemp = in.next();
      
      if (UNKNOWN.equals(bombTemp)) {
        // first turn, nothing to do
      } else if (WARMER.equals(bombTemp)) {
        if (lastMoveType == 0) {
          L = L + (R-L) / 2 +1;
        } else if (lastMoveType == 1) {
          R = oldBx- (oldBx-L)/2;
        } else { // 2
          L = oldBx + (R-oldBx)/2;
        }
      } else if (COLDER.equals(bombTemp)) {
        if (lastMoveType == 0) {
          R = L + (R-L) / 2;
        } else if (lastMoveType == 1) {
          L = oldBx- (oldBx-L)/2;
        } else { // 2
          R = oldBx + (R-oldBx)/2;
        }
      } else if (SAME.equals(bombTemp)) {
        R = (R+L)/2;
        L = R;
        nextBx = L;
        bypass = true;
      }
      
      if (L == R) {
        System.err.println(" trouve");
        continue;
      }
      
      if (!bypass) {
        if (L <= Bx && Bx <= R) {
          nextBx = R - (Bx-L);
          if (nextBx == Bx) nextBx = Bx+1;
          lastMoveType = 0;
        } else {
          if (Bx > R) {
            nextBx = L;
            lastMoveType = 1;
          } else {
            nextBx = R;
            lastMoveType = 2;
          }
        }
      }
      
      System.out.println("" + nextBx + " " + By);
      oldBx = Bx;
      Bx = nextBx;
    }
  }
}