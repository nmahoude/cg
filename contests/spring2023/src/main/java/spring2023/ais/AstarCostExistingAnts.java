package spring2023.ais;

import spring2023.State;
import spring2023.map.Cell;
import spring2023.search.AStarCost;

public class AstarCostExistingAnts implements AStarCost {

  private State state;
  private int goal;

  public AstarCostExistingAnts(State state, int goal) {
    this.state = state;
    this.goal = goal;
  }

  @Override
  public double costOf(Cell current, Cell next) {
    if (state.cells[next.index].myAnts >= goal) {
      return 0;
    }
    return goal - state.cells[next.index].myAnts;
  }

  @Override
  public double ofStart(Cell start) {
    if (state.cells[start.index].myAnts >= goal) {
      return 0;
    }
    return goal - state.cells[start.index].myAnts;
  }

}
