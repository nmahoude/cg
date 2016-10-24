import java.util.*;

import org.omg.PortableInterceptor.HOLDING;

import java.io.*;
import java.math.*;

class Player {
    static class P {
      int x;
      int y;
      
      P() {
      }
      P(int x, int y) {
        this.x = x;
        this.y = y;
      }
    }
  
    static class Rectangle {
        int x1,y1;
        int x2,y2;
        
        Rectangle(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
        
        int[] middle() {
            return new int[] { x1 + (x2-x1)/2,  y1+(y2-y1)/2};
        }
        void debug() {
            System.err.println("x1,y1: "+x1+","+y1);
            System.err.println("  x2,y2: "+x2+","+y2);
        }
    }

    enum Cut {
      VERTICAL,
      HORIZONTAL,
      IGNORE,
    }
    private static final String UNKNOWN = "UNKNWON";
    private static final String COLDER = "COLDER";
    private static final String WARMER = "WARMER";
    private static final String SAME = "SAME";
    
    static Cut nextCut;
    private static Cut lastCut;

    static P lastPos = new P();
    static P currentPos = new P();
    static P projectedPos = new P();
    private static boolean ignoreNextCut;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int W = in.nextInt(); // width of the building.
        int H = in.nextInt(); // height of the building.
        int N = in.nextInt(); // maximum number of turns before game over.

        lastPos.x = currentPos.x;
        lastPos.y = currentPos.y;
        currentPos.x = in.nextInt();
        currentPos.y = in.nextInt();
        
        int originX = 0;
        int originY = 0;
        int sizeX = W;
        int sizeY = H;
        int lastCutY = 0;
        int lastCutX = 0;
        
        
        // game loop
        lastCut = null;
        nextCut = Cut.HORIZONTAL;
        Rectangle rect = new Rectangle(0,0,W-1,H-1);
        while (true) {
            String bombTemp = in.next(); // the direction of the bombs from batman's current location (U, UR, R, DR, D, DL, L or UL)
            System.err.println("Ignore next is "+ignoreNextCut);
            System.err.println("bombTemp: "+bombTemp);
            System.err.println("old rect:");
            rect.debug();
            System.err.println("Batman(curr): "+currentPos.x+","+currentPos.y);

            
            int deltax = lastPos.x+projectedPos.x % 2 == 0 ? 0 : 1;
            int deltay = lastPos.y+projectedPos.y % 2 == 0 ? 0 : 1;
            if (ignoreNextCut) {
              ignoreNextCut = false;
            } else if (bombTemp.equals(UNKNOWN)) {
              
            } else if (bombTemp.equals(WARMER)) {
              
              if (lastCut == Cut.VERTICAL) {
                if (projectedPos.x == rect.x1) {
                  rect.x2 = (lastPos.x+projectedPos.x+deltax) / 2;
                } else {
                  rect.x1 = (lastPos.x+projectedPos.x+deltax) / 2;
                }
              } else if (lastCut == Cut.HORIZONTAL) {
                if (projectedPos.y == rect.y1) {
                  rect.y2 = (lastPos.y+projectedPos.y+deltay) / 2;
                } else {
                  rect.y1 = (lastPos.y+projectedPos.y+deltay) / 2;
                }
              }
            } else if (bombTemp.equals(COLDER)) {
              ignoreNextCut = true;
              if (lastCut == Cut.VERTICAL) {
                if (projectedPos.x == rect.x1) {
                  rect.x1 = (lastPos.x+projectedPos.x+deltax) / 2;
                  currentPos.x = rect.x1;
                } else {
                  rect.x2 = (lastPos.x+projectedPos.x+deltax) / 2;
                  currentPos.x = rect.x2;
                }
              } else if (lastCut == Cut.HORIZONTAL) {
                if (projectedPos.y == rect.y1) {
                  rect.y1 = (lastPos.y+projectedPos.y+deltay) / 2;
                  currentPos.y = rect.y1;
                } else {
                  rect.y2 = (lastPos.y+projectedPos.y+deltay) / 2;
                  currentPos.y = rect.y2;
                }
              }
              nextCut = lastCut;
            } else if (bombTemp.equals(SAME)) {
              if (lastCut == Cut.HORIZONTAL) {
                rect.y1 = (lastPos.y+projectedPos.y+ deltay) / 2;
                rect.y2 = rect.y1;
                nextCut = Cut.VERTICAL;
                currentPos.y = rect.y1;
                System.err.println("***LOCK Y***");
              } else {
                rect.x1 = (lastPos.x+projectedPos.x+deltax) / 2;
                rect.x2 = rect.x1;
                currentPos.x = rect.x1;
                nextCut = Cut.HORIZONTAL;
                System.err.println("***LOCK X***");
              }
            }
            
            System.err.println("New rect:");
            rect.debug();

            projectedPos.x = currentPos.x;
            projectedPos.y = currentPos.y;
            if (ignoreNextCut) {
              if (nextCut == Cut.VERTICAL) {
                projectedPos.x = rect.middle()[0];
              } else {
                projectedPos.y = rect.middle()[1];
              }
            } else 
            if (nextCut == Cut.VERTICAL) {
              // find best way to cut
              if (currentPos.x - rect.x1 > rect.x2 - currentPos.x) {
                projectedPos.x = rect.x1;
              } else {
                projectedPos.x = rect.x2;
              }
            } else if (nextCut == Cut.HORIZONTAL) {
              if (currentPos.y - rect.y1 > rect.y2 - currentPos.y) {
                projectedPos.y = rect.y1;
              } else {
                projectedPos.y = rect.y2;
              }
            }
              
            System.err.println("Batman(proj): "+projectedPos.x+","+projectedPos.y);


            lastCut = nextCut;
            if (lastCut == Cut.HORIZONTAL && rect.x1 != rect.x2) {
              nextCut = Cut.VERTICAL;
            }
            if (lastCut == Cut.VERTICAL && rect.y1 != rect.y2) {
              nextCut = Cut.HORIZONTAL;
            }

            lastPos.x = currentPos.x;
            lastPos.y = currentPos.y;
            currentPos.x = projectedPos.x;
            currentPos.y = projectedPos.y;
            System.out.println(""+projectedPos.x+" "+projectedPos.y);
        }
    }
}