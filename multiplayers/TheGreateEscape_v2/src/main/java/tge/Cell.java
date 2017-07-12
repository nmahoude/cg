package tge;

public class Cell implements Comparable<Cell> {
  public static long wallIndex = 1;
  
  public Point position = Point.unknown;
  
  @Override
  public int compareTo(Cell o) {
    return Double.compare(fScore, o.fScore);
  }
  
  @Override
  public String toString() {
    return "Cell at "+position.toString()+" ,f="+fScore+", g="+gScore;
  }
  public Cell(Point p) {
    this.position = p;
  }
  public static final Cell invalid = new Cell(Point.unknown);
  static {
    invalid.walls[0] = -1;
    invalid.walls[1] = -1;
    invalid.walls[2] = -1;
    invalid.walls[3] = -1;
  }

  public static final int RIGHT = 0;
  public static final int DOWN = 1;
  public static final int LEFT = 2;
  public static final int UP = 3;
  
  public Cell cells[] = new Cell[4];
  public long walls[] = new long[4];

  // AStar helpers
  public double gScore;
  public double fScore;
  public int counter;
  
  public static double heuristicLength(Cell from, int id) {
    if (id == 0) {
      return 8 - from.position.x;
    } else if (id == 1) {
      return from.position.x;
    } else {
      return 8-from.position.y;
    }
  }

  public static double heuristicLength(Cell from, Cell target) {
    return from.position.manathan(target.position);
  }
  
}
