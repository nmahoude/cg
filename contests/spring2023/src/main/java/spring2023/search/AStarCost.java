package spring2023.search;

import spring2023.map.Cell;

public interface AStarCost {

  public double costOf(Cell current, Cell next);

  public double ofStart(Cell start);
}
