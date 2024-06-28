package spring2023.search;

import spring2023.map.Cell;

public class SimpleCostWithGoal implements AStarCost {

  private int goal;


  public SimpleCostWithGoal(int goal) {
    this.goal = goal;
  }
  
  
  @Override
  public double ofStart(Cell start) {
    return 0;
  }
  
  
  public double costOf(Cell current, Cell next) {
    if (next.vAnts >= goal) {
      return 0.1 - 0.0001 * next.vAnts;
    } else if (next.resources > 0) {
      return 0.9;
    } else {
      return 1.0;
    }
  }

}
