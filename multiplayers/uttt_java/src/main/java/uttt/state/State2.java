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
  
  public static final int ALL_MASK = 0b111111111;
  public int cells[] = new int[9];
  int global = 0;
  int globalMask = 0;
  
  int winner = -1;
  public int nextPlayGrid;
  
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
      cells[decalGlobal] |= (setMask << 16);
    }
    
    // check if setting the bit has made a winner
    // TODO we may optimize here as we know only the playing player can win.
    decideWinner(decalGlobal);
    
    
    // return the obligated index to play
    nextPlayGrid = nextGridDecal(setMask);
  }

  private void decideWinner(int decalGlobal) {
    int localWinner = winner(cells[decalGlobal], (cells[decalGlobal] >> 16));
    if (localWinner == 0) {
      cells[decalGlobal] = ALL_MASK; // player win all cells :)
      global |= (1 << decalGlobal);
    } else if (localWinner == 1) {
      cells[decalGlobal] = ALL_MASK << 16; // player win all cells :)
      global |= (1 << decalGlobal << 16);
    }

    // check for the global win now or draw
    if (localWinner != -1) {
      globalMask |= (1 << decalGlobal);
      
      int globalWinner = winner(global, global >> 16);
      if (globalWinner == 0) {
        winner = 0;
      } else if (globalWinner == 1) {
        winner = 1;
      } else if (globalMask == ALL_MASK || globalWinner == 2) {
        int p0 = Integer.bitCount(global & ALL_MASK);
        int p1 = Integer.bitCount((global >> 16) & ALL_MASK);
        if (p0 > p1) winner = 0;
        else if (p1 > p0) winner = 1;
        else winner = 2;
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
  int nextGridDecal(int setMask) {
    int decalGlobal = Integer.numberOfTrailingZeros(setMask);
    int grid = cells[decalGlobal];
    if (complete(grid) == ALL_MASK) {
      return -1; // you can choose
    } else {
      return decalGlobal; // only in this one
    }
  }
  
  /*
   * return the mask of combined grids (p0 & p1)
   */
  public static int complete(int fullMask) {
    return (fullMask | ((fullMask >> 16) )) & ALL_MASK;
  }
  
  /*
   * return the winner of the grid or -1 if no winner, or 2 if draw
   */
  int winner(int p0Grid, int p1Grid) {
    int mask;
    for (int i=8-1;i>=0;i--) {
      mask = completedLines[i];
      if ((mask & p0Grid) == mask) return 0;
      if ((mask & p1Grid) == mask) return 1;
    }
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
  
  public String getCell(int mask) {
    String result = "";
    int d = 1;
    for (int y=0;y<3;y++) {
      for (int x=0;x<3;x++) {
        if ((mask & d) != 0) result +="X";
        else if ((mask & (d << 16) ) != 0) result +="O";
        else result += " ";
        d*=2;
      }
      result+="|\r\n";
    }
    return result;
  }
}
