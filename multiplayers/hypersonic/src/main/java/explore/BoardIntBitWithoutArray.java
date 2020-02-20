package explore;

public class BoardIntBitWithoutArray extends Board {
  int cell0, cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8,
  cell9, cell10;
  
  public BoardIntBitWithoutArray() {
    
  }
  
  public void copyFrom(Board board2) {
    BoardIntBitWithoutArray board = (BoardIntBitWithoutArray)board2;
    //System.arraycopy(board.cells, 0, cells, 0, 11);
    cell0 = board.cell0;
    cell1 = board.cell1;
    cell2 = board.cell2;
    cell3 = board.cell3;
    cell4 = board.cell4;
    cell5 = board.cell5;
    cell6 = board.cell6;
    cell7 = board.cell7;
    cell8 = board.cell8;
    cell9 = board.cell9;
    cell10 = board.cell10;
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
          int value = getCell(ny);
          if ((value & mask) != 0) {
            setCell(ny, value & ~mask);
            boxes++;
          }
        }
      }
    }
    return boxes;
  }

  private int getCell(int ny) {
    switch(ny) {
      case 0 : return cell0;
      case 1 : return cell1;
      case 2 : return cell2;
      case 3 : return cell3;
      case 4 : return cell4;
      case 5 : return cell5;
      case 6 : return cell6;
      case 7 : return cell7;
      case 8 : return cell8;
      case 9 : return cell9;
      case 10 : return cell10;
    }
    return 0;
  }

  final void setCell(int ny, int value) {
    switch(ny) {
      case 0 : cell0 = value;break;
      case 1 : cell1 = value;break;
      case 2 : cell2 = value;break;
      case 3 : cell3 = value;break;
      case 4 : cell4 = value;break;
      case 5 : cell5 = value;break;
      case 6 : cell6 = value;break;
      case 7 : cell7 = value;break;
      case 8 : cell8 = value;break;
      case 9 : cell9 = value;break;
      case 10 : cell10 = value;break;
    }
  }
  private boolean isInRange(int x, int y) {
    return (x>=0 && x<13 && y>=0 && y<11);
  }
  
}
