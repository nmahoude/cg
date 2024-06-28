package spring2023.search;

import spring2023.map.Cell;

public class SimpleCostWithGoalWithOpp implements AStarCost {

  private int goal;
  
  public SimpleCostWithGoalWithOpp( int goal) {
    this.goal = goal;
  }
  
  
  @Override
  public double ofStart(Cell start) {
    return 0;
  }
  
  
  public double costOf(Cell base, Cell next) {
    double cost = 0.0;
  
    if (next.dedicatedBase >=0 && next.dedicatedBase != base.index) {
      cost += 1;
    }
    
    if (next.oppAnts > goal) cost += 1.0;
    
    if (next.vAnts >= goal) {
      cost += 0.1 - 0.0001 * next.vAnts;
    } else if (next.resources > 0) {
      cost+= 0.9;
    } else {
      cost+= 1.0;
    }
    return cost;
  }

}
