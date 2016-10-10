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
      } else if (row.charAt(x) == '0') {
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

  private void updateColorGroups() {
    int groupIndex = 0;
    for (int y=0;y<12;y++) {
      for (int x=0;x<6;x++) {
        int value = cells[x][y];
        if (value == EMPTY || value == SKULL) {
          groups[x][y] = 0; // special group
        } else {
          if (value == cells[x-1][y]) {
            int groupI = groups[x-1][y];
            groups[x][y] = groupI;
            groupCells[groupI]+=1;
          } else if (value == cells[x][y-1]) {
            int groupI = groups[x][y-1];
            groups[x][y] = groupI;
            groupCells[groupI]+=1;
          } else {
            groupIndex++;
            groups[x][y] = groupIndex;
            groupCells[groupIndex]=1;
          }
        }
      }
    }
  }

  private void destroyBlocks() {
    for (int y=0;y<12;y++) {
      for (int x=0;x<6;x++) {
        int color = cells[x][y];
        int destroyed = destroyNeighbours(color, x, y, 0);
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
