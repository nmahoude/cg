package othello;

import java.util.Scanner;

public class State {
  final static int BOARDSIZE = 8;

  static int id;
  static int oppId;

  int[][] grid;
  int[] scores = new int[2];
  
  public State() {
    grid = new int[BOARDSIZE][BOARDSIZE];
  }
  
  public void readInit(Scanner in) {
    id = in.nextInt();
    oppId = 1-id;
    
    in.nextInt(); // board size ignore
  }

  public void read(Scanner in) {
    scores[0] = scores[1] = 0;
    
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
        }
        grid[x][y] = cell;
      }
    }
    
    int actionCount = in.nextInt(); // number of legal actions for this turn.
    for (int i = 0; i < actionCount; i++) {
      String action = in.next(); // the action
    }
  }

  public void copyFrom(State model) {
    for (int y = 0; y < BOARDSIZE; y++) {
      for (int x=0;x<BOARDSIZE;x++) {
        grid[x][y] = model.grid[x][y];
      }
    }
    scores[0] = model.scores[0];
    scores[1] = model.scores[1];
  }
  
  
  static int dx[] = new int[] { 0, 0, 1, -1, 1, 1 ,-1 ,-1};
  static int dy[] = new int[] { 1, -1, 0, 0, 1, -1, 1, -1};
  
  public int putTile(int sx, int sy, int id) {
    int oppId = 1 - id;
    grid[sx][sy] = id;
    
    int score = 0;
    for (int d = 0;d<dx.length;d++) {
      
      int s = 0;
      int x = sx + dx[d];
      int y = sy + dy[d];
      while (isOnBoard(x,y)) {

        if (grid[x][y] == -1) {
          break;
        } else if (grid[x][y] == oppId) {
          grid[x][y] = id;
          scores[oppId]--;
          scores[id]++;
          s++; 
        } else if (grid[x][y] == id){
          score += s;
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

  public static boolean isOnBoard(int x, int y) {
    if (x < 0 || x>=BOARDSIZE) return false;
    if (y<=0  || y>=BOARDSIZE) return false;
    return true;
  }

  public int countNeighbors(int sx, int sy, int id) {
    int count = 0;
    for (int y=-1;y<=1;y++) {
      for (int x=-1;x<=1;x++) {
        if (x == 0 && y == 0) continue;
        if (!isOnBoard(x + sx, y + sy)) continue;
        if (grid[x+sx][y+sy] == id) count++;
      }
    }
    return count;
  }
  
  
  public void debug() {
    for (int y = 0; y < BOARDSIZE; y++) {
      for (int x=0;x<BOARDSIZE;x++) {
        System.err.print(grid[x][y] == -1 ? " " : grid[x][y]);
      }
      System.err.println();
    }
  }
}
