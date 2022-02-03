package connect4;

import cgutils.random.FastRandom;
import fast.read.FastReader;

public class State {
  int cells[][] = new int[9][7];
  int winner = -1;

  int choice[] = new int[9];
  int choiceFE = 0;
  private FastRandom random = new FastRandom(0); //System.currentTimeMillis());
  
  private State() {
  }
  
	public static State emptyState() {
	  State state = new State();
    for (int y = 0; y < 7; y++) {
      for (int x = 0; x < 9; x++) {
        state.cells[x][y] = -1;
      }
    }
    return state;
	}
	
  public void read(FastReader in) {
    int turnIndex = in.nextInt(); // starts from 0; As the game progresses, first player gets [0,2,4,...] and second player gets [1,3,5,...]

    for (int y = 6; y >= 0; y--) {
      char[] boardRow = in.nextChars(); // one row of the board (from top to bottom)
      for (int x = 0; x < 9; x++) {
        char v = boardRow[x];
        if (v == '.') {
          cells[x][y] = -1;
        } else {
          cells[x][y] = (v == '0' && ! Player.inverse) || (v == '1' && Player.inverse) ? 0 : 1;
        }
      }
    }
    
    debug();
    
    choiceFE = in.nextInt(); // number of unfilled columns in the board
    for (int i = 0; i < choiceFE; i++) {
      choice[i] = in.nextInt(); // a valid column index into which a chip can be dropped
    }
    int oppPreviousAction = in.nextInt(); // opponent's previous chosen column index (will be -1 for first player in the first turn)

  }

	public int findCol() {
	  int possibleChoices[] = new int[9];
	  int possibleChoicesFE = 0;
	  
	  int bestChoice = -1;
	  for (int i=0;i<choiceFE;i++) {
	    this.put(choice[i], 0);
	    
	    if (winner == 0) {
        System.err.println("I can won @ "+choice[i]);
	      bestChoice = choice[i];
	      this.remove(choice[i]);
	      break;
	    } else if (firstEmptyCell(choice[i]) != 7) {
	      // check he won't win with our direct move 
	      this.put(choice[i], 1);
	      if (winner != 1) {
	        System.err.println("He won't win if a put on "+choice[i]);
	        possibleChoices[possibleChoicesFE++] = choice[i];
	      } else {
	        System.err.println("He would win if a put on "+choice[i]);
	        this.debug();
	      }
	      this.remove(choice[i]);
	    }
	    this.remove(choice[i]);
	  }
	  
	  // check for him
    for (int i=0;i<choiceFE;i++) {
      this.put(choice[i], 1);
      
      if (winner == 1) {
        System.err.println("He can win @ "+choice[i]+" so block it");
        this.debug();
        
        bestChoice = choice[i];
        this.remove(choice[i]);
        break;
      }
      this.remove(choice[i]);
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
	    checkWinner(col, r, player);
	  }
	}

  private boolean checkWinner(int posx, int posy, int player) {
    winner = -1;
    
    // check col
    int start = Math.max(0, posy-3);
    int end = Math.min(posy+3, 6);
    int connect4 = 0;

    for (int y=start;y<=end;y++) {
      if (cells[posx][y] == player) {
        connect4++; 
        if (connect4 == 4) {
          winner = player; 
          return true;
        }
      } else {
        connect4 = 0;
      }
    }
    
    // checkRow
    start = Math.max(0, posx-3);
    end = Math.min(posx+3, 7);
    connect4 = 0;
    for (int x=start;x<=end;x++) {
      if (cells[x][posy] == player) {
        connect4++; 
        if (connect4 == 4) {
          winner = player; 
          return true;
        }
      } else {
        connect4 = 0;
      }
    }

    // diagonal upright
    connect4 = 0;
    for (int d=-3;d<4;d++) {
      int px = posx+d;
      int py = posy+d;
      if (px < 0 || py < 0) continue;
      if (px > 8 || py > 6) break;
      
      if (cells[px][py] == player) {
        connect4++; 
        if (connect4 == 4) {
          winner = player;
          return true;
        }
      } else {
        connect4 = 0;
      }
    }
    
    connect4 = 0;
    for (int d=-3;d<4;d++) {
      int px = posx+d;
      int py = posy-d;
      if (px > 8 || py < 0) continue;
      if (px < 0 || py > 6) break;
      
      if (cells[px][py] == player) {
        connect4++; 
        if (connect4 == 4) {
          winner = player;
          return true;
        }
      } else {
        connect4 = 0;
      }
    }
    
    return false;
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
        String v = cells[x][y] == -1 ? " ": (""+cells[x][y]);
        System.err.print(v);
      }
      System.err.println();
    }
  }

  private int lastFilledCell(int col) {
    int r = firstEmptyCell(col);
    return r-1;
  }
}
