package explore;

public class PerfMonitor {
  private static final int PERF_SIZE = 1_000_000;
  private static Board board;
  private static Board newBoard;
  
  public static void main(String[] args) {
    initBoardIntBit();
    
    long t0 = System.nanoTime();
    for (int i=0;i<PERF_SIZE;i++) {
      newBoard.copyFrom(board);
      int value = newBoard.explode(4, 4);
      if (value != 10) {
        System.err.println("value: "+value);
        break;
      }
    }
    long t1 = System.nanoTime();
    
    System.out.println("delta: "+(t1-t0)/1000);
  }

  private static void initBoardIntBitWithoutArray() {
    board = new BoardIntBitWithoutArray();
    for (int y=0;y<11;y++) {
      ((BoardIntBitWithoutArray)board).setCell(y, 0xFFFF_FFFF);
    }
    newBoard = new BoardIntBitWithoutArray();
  }
  private static void initBoardLongBit() {
    board = new BoardLongBit();
    for (int y=0;y<11;y++) {
      ((BoardLongBit)board).cells[y] = 0xFFFF_FFFF_FFFF_FFFFL;
    }
    newBoard = new BoardLongBit();
  }

  private static void initBoardIntBit() {
    board = new BoardIntBit();
    for (int y=0;y<11;y++) {
      ((BoardIntBit)board).cells[y] = 0x0; //0xFFFF_FFFF;
    }
    newBoard = new BoardIntBit();
  }

  private static void initBoard2Dim() {
    board = new Board2Dim();
    initBoard2Dim(board);
    newBoard = new Board2Dim();
  }

  private static void initBoard1Dim() {
    board = new Board1Dim();
    initBoard1Dim(board);
    newBoard = new Board1Dim();
  }

  private static void initBoard1Dim(Board board2) {
    Board1Dim board = (Board1Dim)board2;
    for (int x=0;x<13*11;x++) {
      board.cells[x] = 1;
    }
  }

  private static void initBoard2Dim(Board board2) {
    Board2Dim board = (Board2Dim)board2;
    for (int x=0;x<13;x++) {
      for (int y=0;y<11;y++) {
        board.cells[x][y] = 1;
      }
    }
  }
}
