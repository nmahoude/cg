package explore;

public class Board1Dim extends Board {
  int cells[] = new int[13*11];
  
  public Board1Dim() {
    
  }
  
  public void copyFrom(Board board2) {
    Board1Dim board = (Board1Dim)board2;
    System.arraycopy(board.cells, 0, cells, 0, 13*11);
    
  }
  
  public int explode(int x, int y) {
    int boxes = 0;
    for (int r=0;r<4;r++) {
      int dx = Board.rot[r][0];
      int dy = Board.rot[r][0];
      for (int d=1;d<6;d++) {
        int nx = x+d*dx;
        int ny = y+d*dy;
        int i = nx+13*ny;
        if ((nx>=0 && nx<13 && ny>=0 && ny<11) && cells[i] == 1) {
          cells[i] = 0;
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
