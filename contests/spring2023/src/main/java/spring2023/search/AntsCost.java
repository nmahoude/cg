package spring2023.search;

import spring2023.map.Cell;
import spring2023.map.Map;

public class AntsCost implements AStarCost {

  int goal;
  private int[] movablesAnts;
  private int[] fixedAnts;
  
  public AntsCost(int goal, int[] movablesAnts, int[] fixedAnts) {
    super();
    this.goal = goal;
    this.movablesAnts = movablesAnts;
    this.fixedAnts = fixedAnts;
  }

  @Override
  public double ofStart(Cell start) {
    if (goal > movablesAnts[start.index] + fixedAnts[start.index]) {
      return Double.MAX_VALUE; // not enough ants ?
    }
    if (goal <= movablesAnts[start.index] + fixedAnts[start.index]) return 0; // no cost
    
    return Math.max(0 , goal - (movablesAnts[start.index] + fixedAnts[start.index]));
  }
  
  @Override
  public double costOf(Cell current, Cell next) {
    double baseCost = 1.0;
    if (next.resources > 0) {
      double score = 0.001 * Math.min(100, next.resources);
      if (next.data.type == Map.CELL_EGGS) {
        baseCost -= score * 1.0;
      } else {
        baseCost -= score * 0.9; // eggs are better than crystals
      }
    }
    
    if (goal <= fixedAnts[next.index] + movablesAnts[next.index]) return baseCost + 0; // no cost
    
    return baseCost + Math.max(0 , goal - (movablesAnts[next.index] + fixedAnts[next.index]));
  }

}
