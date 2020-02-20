package explore;

public class Board2Dim extends Board {
  int cells[][] = new int[13][11];
  
  public Board2Dim() {
    
  }
  
  public void copyFrom(Board board2) {
    Board2Dim board = (Board2Dim)board2;
    for (int x=0;x<13;x++) {
      for (int y=0;y<11;y++) {
        cells[x][y] = board.cells[x][y];
      }
    }
  }
  
  public int explode(int x, int y) {
    int boxes = 0;
    for (int r=0;r<4;r++) {
      int dx = Board.rot[r][0];
      int dy = Board.rot[r][0];
      for (int d=1;d<6;d++) {
        int nx = x+d*dx;
        int ny = y+d*dy;
        if (isInRange(nx, ny) && cells[nx][ny] == 1) {
          cells[nx][ny] = 0;
          boxes++;
        }
      }
    }
    return boxes;
  }

  private boolean isInRange(int x, int y) {
    return (x>=0 && x<13 && y>=0 && y<11);
  }
  
}
