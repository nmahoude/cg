package ww;

public class Grid {
  private Cell cells[][] ; // just to save them, don't use this
  private int size;
  
  public Grid(int size) {
    this.size = size;
    
    cells = new Cell[size][size];
    for (int x=0;x<size;x++) {
      for (int y=0;y<size;y++) {
        cells[x][y] = new Cell();
        cells[x][y].x = x;
        cells[x][y].y = y;
      }
    }
    
    // attach all now
    for (int x=0;x<size;x++) {
      for (int y=0;y<size;y++) {
        for (Dir dir : Dir.values()) {
          if (x+dir.dx >=0 && x+dir.dx<size && y+dir.dy>=0 && y+dir.dy < size) {
            cells[x][y].neighbors[dir.index] = cells[x+dir.dx][y+dir.dy];
          } else {
            cells[x][y].neighbors[dir.index] = Cell.InvalidCell;
          }
        }
      }
    }
  }
  
  public void backup() {
    for (int x=0;x<size;x++) {
      for (int y=0;y<size;y++) {
        cells[x][y].backup();
      }
    }
  }
  
  // TODO use a cache to restore only modified cells 
  public void restore() {
    for (int x=0;x<size;x++) {
      for (int y=0;y<size;y++) {
        cells[x][y].restore();
      }
    }
  }
  
  public Cell get(int x, int y) {
    if (x== -1) return Cell.InvalidCell;
    return cells[x][y];
  }

  public void reset() {
    for (int x=0;x<size;x++) {
      for (int y=0;y<size;y++) {
        cells[x][y].agent = null;
      }
    }
  }

  public void setHole(int x, int y) {
    cells[x][y].height = 4;
    cells[x][y].isHole = true; // only for debug ...
  }

  public void setHeight(int x, int y, int height) {
    cells[x][y].height = height;
  }
}
