package spring2023.search;

import spring2023.map.Cell;

public class OppMaximumAnts implements AStarCost {

  @Override
  public double costOf(Cell current, Cell next) {
    return 1.0 / next.oppAnts;
  }

  @Override
  public double ofStart(Cell start) {
    return 0;
  }

}
