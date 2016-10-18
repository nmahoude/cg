import java.util.*;
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
        
        P projectOnCutLine(P p, Cut cut) {
          switch(cut) {
            case HORIZONTAL:
              return new P(p.x,(y1+y2)/2);
            case VERTICAL:
              return  new P((x1+x2)/2, p.y);
          }
          return null;
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
      HORIZONTAL
    }
    private static final String UNKNOWN = "UNKNWON";
    private static final String COLDER = "COLDER";
    private static final String WARMER = "WARMER";
    private static final String SAME = "SAME";
    
    static Cut nextCut;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int W = in.nextInt(); // width of the building.
        int H = in.nextInt(); // height of the building.
        int N = in.nextInt(); // maximum number of turns before game over.
        int x = in.nextInt();
        int y = in.nextInt();
        int lastX = x;
        int lastY = y;
        
        int originX = 0;
        int originY = 0;
        int sizeX = W;
        int sizeY = H;

        // game loop
        nextCut = Cut.HORIZONTAL;
        Rectangle rect = new Rectangle(0,0,W-1,H-1);
        Rectangle lastRect = rect;
        while (true) {
            String bombDir = in.next(); // the direction of the bombs from batman's current location (U, UR, R, DR, D, DL, L or UL)
            if (bombDir == UNKNOWN) {
              // 1st step
              P p = rect.projectOnCutLine(new P(x,y), nextCut);
            } else if (bombDir == WARMER) {
              
            }
            rect.debug();
            System.err.println("Batman: "+x+","+y);
            System.err.println("bombDir: "+bombDir);
            rect = rect.cut(x,y,bombDir);
            int[] pos = rect.middle();
            x = pos[0];
            y = pos[1];
            nextCut = nextCut == Cut.HORIZONTAL ? Cut.VERTICAL : Cut.HORIZONTAL;
            
            System.out.println(""+x+" "+y);
        }
    }
}