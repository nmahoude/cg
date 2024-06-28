package spring2023.search;

import spring2023.map.Cell;

public class MyMaximumAnts implements AStarCost {

  @Override
  public double costOf(Cell current, Cell next) {
    return 1.0 / next.myAnts;
  }

  @Override
  public double ofStart(Cell start) {
    return 0;
  }

}
