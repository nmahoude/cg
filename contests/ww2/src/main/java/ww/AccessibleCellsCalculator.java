package ww;

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

  public static double countWithoutLevel(GameState state, Agent agent) {
    AccessibleCellsCalculator acc = new AccessibleCellsCalculator(state, agent);
    acc.checkLevel  = false;
    return acc.count();
  }

  public int count() {
    visited = 0L;
    return countFromCell(agent.cell.height, agent.cell)-1; // -1 to remove the one we are on
  }
  
  public int countFromCell(int fromHeight, Cell cell) {
    if (!cell.isValid()) return 0;
    if ((cell.position.mask & visited) != 0) return 0;
    if (checkLevel && cell.height > fromHeight+1) return 0;
    if (cell.agent != null && cell.agent != agent) return 0;
    
    int count = 1;
    visited|=cell.position.mask;
    for (Dir dir : Dir.values()) {
      count += countFromCell(cell.height, cell.get(dir));
    }
    return count;
  }

}
