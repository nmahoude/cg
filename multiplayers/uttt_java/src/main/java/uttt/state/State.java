package uttt.state;

public class State {
  Grid mainGrid = new Grid();
  public Grid grids[] = new Grid[9];

  public Grid nextPlayGrid = null;
  
  public State() {
    for (int y=0;y<3;y++) {
      for (int x=0;x<3;x++) {
        Grid grid = new Grid();
        grids[x+3*y] = grid;
        grid.baseX = 3*x;
        grid.baseY = 3*y;
      }
    }
  }

  public boolean terminated() {
    return (mainGrid.winner != -1) || mainGrid.full;
  }
  public int winner() {
    if (mainGrid.winner != -1) {
      return mainGrid.winner;
    } else if (!mainGrid.full) {
      return -1;
    } else {
      int score = 0;
      for (int i=0;i<9;i++) {
        if (grids[i].winner == 0) score++;
        else if (grids[i].winner == 1) score--;
      }
      return score > 0 ? 0 : score < 0 ? 1 : -1;
    }
  }
  public Grid set(boolean b, int opponentRow, int opponentCol) {
    return set(b, (int)(opponentCol / 3), (int)(opponentRow / 3),  
                      opponentCol % 3, opponentRow % 3);
  }
  
  public Grid set(boolean b, int gridX, int gridY, int x, int y) {
    int gridIndex = gridX + 3*gridY;
    Grid grid = grids[gridIndex];
    grid.set(b, x, y);
    if (grid.winner != -1) {
      mainGrid.set(b, gridX, gridY);
    } 
    if (grid.full) {
      boolean full = true;
      for (int i=0;i<9;i++) {
        if (grids[i].winner == -1 && !grids[i].full) {
          full = false;
          break;
        }
      }
      mainGrid.full = full;
    }
    
    nextPlayGrid = grids[x + 3 * y];
    if (nextPlayGrid.winner != -1) {
      nextPlayGrid = null;
    } else if (nextPlayGrid.full) {
      nextPlayGrid = null;
    }
    
    return grids[gridIndex];
  }
  
  public void unset(boolean b, int opponentRow, int opponentCol) {
    unset(b, (int)(opponentCol / 3), (int)(opponentRow / 3),  
                      opponentCol % 3, opponentRow % 3);
  }

  public void unset(boolean b, int gridX, int gridY, int x, int y) {
    int gridIndex = gridX + 3 * gridY;
    
    Grid grid = grids[gridIndex];
    this.mainGrid.unset(b, gridX, gridY);
    grid.unset(b, x, y);
  }

  public void copyFrom(State oldState) {
    this.mainGrid.copyFrom(oldState.mainGrid);
    for (int i=0;i<9;i++) {
      this.grids[i].copyFrom(oldState.grids[i]);
    }
    if (oldState.nextPlayGrid == null) {
      this.nextPlayGrid = null;
    } else {
      this.nextPlayGrid = grids[
                             oldState.nextPlayGrid.baseX / 3 
                             + 3* (oldState.nextPlayGrid.baseY / 3) ];
    }
  }
  
  public void debug() {
    if (nextPlayGrid != null) {
      System.err.println("next grid (baseX, baseY) :" + nextPlayGrid.baseX +" / " + nextPlayGrid.baseY);
    } else {
      System.err.println("next grid anywhere");
    }
    
    for (int y=0;y<9;y++) {
      for (int x=0;x<9;x++) {
        int gridY = y / 3;
        int gridX = x / 3;
        int gridIndex = 3*gridY + gridX;
        
        Grid grid = grids[gridIndex];
        
        int mask = 1 << (3 * (y - 3 * gridY) + (x - 3 * gridX));
        
        if ((grid.myGrid & mask) == mask) {
          System.err.print("x");
        } else if ((grid.hisGrid & mask) == mask) {
          System.err.print("o");
        } else {
          System.err.print(" ");
        }
        if (x % 3 == 2) {
          System.err.print("|");
        }
      }
      if (y % 3 == 2) {
        System.err.println();
        System.err.println("------------");
      } else {
        System.err.println();
      }
    }
  }
}
