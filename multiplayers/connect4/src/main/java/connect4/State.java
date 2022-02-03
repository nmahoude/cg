package connect4;

import cgutils.random.FastRandom;
import fast.read.FastReader;

public class State {
  private static FastRandom random = new FastRandom(0); //System.currentTimeMillis());

  
  int cells[][] = new int[9][7];
  long mine;
  long opp;
  long all;
  
  int winner = -1;
  
  private State() {
  }
  
	public static State emptyState() {
	  State state = new State();
    for (int y = 0; y < 7; y++) {
      for (int x = 0; x < 9; x++) {
        state.cells[x][y] = -1;
      }
    }
    
    state.mine = 0L;
    state.opp = 0L;
    state.all = 0L;
    
    return state;
	}
	
  public void read(FastReader in) {
    int turnIndex = in.nextInt(); // starts from 0; As the game progresses, first player gets [0,2,4,...] and second player gets [1,3,5,...]

    this.all = 0L;
    this.mine = 0L;
    this.opp = 0L;
    
    for (int y = 6; y >= 0; y--) {
      char[] boardRow = in.nextChars(); // one row of the board (from top to bottom)
      for (int x = 0; x < 9; x++) {
        char v = boardRow[x];
        if (v == '.') {
          cells[x][y] = -1;
        } else {
          boolean myCell = ((v == '0' || v == 'O') && ! Player.inverse) || ((v == '1' || v== 'X') && Player.inverse);
          cells[x][y] = myCell ? 0 : 1;
          
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
	  int possibleChoices[] = new int[9];
	  int possibleChoicesFE = 0;
	  
	  int bestChoice = -1;
	  for (int i=0;i<9;i++) {
	    if (firstEmptyCell(i) == 7) continue;
	    this.put(i, 0);
	    
	    if (winner == 0) {
        System.err.println("I can won @ "+i);
	      bestChoice = i;
	      this.remove(i);
	      break;
	    } else if (firstEmptyCell(i) != 7) {
	      // check he won't win with our direct move 
	      this.put(i, 1);
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
      this.put(i, 1);
      
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
	    bestChoice = possibleChoices[random .nextInt(possibleChoicesFE)];
	  }
	  
	  return bestChoice;
	}
	
	
	
	public void put(int col, int player) {
	  int r = firstEmptyCell(col);
	  
	  if (r == -1) {
	    throw new IllegalArgumentException("Col "+col+" is full !");
	  } else {
	    cells[col][r] = player;
	    boolean result = Connect4Checker.is4Connected(cells, col, r);
	    if (result) {
	      winner = player;
	    }
	  }
	}

  private int firstEmptyCell(int col) {
    int r=0;
	  for (;r<7;r++) {
	    if (cells[col][r] == -1) break;
	  }
    return r;
  }
	
	public void remove(int col) {
    int r = lastFilledCell(col);
    cells[col][r] = -1;
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
}
