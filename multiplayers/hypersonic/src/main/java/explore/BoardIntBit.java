package explore;

public class BoardIntBit extends Board {
  private static final int BOMB_RANGE = 6;
  int[] cells = new int[11];
  
  int[] hHardBlockCells = new int[11];
  
  public BoardIntBit() {
    for (int y=0;y<11;y++) {
      if (y%2 == 0) {
        hHardBlockCells[y] = 0;
      } else {
        for (int x=0;x<13;x++) {
          hHardBlockCells[y] <<= 1;
          hHardBlockCells[y] += (x %2 == 0) ? 0: 1;
        }
      }
    }
  }
  
  public void copyFrom(Board board2) {
    BoardIntBit board = (BoardIntBit)board2;
    System.arraycopy(board.cells, 0, cells, 0, 11);
  }
  
  public void explodeLeft(int x, int y) {
    // prepare bitmask for right explosion
    int range_mask = (int)Math.pow(2, BOMB_RANGE)-1;
    int rh_mask = range_mask << x ; // decal to bomb pos
    int val = hHardBlockCells[y] & rh_mask; // check layer
    int first = Integer.lowestOneBit(val); // get first bit touched (interesting for hardblock)
    int firstTouched = Integer.numberOfTrailingZeros(first); // get as a deltaX
    
    hHardBlockCells[y] ^=first;
    
    System.err.println("Left");
    System.err.println("First touched : "+firstTouched);
    for (int yy=0;yy<11;yy++) {
      System.err.println(Integer.toString(hHardBlockCells[yy], 2));
    }
  }

  public void explodeRight(int x, int y) {
    int range_mask = (int)Math.pow(2, BOMB_RANGE)-1;
    int lh_mask; // decal to bomb pos
    if (x < BOMB_RANGE) {
      lh_mask = range_mask >> (BOMB_RANGE-x) ;
    } else {
      lh_mask = range_mask << (x-BOMB_RANGE) ;
    }
    int val = hHardBlockCells[y] & lh_mask; // check layer
    int first = Integer.highestOneBit(val); // get first bit touched (interesting for hardblock)
    int firstTouched = Integer.numberOfTrailingZeros(first); // get as a deltaX
    
    hHardBlockCells[y] ^=first;
    
    System.err.println("Right:");
    System.err.println("First touched : "+firstTouched);
    for (int yy=0;yy<11;yy++) {
      System.err.println(Integer.toString(hHardBlockCells[yy], 2));
    }
  }
  public int explode(int x, int y) {
    explodeRight(2, 1);
    explodeLeft(2, 1);
    
    
    
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
