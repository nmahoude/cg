package connect4;

import cgutils.random.FastRandom;
import fast.read.FastReader;

public class State {
  private static FastRandom random = new FastRandom(0); //System.currentTimeMillis());

  State parent;
  State childs[] = new State[9];
  int childsFE = 0;
  
  int possibleColumns[] = new int[9];
  int possibleColumnsFE = 0;
  
  long wins;
  long count;
  boolean turn;
  
  
  
  long mine;
  long opp;
  long all;
  
  int winner = -1;
  
  private State() {
  }
  
	public static State emptyState() {
	  State state = new State();
    state.mine = 0L;
    state.opp = 0L;
    state.all = 0L;
    
    state.possibleColumnsFE =0;
    for (int i=0;i<9;i++) {
      state.possibleColumns[state.possibleColumnsFE++] = i;
    }
    return state;
	}
	
  public void read(FastReader in) {
    int turnIndex = in.nextInt(); // starts from 0; As the game progresses, first player gets [0,2,4,...] and second player gets [1,3,5,...]

    winner = -1;
    this.all = 0L;
    this.mine = 0L;
    this.opp = 0L;
    this.turn = true;
    
    possibleColumnsFE = 0;
    for (int y = 6; y >= 0; y--) {
      char[] boardRow = in.nextChars(); // one row of the board (from top to bottom)
      for (int x = 0; x < 9; x++) {
        char v = boardRow[x];
        if (v == '.') {
          if (y == 0) {
            possibleColumns[possibleColumnsFE++] = x;
          }
        } else {
          boolean myCell = ((v == '0' || v == 'O') && ! Player.inverse) || ((v == '1' || v== 'X') && Player.inverse);
          
          long mask = 1L << (7*x + y);
          all |= mask;
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

  public int findCol() {
    return findCol(null, 0);
  }
	public int findCol(int forbidenCols[], int forbidenColsFE) {
	  
	  int possibleChoices[] = new int[9];
	  int possibleChoicesFE = 0;
	  
	  int bestChoice = -1;
	  for (int i=0;i<9;i++) {
	    boolean forbiden = false;
	    for (int k=0;k<forbidenColsFE;k++) {
	      if (forbidenCols[k] == i) {
	        forbiden = true;
	        break;
	      }
	    }
	    if (forbiden) continue;
	    if (firstEmptyCell(i) == 7) continue;
	    
	    
	    this.put(i, true);
	    
	    if (winner == 0) {
        System.err.println("I can won @ "+i);
	      bestChoice = i;
	      this.remove(i);
	      break;
	    } else if (firstEmptyCell(i) != 7) {
	      // check he won't win with our direct move 
	      this.put(i, false);
	      if (winner != 1) {
	        System.err.println("He won't win if I put on "+i);
	        possibleChoices[possibleChoicesFE++] = i;
	      } else {
	        System.err.println("He would win if i put on "+i);
	        this.debug();
	      }
	      this.remove(i);
	    }
	    this.remove(i);
	  }
	  
	  // check for him
	  for (int i=0;i<9;i++) {
      if (firstEmptyCell(i) == 7) continue;
      this.put(i, false);
      
      if (winner == 1) {
        System.err.println("He can win @ "+i+" so block it");
        this.debug();
        
        bestChoice = i;
        this.remove(i);
        break;
      }
      this.remove(i);
    }
	  
	  
	  
	  if (bestChoice == -1) {
	    if (possibleChoicesFE == 0) {
	      System.err.println("No available cols, we lost ...");
	      System.err.println("TODO : find the less penalisable column ?");
	      bestChoice = 0; // lost
	    } else {
	      bestChoice = possibleChoices[random .nextInt(possibleChoicesFE)];
	    }
	  }
	  
	  return bestChoice;
	}
	
	
	
	public void put(int col, boolean player) {
	  int r = firstEmptyCell(col);
	  
	  if (r == -1) {
	    throw new IllegalArgumentException("Col "+col+" is full !");
	  } else {
	    if (r == 6) {
	      // remove column from possible
	      for (int i=0;i<possibleColumnsFE;i++) {
	        if (possibleColumns[r] == col) {
	          possibleColumns[r] = possibleColumns[possibleColumnsFE-1];
	          possibleColumnsFE--;
	          break;
	        }
	      }
	    }
	    
      long mask = 1L << (7*col+r);
      all |=mask;
	    if (player) {
	      mine |=mask;
	    } else {
	      opp |=mask;
	    }
	    
	    boolean result = Connect4Checker.is4Connected(player ? mine : opp, col, r);
	    if (result) {
	      winner = player ? 0 : 1;
	    }
	  }
	  
	  if (possibleColumnsFE == 0) {
	    winner = 2;
	  }
	}

  int firstEmptyCell(int col) {
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

  public void remove(int col) {
    int r = lastFilledCell(col);
    if (r == 6) {
      // remettre dans les possibilités
      possibleColumns[possibleColumnsFE++] = col;
    }
    
    long notMask = ~(1L << (7*col+r));
    all &=notMask;
    mine &=notMask;
    opp &=notMask;
    
    winner = -1;
	}

  private void debug() {
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

  public void init(State parent) {
    this.parent = parent;
    this.childsFE = 0;
    
    this.wins = 0;
    this.count = 0;
    
    this.all = parent.all;
    this.mine = parent.mine;
    this.opp = parent.opp;
    this.winner = parent.winner;
  }

  public boolean canPutOn(int col) {
    return firstEmptyCell(col) != 7;
  }

  public void copyFrom(State model) {
    this.all = model.all;
    this.mine = model.mine;
    this.opp = model.opp;
    this.turn = model.turn;
    this.winner = model.winner;
  }

  public boolean end() {
    return winner != -1;
  }

  public void put(int col, int i) {
    put(col, i == 0);
  }
}
