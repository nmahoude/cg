package uttt.state;

public class InstaWinCalculator {
  
  /** 
   * for each possible grid, register if there is winning moves 
   * and which one (via bit set)
   */
  public static int winningMove[] = null;
  static {
    if (winningMove == null) winningMove = new int[262_144];
    
    for (int me = 0;me<0b11111111;me++) {
      if (State2.winingGrid[me]) continue;

      for (int him = 0;him<0b11111111;him++) {
        if (State2.winingGrid[him]) continue;

        int mask = me + (him << 9);
        int complete = me | him;
        
        int winningMovesMask = 0;
        for (int d = 1; d <= 0b100_000_000; d *= 2) {
          if ((complete & d) == 0) {
            int newMask = me | d;
            if (State2.winingGrid[newMask]) {
              winningMovesMask |= d;
            }
          }
        }
        winningMove[mask] = winningMovesMask; // register all winning moves
      }
    }
  }
  
  /**
   * return if, in this situation, the player has an instaWin move
   */
  public static boolean isWinningSituation(State2 state, boolean player) {
    int mask = state.global;
    if (player) {
      mask = mask & State2.ALL_MASK;
    } else {
      mask = (mask >> 9) & State2.ALL_MASK;
    }
    
    // no way to win the big grid in one 'big move' so don't look further
    int winningMoveMask = winningMove[mask];
    if (winningMoveMask == 0) return false;
    
    // ok, we may win, look if we can play in the right cell !
    if (state.nextPlayGrid != -1) {
      int winCellMask = 1 << state.nextPlayGrid;
      if ((winningMoveMask & winCellMask) == 0) return false; // we can't play in a winning cell :(
      
      // now check if we can win this little grid !
      int winningMoveLittleMask = state.cells[state.nextPlayGrid];
      if (player) {
        winningMoveLittleMask = winningMoveLittleMask & State2.ALL_MASK;
      } else {
        winningMoveLittleMask = (winningMoveLittleMask >> 9) & State2.ALL_MASK;
      }
      if (winningMove[winningMoveLittleMask] != 0) {
        return true;
      } else {
        return false;
      }
    } else {
      // hmm we can play anywhere we want, need to check all winning small grids
      int gDecal = 0;
      for (int d = 1; d <= 0b100_000_000; d *= 2) {
        if ((winningMoveMask & d) == 1) {
          int winningMoveLittleMask = state.cells[gDecal];
          if (player) {
            winningMoveLittleMask = winningMoveLittleMask & State2.ALL_MASK;
          } else {
            winningMoveLittleMask = (winningMoveLittleMask >> 9) & State2.ALL_MASK;
          }
          if (winningMove[winningMoveLittleMask] != 0) {
            return true;
          }
        }
        gDecal++;
      }
      return false;
    }
  }
}
