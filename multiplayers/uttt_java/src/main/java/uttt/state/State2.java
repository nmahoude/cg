package uttt.state;

public class State2 {
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
  
  public static boolean winingGrid[] = new boolean[512];
  static {
    for (int i=0;i<512/*2^9*/;i++) {
      winingGrid[i] = false;
      for (int cl=0;cl<8;cl++) {
        if ((i & completedLines[cl]) == completedLines[cl]) {
          winingGrid[i] = true;
          break;
        }
      }
    }
  }
  
//  public static int winningMove[] = new int[262_144];
//  static {
//    for (int me = 0;me<0b11111111;me++) {
//      for (int him = 0;him<0b11111111;him++) {
//        int mask = me + him << 9;
//        winningMove[mask] = me;
//      }
//    }
//  }
  
  public static final int ALL_MASK = 0b111111111;
  public int cells[] = new int[9]; // 9 cells (2*9bits)
  public int global = 0;  // the global grid (2*9 bits)
  public int globalMask = 0; // can we play in the bit cells (9 bits)
  public int nextPlayGrid;
  public int winner = -1;
  
  public void copyFrom(State2 model) {
    this.global = model.global;
    this.globalMask = model.globalMask;
    for (int i=0;i<9;i++) {
      this.cells[i] = model.cells[i];
    }
    this.winner = model.winner;
    this.nextPlayGrid = model.nextPlayGrid; // TODO really needed ?
  }
  
  /**
   * set the bit in god position for the player
   */
  public void set(boolean player, int decalGlobal, int setMask) {
    // set the bit in the good grid
    if (player) {
      cells[decalGlobal] |= setMask;
    } else {
      cells[decalGlobal] |= (setMask << 9);
    }
    
    // check if setting the bit has made a winner
    // TODO we may optimize here as we know only the playing player can win.
    decideWinner(player, decalGlobal);
    
    
    // return the obligated index to play
    nextGridDecal(setMask);
  }

  private void decideWinner(boolean player, int decalGlobal) {
    int localWinner = winner(cells[decalGlobal] & 0b111111111, (cells[decalGlobal] >> 9));
    
    if (localWinner == 0) {
      cells[decalGlobal] = ALL_MASK; // player win all cells :)
      global |= (1 << decalGlobal);
    } else if (localWinner == 1) {
      cells[decalGlobal] = ALL_MASK << 9; // player win all cells :)
      global |= (1 << decalGlobal << 9);
    }

    // check for the global win now or draw
    if (localWinner != -1) {
      globalMask |= (1 << decalGlobal);
      
      int globalWinner = winner(global & 0b111111111, global >> 9);
      if (globalWinner == 0) {
        winner = 0;
      } else if (globalWinner == 1) {
        winner = 1;
      } else if (globalMask == ALL_MASK || globalWinner == 2) {
        int p0 = Integer.bitCount(global & ALL_MASK);
        int p1 = Integer.bitCount((global >> 9) & ALL_MASK);
        if (p0 > p1) winner = 0;
        else if (p1 > p0) winner = 1;
        else winner = 2; // tie
      } else {
        winner = -1; // no winner
      }
    }
  }
  
  /** 
   * return the decalGlobal (which grid to play)
   * based on the setMask
   * 
   */
  private void nextGridDecal(int setMask) {
    int decalGlobal = Integer.numberOfTrailingZeros(setMask);
    int gridMask = cells[decalGlobal];
    if (complete(gridMask) == ALL_MASK) {
      nextPlayGrid =  -1; // you can choose
    } else {
      nextPlayGrid = decalGlobal; // only in this one
    }
  }
  
  /*
   * return the mask of combined grids (p0 & p1)
   */
  public static int complete(int fullMask) {
    return (fullMask | ((fullMask >> 9) )) & ALL_MASK;
  }
  
  /*
   * return the winner of the grid or -1 if no winner, or 2 if draw
   */
  int winner(int p0Grid, int p1Grid) {
    if (winingGrid[p0Grid])
      return 0;
    if (winingGrid[p1Grid])
      return 1;
  
    if (((p0Grid | p1Grid) & ALL_MASK) == ALL_MASK) return 2;
    return -1;
  }
  
  
  public int winner() {
    return winner;
  }

  public boolean terminated() {
    return winner != -1;
  }

  public void debug() {
    if (nextPlayGrid != -1) {
      System.err.println("next move should be in " + nextPlayGrid);
    } else {
      System.err.println("next move can be anywhere");
    }
  }
  
  @Override
  public String toString() {
    return getCell(global);
  }
  
  public void debugCell(int index) {
    System.err.println(getCell(cells[index]));
  }
  public String getCell(int mask) {
    String result = "";
    int d = 1;
    for (int y=0;y<3;y++) {
      for (int x=0;x<3;x++) {
        if ((mask & d) != 0) result +="X";
        else if ((mask & (d << 9) ) != 0) result +="O";
        else result += " ";
        d*=2;
      }
      result+="|\r\n";
    }
    return result;
  }
}
