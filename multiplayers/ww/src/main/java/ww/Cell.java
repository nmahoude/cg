package ww;

public class Cell {
  public static final int FINAL_HEIGHT = 4;
  public static final Cell InvalidCell = new Cell();
  static {
    InvalidCell.position = Point.unknown;
    InvalidCell.height = 4;
    for (Dir dir : Dir.getValues()) {
      InvalidCell.neighbors[dir.index] = InvalidCell;
    }
  }
  public Cell neighbors[] = new Cell[8];
  
  public int height;
  public Agent agent;
  
  public boolean isHole; // for debug information
  public Point position; // for debug information
  
  @Override
  public String toString() {
    return "("+position.x+","+position.y+"), h:"+height+" occ:"+(agent != null ? agent.id : -1);
  }
  
  /**
   * Get the cell in the direction dir
   */
  public Cell get(Dir dir) {
    return neighbors[dir.index];
  }
  
  public boolean isValid() {
    return height != 4;
  }

  public void elevate() {
    height++;
  }
  public void decrease() {
    height--;
  }

  public boolean isOccupied() {
    return agent != null;
  }

  public boolean isOccupiedButNotBy(Agent by) {
    return this.agent != null && this.agent != by;
  }
  
  public boolean isThreat(Agent agent) {
    return this.agent != null &&
        ((this.agent.id >=2 && agent.id < 2) || (this.agent.id < 2 && agent.id >= 2));
  }
  
  public boolean isFriendly(Agent agent) {
    return this.agent != null && 
        ((this.agent.id >=2 && agent.id >= 2) || (this.agent.id < 2 && agent.id < 2));
  }
  public void copyTo(GameState otherState, Cell cell) {
    cell.height = this.height;
    if (agent == null) {
      cell.agent = null;
    } else {
      cell.agent = otherState.agents[agent.id];
    }
  }

  /** return the direction from this to To */
  public Dir dirTo(Cell to) {
    for (Dir dir : Dir.getValues()) {
      if (this.get(dir) == to) {
        return dir;
      }
    }
    return null; // should never happen if the cells are adjacent
  }

}
