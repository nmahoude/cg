package spring2023.ais.newAI;

import spring2023.map.Cell;
import spring2023.map.Map;
import spring2023.map.MapData;
import spring2023.search.AStarCost;

public class AntsPathCost implements AStarCost {

  private int[] movableAnts;
  private int[] currentAnts;
  private int goal;

  public AntsPathCost(int[] movablesAnts, int[] currentAnts, int i) {
    this.movableAnts = movablesAnts;
    this.currentAnts = currentAnts;
    this.goal = i;
  }

  @Override
  public double costOf(Cell current, Cell next) {
    int neededAnts = goal;
    if (currentAnts[next.index] >= goal) return 0;
    neededAnts -= currentAnts[next.index];
    
    if (movableAnts[next.index] >= neededAnts) return 0;
    neededAnts -= movableAnts[next.index];
    
    int cost = 0;
    
    for (MapData md : Map.cellsByDistances[next.index].allCellsByDistance) {
      int canTake = Math.min(neededAnts, movableAnts[md.index]);
      neededAnts -= canTake;
      cost += canTake * Map.distances[md.index][next.index];
      if (neededAnts == 0) break;
    }
    
    if (neededAnts != 0) {
      return Double.POSITIVE_INFINITY; // can't do it
    } else {
      return cost;
    }
  }

  @Override
  public double ofStart(Cell start) {
    
    return costOf(null, start);
  }

}
