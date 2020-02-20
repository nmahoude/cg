package tge;

import java.util.ArrayList;
import java.util.List;

public class Cell implements Comparable {
  public static long wallIndex = 1;
  
  public Point position = Point.unknown;
  public List<Cell> registered = new ArrayList<>();
  public int from;
  
  @Override
  public int compareTo(Object o) {
    double d2 =  ((Cell)o).fScore;
    if (fScore < d2)
      return -1;
    else if (fScore > d2)
      return 1;    
    else
      return 0;
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
    invalid.from = 100;
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
    return Grid.heuristicLength(from.position, id);
  }

  public static double heuristicLength(Cell from, Cell target) {
    return from.position.manathan(target.position);
  }

  public void propagate() {
    counter = 1;
    while(!registered.isEmpty()) {
      Cell cell = registered.remove(0);
      cell.propagate();
    }
  }

  public boolean visited() {
    return from != 0;
  }

  public void visit() {
    from = 1;
  }
  
}
