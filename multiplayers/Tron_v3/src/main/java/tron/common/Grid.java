package tron.common;

public class Grid {
  public final int MAX_X = 30;
  public final int MAX_Y = 20;
  public Cell cells[];
  
  public Grid() {
    cells = new Cell[MAX_X * MAX_Y];
    for (int y=0;y<MAX_Y;y++) {
      for (int x=0;x<MAX_X;x++) {
        cells[x+MAX_X*y] = new Cell(Point.get(x,y));
      }
    }

    for (int y=0;y<MAX_Y;y++) {
      for (int x=0;x<MAX_X;x++) {
        cells[x+MAX_X*y].neighbors[Cell.RIGHT] = x+1 < MAX_X ? cells[(x+1)+MAX_X*y] : Cell.invalid;
        cells[x+MAX_X*y].neighbors[Cell.DOWN] = y+1 < MAX_Y ? cells[x+MAX_X*(y+1)] : Cell.invalid;
        cells[x+MAX_X*y].neighbors[Cell.LEFT] = x-1 >= 0 ? cells[(x-1)+MAX_X*y] : Cell.invalid;
        cells[x+MAX_X*y].neighbors[Cell.UP] = y-1 >= 0 ? cells[x+MAX_X*(y-1)] : Cell.invalid;
      }
    }
  }

  public void toTDD() {
    for (int y=0;y<MAX_Y;y++) {
      String l1="";
      for (int x=0;x<MAX_X;x++) {
        Cell cell = get(Point.get(x, y));
        if (cell.owner >=0) {
          l1+=(""+cell.owner);
        } else {
          l1+=" ";
        }
      }
      System.err.println(l1);
    }
  }
  public Cell get(Point position) {
    return cells[position.x+MAX_X*position.y];
  }

  public void reset() {
    for (int y=0;y<MAX_Y;y++) {
      for (int x=0;x<MAX_X;x++) {
        Cell cell = cells[x+MAX_X*y];
        cell.owner = -1;
      }
    }
  }

  public void resetVisited() {
    for (int i=0;i<MAX_X*MAX_Y;i++) {
      cells[i].visited = -1;
    }
  }
  /** -------------- TRON SPECIFIC ------------------------ */

  public void resetDeadOwner(int i) {
    for (int y=0;y<MAX_Y;y++) {
      for (int x=0;x<MAX_X;x++) {
        Cell cell = cells[x+MAX_X*y];
        if (cell.owner == i) {
          cell.owner = -1;
        }
      }
    }
  }

  public void markOwner(int x, int y, int playerId) {
    cells[x + MAX_X*y].owner = playerId;
  }

}
