package stc;

public class Board {
  public static final int EMPTY = 0;
  public static final int SKULL = 9;
  int[][] cells = new int[6][12];

  int[][] groups = new int[6][12];
  int[] groupCells = new int[6*12]; // how many balls per group
  int[] heights = new int[6];
  
  public final void updateRow(int y, String row) {
    for (int x = 0; x < 6; x++) {
      if (row.charAt(x) >= '1' && row.charAt(x) <= '5') {
        cells[x][y] = row.charAt(x) - '0';
        heights[x] = y;
      } else if (row.charAt(x) == '0' || row.charAt(x) == '@') {
        cells[x][y] = SKULL;
        heights[x] = y;
      } else {
        cells[x][y] = EMPTY;
      }
    }
  }
  
  final void putBlocks(int color1, int color2, int rotation, int baseColumn) {
    int otherColumn = baseColumn;
    switch(rotation) {
      case 0:
        otherColumn = baseColumn+1;
        break;
      case 1:
        otherColumn = baseColumn;
        break;
      case 2:
        otherColumn = baseColumn-1;
        break;
      case 3:
        int temp = color1;
        color1 = color2;
        color2 = temp;
        otherColumn = baseColumn;
        break;
    }
    putBlock(color1, baseColumn);
    putBlock(color2, otherColumn);
    destroyBlocks();
  }

  final public void destroyBlocks() {
    boolean destruction = false;
    for (int y=12;--y>=0;) {
      for (int x=6;--x>=0;) {
        int color = cells[x][y];
        if (color > 0 && color <=7) {
          int destroyed = destroyNeighbours(color, x, y, 0);
          if (destroyed >= 4) {
            destruction = true;
          }
        }
      }
    }
    if (destruction) {
      updateBoard();
    }
  }

  final public void updateBoard() {
    for (int x=5;--x>=0;) {
      int index = 0;
      for (int y=0;y<12;y++) {
        int value = cells[x][y];
        if (value != 0) {
          cells[x][index++] = cells[x][y];
        }
      }
      heights[x] = index;
      for (int y=index;y<12;y++) {
        cells[x][y] = 0;
      }
    }
  }

  final int destroyNeighbours(int color, int x, int y, int count) {
    if (cells[x][y] == color) {
      cells[x][y] = EMPTY;
      count++;
      count= x < 5 ? destroyNeighbours(color, x+1, y, count) : count;
      count= x > 0 ? destroyNeighbours(color, x-1, y, count) : count;
      count= y < 11 ?destroyNeighbours(color, x, y+1, count) : count;
      count= y > 0 ? destroyNeighbours(color, x, y-1, count) : count;
      if (count < 4) {
        cells[x][y] = color;
        return 0;
      } else {
        // check for skulls
        if (x<5  && cells[x+1][y] == SKULL) cells[x+1][y] = 0;
        if (x>0  && cells[x-1][y] == SKULL) cells[x-1][y] = 0;
        if (y<11 && cells[x][y+1] == SKULL) cells[x][y+1] = 0;
        if (y>0  && cells[x][y-1] == SKULL) cells[x][y-1] = 0;
      }
    }
    return count;
  }

  final void putBlock(int color, int column) {
    cells[column][heights[column]] = color;
    heights[column]+=1;
  }

  final public void prepare() {
    heights[0] = 0;
    heights[1] = 0;
    heights[2] = 0;
    heights[3] = 0;
    heights[4] = 0;
    heights[5] = 0;
  }
  
  void debug() {
    for (int y=12-1;y>=0;y--) {
      String row = "";
      for (int x=0;x<6;x++) {
        row+=cells[x][y];
      }
      System.err.println(row);
    }
  }
}
