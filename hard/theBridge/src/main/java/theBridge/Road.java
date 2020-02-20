package theBridge;

public class Road {
  int width;
  int height = 4;
  int cells[][];
  public void init(String... rows) {
    this.width = rows[0].length();
    cells = new int[width][4];

    for (int y=0;y<rows.length;y++) {
      for (int x=0;x<rows[y].length();x++) {
        cells[x][y] = (rows[y].charAt(x) == '.') ? 1 : 0;
      }
    }
  }

}
