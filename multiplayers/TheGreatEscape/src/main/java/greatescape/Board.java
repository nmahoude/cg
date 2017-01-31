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

  public boolean addWall(int wallId, int x, int y, WallOrientation wallOrientation) {
    if (wallOrientation == WallOrientation.V) {
      // check if a horizontal wall does not exists
      if (x-1>=0 && y+1<9) {
        if (cells[x-1][y].wallDown != 0 && cells[x-1][y].wallDown == cells[x][y].wallDown) {
          return false;
        }
      }
      
      if (x-1 >= 0) {
        if (cells[x-1][y].wallRight != 0) { return false; }
        cells[x-1][y].wallRight = wallId;
      }
      if (x-1 >= 0 && y+1 < 9) {
        if (cells[x-1][y+1].wallRight != 0) return false;
        cells[x-1][y+1].wallRight = wallId;
      }
      if (x < 9) {
        if (cells[x][y].wallLeft != 0) return false;
        cells[x][y].wallLeft = wallId;
        if (y+1 < 9) {
          if (cells[x][y+1].wallLeft != 0) return false;
          cells[x][y+1].wallLeft = wallId;
        }
      }
    } else {
      // Horizontal
      // check if a vertical wall does not exists
      if (y-1 >=0 ) {
        if (cells[x][y-1].wallRight != 0 && cells[x][y-1].wallRight == cells[x][y].wallRight) {
          return false;
        }
      }
      
      
      if (y-1>=0) {
        if (cells[x][y-1].wallDown != 0) return false;
        cells[x][y-1].wallDown = wallId;
        if (x+1 < 9) {
          if (cells[x+1][y-1].wallDown != 0) return false;
          cells[x+1][y-1].wallDown = wallId;
        }
      }
      if (y < 9) {
        if (cells[x][y].wallUp != 0) return false;
        cells[x][y].wallUp = wallId;
        if (x+1 < 9) {
          if (cells[x+1][y].wallUp != 0) return false;
          cells[x+1][y].wallUp= wallId;
        }
      }
    }
    return true;
  }

  public void backupCells() {
    for (int x=0;x<9;x++) {
      for (int y=0;y<9;y++) {
        cells[x][y].backup();
      }
    }
  }
  
  public void restore() {
    for (int x=0;x<9;x++) {
      for (int y=0;y<9;y++) {
        cells[x][y].restore();
      }
    }
  }
}
