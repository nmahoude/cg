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

    int xL = 0;
    int xR = W-1;
    
    int yL = 0;
    int yR = H-1;

    int Bx = in.nextInt();
    int By = in.nextInt();

    int cutX = 0;
    int cutY = 0;
    
    
    int lastMoveType = 0; // 0 in between
                      // 1 > R
                      // 2 < L
    int oldBx =0, oldBy = 0;
    
    boolean bypass = false;
    while (true) {
      boolean wasOdd = false;
      
      String bombTemp ;
      if (!bypass) {
        bombTemp = in.next();
      } else {
        //switch to y
        bypass = false;
        bombTemp = UNKNOWN;
      }
      
      if (xL != xR) {
        System.err.println("Look for x, xl!=xR "+xL +" / "+xR);
        if( UNKNOWN.equals(bombTemp)) {
          // do nothing
        } else if (WARMER.equals(bombTemp)) {
          xL = Math.max(oldBx+1, cutX + (wasOdd ? 1 : 0));
        } else if (COLDER.equals(bombTemp)) {
          xR = Math.min(oldBx-1, cutX - (wasOdd ? 1 : 0));
        } else if (SAME.equals(bombTemp)) { 
          xL = xR = cutX;
        }
        if (xL == xR) {
          bypass = true;
          continue;
        }
        
        oldBx = Bx;
        
        if (xR-xL % 2 == 1) {
          wasOdd = true;
          cutX = (xR-xL)/2+xL;
        } else {
          wasOdd = false;
          cutX = (xR-xL)/2+xL;
        }
        if (cutX == oldBx) {
          if (Bx > oldBx) cutX+=1; else cutX-=1;
        }
        Bx = xR-(Bx-xL);
        if (Bx == oldBx) { Bx +=1; }
      } else {
        // do the same for y !
        if( UNKNOWN.equals(bombTemp)) {
          // do nothing
        } else if (WARMER.equals(bombTemp)) {
          yL = Math.max(oldBy+1, cutY + (wasOdd ? 1 : 0));
        } else if (COLDER.equals(bombTemp)) {
          yR = Math.min(oldBy-1, cutY - (wasOdd ? 1 : 0));
        } else if (SAME.equals(bombTemp)) { 
          yL = yR = cutY;
        }
      
        oldBy = By;
        
        if (yR-yL % 2 == 1) {
          wasOdd = true;
          cutY = (yR-yL)/2+yL;
        } else {
          wasOdd = false;
          cutY = (yR-yL)/2+yL;
        }
        if (cutY == oldBy) {
          if (By > oldBy) cutY+=1; else cutY-=1;
        }
        By = yR-(By-yL);
        if (By == oldBy) { By +=1; }
        
      }
      
      System.err.println("L="+xL+" , R="+xR+" , batman was in x ="+oldBx+" and go to x="+Bx);
      System.err.println("cut : "+cutX);
      System.out.println("" + Bx + " " + By);
    }
  }
}