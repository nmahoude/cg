package uttt.state;

import uttt.Player;

public class Grid {
  public final static int allCells = 0b111111111;
  public final static int[] completedLines = new int[] {
      0b111000000,
      0b000111000,
      0b000000111,
      0b100100100,
      0b010010010,
      0b001001001,
      0b100010001,
      0b001010100
  };

  public static class PMove {
    public PMove(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public final int x;
    public final int y;
  }
  public static PMove possibleMoves[][] = new PMove[512][81];
  public static int possibleMovesFE[] = new int[512];
  public static int zobrist[][] = new int[9][512];
  
  static {
    for (int mask=0;mask<512;mask++) {
      possibleMovesFE[mask] = 0;
      for (int i=0;i<9;i++) {
        if ((mask & (1 << i)) == 0) {
          possibleMoves[mask][possibleMovesFE[mask]++] = new PMove(i % 3, i /3);
        }
        
        zobrist[i][mask] = Player.random.nextInt();
      }
      
    }
  }
  int winner = -1;
  int myGrid;
  int hisGrid;
  public boolean full = false;
  public int baseX;
  public int baseY;
  
  
  public int get(boolean b) {
    return b ? myGrid : hisGrid;
  }
  public int getComplete() {
    return myGrid | hisGrid;
  }

  void set(boolean who, int x, int y) {
    int temp = who ? myGrid : hisGrid;
    temp = temp | (1 << x << 3*y);
    if (who) {
      myGrid = temp;
    } else {
      hisGrid = temp;
    }
    for (int i=0;i<8;i++) {
      if ((completedLines[i] & myGrid) == completedLines[i]) {
        winner = 0;
        full = true;
        return;
      }
      if ((completedLines[i] & hisGrid) == completedLines[i]) {
        winner = 1;
        full = true;
        return;
      }
    }
    if ((getComplete() & allCells) == allCells) {
      full = true;
      return;
    }
  }
  void unset(boolean who, int x, int y) {
    winner = -1;
    full = false;
    
    int temp = who ? myGrid : hisGrid;
    temp = temp & ~(1 << x << 3*y);
    if (who) {
      myGrid = temp;
    } else {
      hisGrid = temp;
    }
  }
  public void copyFrom(Grid grid) {
    this.baseX = grid.baseX;
    this.baseY = grid.baseY;
    this.myGrid = grid.myGrid;
    this.hisGrid = grid.hisGrid;
    this.winner = grid.winner;
    this.full = grid.full;
  }
  
  @Override
  public String toString() {
    return Integer.toBinaryString(getComplete());
  }
}
