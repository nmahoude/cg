package othello;

import java.util.Scanner;

public class State {
  final static int BOARDSIZE = 8;

  static int myId;
  static int oppId;

  long[] grids = new long[2];
  int[] scores = new int[2];
  
  public State() {
    grids[0] = grids[1] = 0;
  }
  
  public void readInit(Scanner in) {
    myId = in.nextInt();
    oppId = 1-myId;
    
    in.nextInt(); // board size ignore
  }

  public void read(Scanner in) {
    scores[0] = scores[1] = 0;
    grids[0] = grids[1] = 0;
    
    for (int y = 0; y < BOARDSIZE; y++) {
      String line = in.next(); // rows from top to bottom (viewer perspective).
      if (Player.debugInput) System.err.println("^"+line);
      for (int x=0;x<BOARDSIZE;x++) {
        
        char charAt = line.charAt(x);
        int cell;
        if (charAt == '.') {
          cell = -1;
        } else {
          cell = charAt - '0';
          scores[cell]++;
          grids[cell] |= (1L << Pos.from(x,y).offset);
        }
      }
    }
    
    int actionCount = in.nextInt(); // number of legal actions for this turn.
    for (int i = 0; i < actionCount; i++) {
      String action = in.next(); // the action
    }
  }

  public void copyFrom(State model) {
    grids[0] = model.grids[0];
    grids[1] = model.grids[1];
    scores[0] = model.scores[0];
    scores[1] = model.scores[1];
  }
  
  
  static int dx[] = new int[] { 0, 0, 1, -1, 1, 1 ,-1 ,-1};
  static int dy[] = new int[] { 1, -1, 0, 0, 1, -1, 1, -1};
  
  public int putTile(int sx, int sy, int id) {
    int oppId = 1 - id;
    grids[id] |= 1L << Pos.from(sx, sy).offset;
    
    int score = 0;
    for (int d = 0;d<dx.length;d++) {
      
      int s = 0;
      int x = sx + dx[d];
      int y = sy + dy[d];
      Pos p;
      while ((p = Pos.secureFrom(x, y)) != Pos.VOID) {
        long mask = 1L << p.offset;
        long notMask = ~(1L << p.offset);
        
        if ((grids[id] & mask) != 0) /* my tile */ {
          score += s;
          break;
        } else if ((grids[oppId] & mask) != 0) /* opp tile */{
          grids[oppId] &= notMask;
          grids[id] |=mask;
          scores[oppId]--;
          scores[id]++;
          s++; 
        } else {
          break;
        }
        
        x += dx[d];
        y += dy[d];

      }
    }
    return score;
  }
  
  
  
  public static String toBoardCoordinates(int x, int y) {
    return ""+(char)('a'+x)+""+(char)(y+'1');
  }

  // TODO optimize
  public int countNeighbors(int sx, int sy, int id) {
    int count = 0;
    for (int y=-1;y<=1;y++) {
      for (int x=-1;x<=1;x++) {
        if (x == 0 && y == 0) continue;
        Pos p = Pos.from(x + sx, y + sy);
        if (p == Pos.VOID) continue;
        if ((grids[id] & 1L << p.offset) != 0) count++;
      }
    }
    return count;
  }
  
  public void outputTestValues() {
    System.err.println("state.grids[0]="+this.grids[0]+"L;");
    System.err.println("state.grids[1]="+this.grids[1]+"L;");
    System.err.println("state.scores[0]="+this.scores[0]+";");
    System.err.println("state.scores[1]="+this.scores[1]+";");
  }
  
  public void debug() {
    for (int y = 0; y < BOARDSIZE; y++) {
      for (int x=0;x<BOARDSIZE;x++) {
        long mask = 1L << Pos.from(x,y).offset; 
        if ((grids[0] & mask) != 0) {
          System.err.print("0");
        } else if ((grids[1] & mask) != 0) {
          System.err.print("1");
        } else {
          System.err.print(" ");
        }
      }
      System.err.println();
    }
  }

  public int tileAt(int x, int y) {
    long mask = 1L << Pos.from(x,y).offset;
    if ((grids[0] & mask) != 0) return 0;
    if ((grids[1] & mask) != 0) return 1;
    return -1;
  }

  public boolean hasOppNeighbor(Pos pos, int id) {
    return (grids[1-id] & PosMask.neighbors8Masks[pos.offset]) != 0;
  }
}
