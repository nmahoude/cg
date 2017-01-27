package greatescape;

public class Board {

  Cell[][] cells = new Cell[9][9];
  
  public Board() {
    for (int x=0;x<9;x++) {
      for (int y=0;y<9;y++) {
        cells[x][y] = new Cell(x,y);
      }
    }
    
    for (int x=0;x<9;x++) {
      for (int y=0;y<9;y++) {
        cells[x][y].up = y>0 ? cells[x][y-1] : null;
        cells[x][y].down = y<(9-1) ? cells[x][y+1] : null;
        cells[x][y].left = x>0 ? cells[x-1][y] : null;
        cells[x][y].right = x<(9-1) ? cells[x+1][y] : null;
      }
    }
  }

  public void resetWalls() {
    for (int x=0;x<9;x++) {
      for (int y=0;y<9;y++) {
        cells[x][y].resetWalls();
      }
    }
  }
  public void addWall(int x, int y, String wallOrientation) {
    if ("V".equals(wallOrientation)) {
      if (x-1 > 0) {
        cells[x-1][y].canGoRight = false;
      }
      if (x-1 > 0 && y+1 < 9) {
        cells[x-1][y+1].canGoRight = false;
      }
      if (x < 9) {
        cells[x][y].canGoLeft = false;
        if (y+1 < 9) {
          cells[x][y+1].canGoLeft = false;
        }
      }
    } else {
      if (y-1>=0) {
        cells[x][y-1].canGoDown = false;
        if (x+1 < 9) {
          cells[x+1][y-1].canGoDown = false;
        }
      }
      if (y < 9) {
        cells[x][y].canGoUp = false;
        if (x+1 < 9) {
          cells[x+1][y].canGoUp= false;
        }
      }
    }
  }

  public void backupCells() {
    for (int x=0;x<9;x++) {
      for (int y=0;y<9;y++) {
        cells[x][y].backup();
      }
    }
  }
}
