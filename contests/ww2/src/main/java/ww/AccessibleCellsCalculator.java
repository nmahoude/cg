package ww;

import java.util.ArrayList;
import java.util.List;

public class AccessibleCellsCalculator {

  private GameState state;
  private Agent agent;
  private long visited;
  private boolean checkLevel = true;

  private AccessibleCellsCalculator(GameState state, Agent agent) {
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
        count += countFromCell(nextCell);
      }
    }
    return count;
  }

  public static int[] voronoi(GameState state, Agent agent0, Agent agent1) {
    boolean stop[] = new boolean[2];
    Agent agents[] = new Agent[2];
    int cells[] = new int[2];
    agents[0] = agent0;
    agents[1] = agent1;
    stop[0] = agent0.inFogOfWar();
    stop[1] = agent1.inFogOfWar();
    ArrayList<Cell> cells0 = new ArrayList<Cell>();
    ArrayList<Cell> cells1 = new ArrayList<Cell>();
    
    long visited = 0L;
    visited|= agent0.position.mask;
    visited|= agent1.position.mask;
    
    for (int i=0;i<2;i++) {
      if (stop[0] && stop[1]) break;
      if (stop[i] == true) continue;
      
      int newCells = visitCells(cells0, visited);
    }
    
    return cells;
  }

  private static int visitCells(ArrayList<Cell> cells, long visited) {
    for (Cell cell : cells) {
      for (int i=0;i<Dir.LENGTH;i++) {
        Cell nextCell = cell.neighbors[i];
        if ((nextCell.position.mask & visited) == 0 && (nextCell.height <= cell.height+1)) {
          
        }
      }
    }
    return 0;
  }

}
