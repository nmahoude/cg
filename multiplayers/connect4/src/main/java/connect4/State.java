package connect4;

import fast.read.FastReader;

public class State {
  int turn;
  
  private static final long ALL_COLUMNFILLED_MASK = (long)Math.pow(2, 63);

  long zobrist;
  
  long mine;
  long opp;
  
  int winner = -1;
  
  private State() {
  }
  
	public static State emptyState() {
	  State state = new State();
    state.mine = 0L;
    state.opp = 0L;
    
    return state;
	}
	
  public void read(FastReader in) {
    int turnIndex = in.nextInt(); // starts from 0; As the game progresses, first player gets [0,2,4,...] and second player gets [1,3,5,...]
    
    zobrist = 0;
    winner = -1;
    this.mine = 0L;
    this.opp = 0L;
    
    for (int y = 6; y >= 0; y--) {
      char[] boardRow = in.nextChars(); // one row of the board (from top to bottom)
      for (int x = 0; x < 9; x++) {
        char v = boardRow[x];
        if (v == '.') {
        } else {
          boolean myCell = ((v == '0' || v == 'O') && ! Player.inverse) || ((v == '1' || v== 'X') && Player.inverse);
          
          long mask = 1L << (7*x + y);
          if (myCell) {
            this.mine |= mask;
          } else {
            this.opp |= mask;
          }
        }
      }
    }
    
    debug();
    int choiceCount = in.nextInt(); // number of unfilled columns in the board
    for (int i = 0; i < choiceCount; i++) {
      int c = in.nextInt();
    }
    int oppPreviousAction = in.nextInt(); // opponent's previous chosen column index (will be -1 for first player in the first turn)

  }

	public void put(int col, boolean player) {
		int r = firstEmptyCell(col);
		zobrist = zobrist ^ ZobristHash.get(player, col, r); 
		
		long mask = 1L << (7 * col + r);
		if (player) {
			mine |= mask;
		} else {
			opp |= mask;
		}

		boolean result = Connect4Checker.is4Connected(player ? mine : opp, col, r);
		if (result) {
			winner = player ? 0 : 1;
		}

		long total = mine | opp;
		if ((total & ALL_COLUMNFILLED_MASK) == ALL_COLUMNFILLED_MASK) { 
			winner = 2; // draw
		}
	}
	  
  int firstEmptyCell(int col) {
	long all = mine | opp;
	  
    int column = (int)((all >> 7*col) & 0b1111111);
    
    return lookupColumn(column);
  }
	
  
  private static int lookup[] = new int[128];
  static {
    lookup[0b0] = 0;
    lookup[0b1] = 1;
    lookup[0b11] = 2;
    lookup[0b111] = 3;
    lookup[0b1111] = 4;
    lookup[0b11111] = 5;
    lookup[0b111111] = 6;
    lookup[0b1111111] = 7; // not in the grid
  }
	private int lookupColumn(int column) {
    return lookup[column];
  }

  public void remove(int col, boolean player) {
    int r = lastFilledCell(col);
    
    zobrist = zobrist ^ ZobristHash.get(player, col, r); 

    
    long notMask = ~(1L << (7*col+r));
    mine &=notMask;
    opp &=notMask;
    
    winner = -1;
  }

  public void debug() {
    printGrid(mine, opp);
  }

  static public void printGrid(long mine, long opp) {
	System.err.println("State of the grid : ");
    for (int y=6;y>=0;y--) {
      for (int x=0;x<9;x++) {
        long mask = 1L << (7*x+y);
        if ((mine & mask) != 0 ) {
          System.err.print("O");
        } else if ((opp & mask) != 0) {
          System.err.print("X");
        } else {
          System.err.print(".");
        }
      }
      System.err.println();
    }
}

  private int lastFilledCell(int col) {
    int r = firstEmptyCell(col);
    return r-1;
  }

  public boolean canPutOn(int col) {
    return firstEmptyCell(col) != 7;
  }

  public void copyFrom(State model) {
    this.mine = model.mine;
    this.opp = model.opp;
    this.turn = model.turn;
    this.winner = model.winner;
    this.zobrist = model.zobrist;
  }

  public boolean end() {
    return winner != -1;
  }

  public void put(int x, int playerId) {
    put(x, playerId == 0);
  }

  public int getCellPlayerAt(int x, int y) {
    if (x < 0 || x>8 || y<0 || y>7) return -1;
    
    
    long mask = 1L << (7*x + y);
    
    if ((mine & mask) != 0) return 0; // P0
    else if ((opp & mask) != 0) return 1; // P1
    else return -2; // EMPTY
  }

  public void debugColumns() {
    System.err.println("Col height are: ");
    for (int c=0;c<9;c++) {
      System.err.println(""+firstEmptyCell(c)+" , ");
    }
    System.err.println("Grid is : ");
    debug();
  }
}
