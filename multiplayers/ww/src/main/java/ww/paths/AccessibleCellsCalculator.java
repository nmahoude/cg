package ww.paths;

import ww.Agent;
import ww.Cell;
import ww.Dir;
import ww.GameState;

public class AccessibleCellsCalculator {

  private GameState state;
  private Agent agent;
  private long visited;
  private boolean checkLevel = true;

  AccessibleCellsCalculator(GameState state, Agent agent) {
    this.state = state;
    this.agent = agent;
  }

  public static int count(GameState state, Agent agent) {
    AccessibleCellsCalculator acc = new AccessibleCellsCalculator(state, agent);
    return acc.count();
  }

  public static int countWithoutLevel(GameState state, Agent agent) {
    AccessibleCellsCalculator acc = new AccessibleCellsCalculator(state, agent);
    acc.checkLevel  = false;
    return acc.count();
  }

  public int count() {
    visited = 0L;
    return countFromCell(agent.cell)-1; // -1 to remove the one we are on
  }
  
  public int countFromCell(Cell cell) {
    visited|=cell.position.mask;
    if (!cell.isValid()) return 0;
    if (cell.agent != null && cell.agent != agent) return 0;
    
    int count = 1;
    // optimize dir walking
    for (int i=0;i<Dir.LENGTH;i++) {
      Cell nextCell = cell.neighbors[i];
      if ((nextCell.position.mask & visited) == 0 && (!checkLevel || nextCell.height <= cell.height+1)) {
        boolean blocker = false;
        for (Cell neighbor : nextCell.neighbors) {
          if (neighbor.isThreat(agent)) blocker = true;
        }
        if (!blocker) {
          count += countFromCell(nextCell);
        }
      }
    }
    return count;
  }

}
