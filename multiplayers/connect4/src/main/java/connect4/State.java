package connect4;

import cgutils.random.FastRandom;
import fast.read.FastReader;

public class State {
  int cells[][] = new int[9][7];
  int winner = -1;

  int choice[] = new int[9];
  int choiceFE = 0;
  private FastRandom random = new FastRandom(0); //System.currentTimeMillis());
  private boolean shouldDebug;
  
	public void read(FastReader in) {
		int turnIndex = in.nextInt(); // starts from 0; As the game progresses, first player gets [0,2,4,...] and second player gets [1,3,5,...]

		for (int y = 0; y < 7; y++) {
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
	  int bestChoice = -1;
	  for (int i=0;i<choiceFE;i++) {
	    this.put(choice[i], 0);
	    
	    if (winner == 0) {
        System.err.println("I can won @ "+choice[i]);
	      bestChoice = choice[i];
	      this.remove(choice[i]);
	      break;
	    }
	    this.remove(choice[i]);
	  }
	  
	  // check for him
    for (int i=0;i<choiceFE;i++) {
      this.put(choice[i], 1);
      
      shouldDebug = choice[i] == 4;
      if (shouldDebug) {
        System.err.println("debug grid after his move");
        this.debug();
      }
      if (winner == 1) {
        System.err.println("He can win @ "+choice[i]+" so block it");
        bestChoice = choice[i];
        this.remove(choice[i]);
        break;
      }
      this.remove(choice[i]);
    }
	  
	  
	  
	  if (bestChoice == -1) {
	    bestChoice = choice[random .nextInt(choiceFE)];
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

    if (shouldDebug) {
      System.err.println("min max for search: "+start+","+end);
    }
    for (int y=start;y<=end;y++) {
      if (cells[posx][y] == player) {
        connect4++; 
        if (shouldDebug) System.err.println(""+posx+","+y+" is opp, connect4 is "+connect4);
      } else {
        if (shouldDebug) System.err.println(""+posx+","+y+" is not opp, reseting connect4");
        connect4 = 0;
      }
      if (connect4 == 4) {
        winner = player; 
        return true;
      }
    }
    
    // checkRow
    start = Math.max(0, posx-3);
    end = Math.min(posx+3, 7);
    connect4 = 0;
    for (int x=start;x<end;x++) {
      if (cells[x][posy] == player) {
        connect4++; 
        if (shouldDebug) System.err.println(""+x+","+posy+" is opp, connect4 is "+connect4);
      } else {
        if (shouldDebug) System.err.println(""+x+","+posy+" is not opp, reseting connect4");
        connect4 = 0;
      }
      if (connect4 == 4) {
        winner = player; 
        return true;
      }
    }

    return false;
  }

  private int firstEmptyCell(int col) {
    int r=6;
	  for (;r>=0;r--) {
	    if (cells[col][r] == -1) break;
	  }
    return r;
  }
	
	public void remove(int col) {
    int r = lastFilledCell(col);
    cells[col][r] = -1;
	}

  private void debug() {
    System.err.println("State of the grid : ");
    for (int y=0;y<7;y++) {
      for (int x=0;x<9;x++) {
        String v = cells[x][y] == -1 ? " ": (""+cells[x][y]);
        System.err.print(v);
      }
      System.err.println();
    }
  }

  private int lastFilledCell(int col) {
    int r = firstEmptyCell(col);
    return r+1;
  }
}
