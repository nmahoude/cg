package explore;

public class BoardLongBit extends Board {
  long cells[] = new long[11];
  
  public BoardLongBit() {
    
  }
  
  public void copyFrom(Board board2) {
    BoardLongBit board = (BoardLongBit)board2;
    System.arraycopy(board.cells, 0, cells, 0, 11);
    
  }
  
  public int explode(int x, int y) {
    int boxes = 0;
    for (int r=0;r<4;r++) {
      int dx = Board.rot[r][0];
      int dy = Board.rot[r][0];
      for (int d=1;d<6;d++) {
        int nx = x+d*dx;
        int ny = y+d*dy;
        if (isInRange(nx, ny)) {
          int mask = 1 << (nx);
          if ((cells[ny] & mask) != 0) {
            cells[ny] = cells[ny] & ~mask;
            boxes++;
          }
        }
      }
    }
    return boxes;
  }

  private boolean isInRange(int x, int y) {
    return (x>=0 && x<13 && y>=0 && y<11);
  }
  
}
