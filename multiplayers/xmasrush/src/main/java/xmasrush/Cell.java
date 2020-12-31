package xmasrush;

public class Cell {
  public final static Cell WALL = new Cell(-1, -1);
  static {
  }
  public final static int UP = 0;
  public final static int RIGHT = 1;
  public final static int DOWN = 2;
  public final static int LEFT = 3;
  private static final int DIR_UP = 0b0001;
  private static final int DIR_RIGHT = 0b0010;
  private static final int DIR_DOWN = 0b0100;
  private static final int DIR_LEFT = 0b1000;
  
  public final Pos pos;
  public Cell[] neighbors = new Cell[4];
  int directions, invDir; /* 4 bits for directions*/
  int itemIndex = -1;

  
  static char directionsStr[] = new char[] {
      ' ', '╵', '╶', '└',
      '╷', '│', '┌', '├',
      '╴', '┘', '─', '┴', '┐','┤', '┬', '┼'
  };
  
  public Cell(int x, int y) {
    if (x == -1) {
      this.pos = Pos.unknown;
    } else {
      this.pos = Pos.get(x, y);
    }
    
    for (int i=0;i<4;i++) {
      neighbors[i] = WALL;
    }
  }

  public void copyCellContent(Cell from) {
    this.directions = from.directions;
    this.invDir = from.invDir;
    this.itemIndex = from.itemIndex;
  }

  
  @Override
  public String toString() {
    return "C("+directionsStr[directions]+")"+pos;
  }
  
  public void setDirections(char[] tile) {
    directions = 0;
    if (tile[UP] == '1') directions|=DIR_UP;
    if (tile[RIGHT] == '1') directions|=DIR_RIGHT;
    if (tile[DOWN] == '1') directions|=DIR_DOWN;
    if (tile[LEFT] == '1') directions|=DIR_LEFT;
    
    invDir = invDir(directions);
  }

  public int invDir(int direction) {
    int invDir = 0;
    if ((direction & DIR_UP) != 0) invDir |= DIR_DOWN;
    if ((direction & DIR_RIGHT) != 0) invDir |= DIR_LEFT;
    if ((direction & DIR_DOWN) != 0) invDir |= DIR_UP;
    if ((direction & DIR_LEFT) != 0) invDir |= DIR_RIGHT;
    return invDir;
  }

  public void setItem(char[] itemName, int itemPlayerId) {
    itemIndex = Item.getItem(itemName);
    if (itemPlayerId == 1) {
      itemIndex+=100;
    }
    
  }

  public int manhattan(Cell nextCell) {
    return pos.manhattan(nextCell.pos);
  }

  public Cell getVisitableNeighbor(int dir) {
    int dirMask= 1 << dir;
    if ((this.directions & dirMask) != 0 && (neighbors[dir].invDir & dirMask) != 0) {
      return neighbors[dir];
    } else {
      return Cell.WALL;
    }
  }

  public char debug() {
    return directionsStr[directions];
  }

  public void reset() {
    itemIndex = -1;
  }

}
