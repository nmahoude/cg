package breakthrough;

import java.util.Arrays;

import fast.read.FastReader;

public class State {
  public static int ME = 1;
  public static int OPP = -1;
  public static int NEUTRAL = 0;
  
  
  int cells[] = new int[8*8];
  
  public State() {
    for (int y=0;y<8;y++) {
      for (int x=0;x<8;x++) {
        if (y < 2) cells[x+8*y] = ME;
        if (y > 5) cells[x+8*y] = OPP;
      }
    }
  }
  
  
  public void copyFrom(State model) {
    System.arraycopy(model.cells, 0, this.cells, 0, 64 );
  }
  
  public void read (FastReader in) {
    char opponentMove[] = in.nextChars(); // last move played or "None"
    if (Player.turn == 1 && opponentMove[0] != 'N'/*first letter of None*/) {
      Player.  firstPlayer = false;
    }

    if (opponentMove[0] != 'N') {
      Pos from = Pos.from(opponentMove[0], opponentMove[1]);
      Pos to = Pos.from(opponentMove[2], opponentMove[3]);
      unset(from);
      set(to, OPP);
    }

    
    printGrid();
    
    int legalMoves = in.nextInt(); // number of legal moves
    Pos bestFrom = null, bestTo = null;
    double bestMoveScore = Double.NEGATIVE_INFINITY;

    System.err.println("Legal moves : "+legalMoves);
    for (int i = 0; i < legalMoves; i++) {
      char moveString[] = in.nextChars(); // a legal move

      Pos from = Pos.from(moveString[0], moveString[1]);
      Pos to = Pos.from(moveString[2], moveString[3]);
      
      double score = to.y ; 

      // The defense should'nt move
      if (Arrays.asList(Pos.from(1, 0), Pos.from(2, 0), Pos.from(5, 0), Pos.from(6, 0)).contains(from)) {
        score -=10;
      }
      
      // count player behind me
      int backups = 0;
      for (int meX=from.x-1;meX<=from.x+1;meX++) {
        if (meX <0 || meX >7) continue;
        if (from.y == 0) continue;
        
        Pos backupPos = Pos.from(meX, from.y-1);
        if (cells[backupPos.offset] == ME ) {
          backups++;
        }
      }
      if (backups == 0) {
        score -= 10 ;
      } else {
        score+=backups;
      }
      System.err.println("backups from "+from+" is "+backups);
      
      // check if we can be taken from the to Pos
      int taken = 0;
      for (int oppX=to.x-1;oppX<=to.x+1;oppX++) {
        if (oppX <0 || oppX >7) continue;
        if (oppX == to.x) continue; // can't go straight
        if (to.y == 7) continue;
        
        Pos opp = Pos.from(oppX, to.y+1);
        if (cells[opp.offset] == OPP) {
          taken++;
        }
      }
      
      if (taken > 0) {
        score -=5;
      }
      
      // check if all opp pieces are behind
      boolean hasdefense = false;
      for (int c=0;c<64;c++) {
        if (cells[c] != OPP) continue;
        Pos opp = Pos.from(c);
        if (opp.y > to.y && Math.abs(opp.x - to.x) <= Math.abs(opp.y - to.y)) {
          System.err.println(from+" "+to+" is defended");
          hasdefense = true;
          break;
        }
      }
      if (!hasdefense) {
        score += 10_000;
        System.err.println(from+" "+to+" leave him with no defense" );
      }
      
      if (score > bestMoveScore) {
        bestMoveScore = score;
        bestFrom = from;
        bestTo = to;
      }
    }

    
    // check if we can get a piece
    for (int y=0;y<8;y++) {
      for (int x=0;x<8;x++) {
        Pos pos = Pos.from(x, y);
        if (cells[pos.offset] != ME ) continue;
        
        for (int oppX=pos.x-1;oppX<=pos.x+1;oppX++) {
          if (oppX <0 || oppX >7) continue;
          if (oppX == pos.x) continue; // can't go straight
          
          Pos opp = Pos.from(oppX, pos.y+1);
          if (cells[opp.offset] == OPP) {
            if (bestMoveScore < 5000 || opp.y < 2) {
              bestFrom = pos;
              bestTo = opp;
            }
          }
        }
      }
    }

    unset(bestFrom);
    set(bestTo, ME);
    
    System.out.println(bestFrom.output()+bestTo.output());
  }

  private void printGrid() {
    for (int y=0;y<8;y++) {
      for (int x=0;x<8;x++) {
        Pos pos = Pos.from(x, 7-y);
        System.err.print(cells[pos.offset] == NEUTRAL ? " " : cells[pos.offset] == ME ? "0" : "1");
      }
      System.err.println();
    }
  }


  private void set(Pos to, int player) {
    cells[to.offset] = player; 
  }

  private void unset(Pos from) {
    cells[from.offset] = NEUTRAL; 
  }
}
