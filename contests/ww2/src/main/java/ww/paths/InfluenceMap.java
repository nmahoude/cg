package ww.paths;

import cgcollections.arrays.FastArray;
import ww.Agent;
import ww.Cell;
import ww.Dir;
import ww.GameState;

public class InfluenceMap {
  static double influence[] = new double[100];
  static {
    double factor = 0.9;
    double pow = 1.0;
    for (int i=0;i<influence.length;i++) {
      influence[i] = pow;
      pow *= factor;
    }
  }
  
  public double[][] influenceMap = new double[7][7];
  {
    for (int i=0;i<7;i++) {
      influenceMap[i] = new double[7];
    }
  }
  long visited;
  FastArray<Cell> cells = new FastArray<>(Cell.class, 100);
  FastArray<Cell> nextCells = new FastArray<>(Cell.class, 100);
  
  public void calculateInfluence(GameState state, Agent agent) {
    visited = 0L;
    cells.clear();
    cells.add(agent.cell);
    int d = 0;
    while (cells.size() != 0) {
      visitCells(d++);
    }
  }
  
  private void visitCells(int depth) {
    nextCells.clear();
    
    for (int c=0;c<cells.size();c++) {
      Cell cell = cells.get(c);
      for (int i=0;i<Dir.LENGTH;i++) {
        Cell nextCell = cell.neighbors[i];
        if ((nextCell.position.mask & visited) == 0 && nextCell.height != 4 && (nextCell.height <= cell.height+1)) {
          visited|= nextCell.position.mask;
          influenceMap[nextCell.position.x][nextCell.position.y] = influence[depth];
          nextCells.add(nextCell);
        }
      }
    }
    FastArray<Cell> temp = cells;
    cells = nextCells;
    nextCells= temp;
  }
}
