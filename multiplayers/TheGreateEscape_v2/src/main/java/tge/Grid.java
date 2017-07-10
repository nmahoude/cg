package tge;

public class Grid {
  public Cell cells[][] = new Cell[9][9];
  
  public Grid() {
    for (int y=0;y<9;y++) {
      for (int x=0;x<9;x++) {
        cells[x][y] = new Cell(Point.get(x,y));
      }
    }

    for (int y=0;y<9;y++) {
      for (int x=0;x<9;x++) {
        cells[x][y].cells[Cell.RIGHT] = x+1 < 9 ? cells[x+1][y] : Cell.invalid;
        cells[x][y].cells[Cell.DOWN] = y+1 < 9 ? cells[x][y+1] : Cell.invalid;
        cells[x][y].cells[Cell.LEFT] = x-1 >= 0 ? cells[x-1][y] : Cell.invalid;
        cells[x][y].cells[Cell.UP] = y-1 >= 0 ? cells[x][y-1] : Cell.invalid;
      }
    }
  }

  public void setWall(Point pos, WallOrientation wallOrientation) {
    Cell firstCell = cells[pos.x][pos.y];
    if (wallOrientation == WallOrientation.VERTICAL) {
      Cell secondCell = firstCell.cells[Cell.DOWN];
      firstCell.walls[Cell.LEFT] = Cell.wallIndex;
      secondCell.walls[Cell.LEFT] = Cell.wallIndex;
      firstCell.cells[Cell.LEFT].walls[Cell.RIGHT] = Cell.wallIndex;
      secondCell.cells[Cell.LEFT].walls[Cell.RIGHT] = Cell.wallIndex;
      Cell.wallIndex++;
    } else {
      Cell secondCell = firstCell.cells[Cell.RIGHT];
      firstCell.walls[Cell.UP] = Cell.wallIndex;
      secondCell.walls[Cell.UP]  =Cell.wallIndex;
      firstCell.cells[Cell.UP].walls[Cell.DOWN] = Cell.wallIndex;
      secondCell.cells[Cell.UP].walls[Cell.DOWN] = Cell.wallIndex;
      Cell.wallIndex++;
    }
  }

  public void toTDD() {
    for (int y=0;y<9;y++) {
      String l1="",l2="",l3="";
      for (int x=0;x<9;x++) {
        Cell cell = get(Point.get(x, y));
        l1 += " "+(cell.walls[3] != 0 ? "X" : " ") ;
        l2 += (cell.walls[2] != 0 ? "X" : " ") + ".";
      }
      System.err.println(l1);
      System.err.println(l2);
    }
  }
  public Cell get(Point position) {
    return cells[position.x][position.y];
  }

  public void reset() {
    Cell.wallIndex = 1;
    for (int y=0;y<9;y++) {
      for (int x=0;x<9;x++) {
        Cell cell = cells[x][y];
        cell.walls[0] = 0;
        cell.walls[1] = 0;
        cell.walls[2] = 0;
        cell.walls[3] = 0;
      }
    }
  }
}
