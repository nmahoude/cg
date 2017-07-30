package tron.common;

public class Cell {
  public static final int RIGHT = 0;
  public static final int DOWN = 1;
  public static final int LEFT = 2;
  public static final int UP = 3;

  public static final Cell invalid = new Cell(Point.unknown);
  static {
    invalid.neighbors[0] = null;
    invalid.neighbors[1] = null;
    invalid.neighbors[2] = null;
    invalid.neighbors[3] = null;
    invalid.owner = 4;
    invalid.visited = 4;
  }
  
  public Point position = Point.unknown;
  public Cell neighbors[] = new Cell[4];

  public Cell(Point pos) {
    position = pos;
  }
  
  /** ------------------------- TRON SPECIFIC -------------------------*/
  public int owner = -1;
  public int visited = -1;
}
