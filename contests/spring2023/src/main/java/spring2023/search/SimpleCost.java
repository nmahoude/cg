package spring2023.search;

import spring2023.map.Cell;

public class SimpleCost implements AStarCost {

  @Override
  public double ofStart(Cell start) {
    return 0;
  }
  
  
  public double costOf(Cell current, Cell next) {
    if (next.vAnts > 0) {
      return 0.1 - 0.0001 * next.vAnts;
    } else if (next.resources > 0) {
      return 0.9;
    } else {
      return 1.0;
    }
  }

}
