package ww;

public class Grid {
  private long gridMask;
  private Cell cells[][] ; // just to save them, don't use this
  int size;
  
  public Grid(int size) {
    this.size = size;
    
    cells = new Cell[size][size];
    for (int x=0;x<size;x++) {
      for (int y=0;y<size;y++) {
        cells[x][y] = new Cell();
        cells[x][y].position = Point.get(x, y);
        gridMask|=Point.get(x, y).mask;
      }
    }
    
    // attach all now
    for (int x=0;x<size;x++) {
      for (int y=0;y<size;y++) {
        for (Dir dir : Dir.getValues()) {
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
    gridMask&=~Point.get(x, y).mask;
  }

  public void setHeight(int x, int y, int height) {
    cells[x][y].height = height;
  }

  public void copyTo(GameState other) {
    for (int x=0;x<size;x++) {
      for (int y=0;y<size;y++) {
        cells[x][y].copyTo(other, other.grid.cells[x][y]);
      }
    }
  }

  public long toBitMask() {
    return gridMask;
  }
  
  public void debugLayer(long layer) {
    layer |= 0b1000000000000000000000000000000000000000000000000000000000000000L;
    for (int i = 0; i < size; i++) {
      System.err.println(
          new StringBuilder(
              Long.toBinaryString(layer).substring(8 * (7 - i), 8 * (7 - i) + 8)).reverse().toString().substring(0, size));
    }
  }
}
