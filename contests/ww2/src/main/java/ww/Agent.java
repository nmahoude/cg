package ww;

public class Agent {
  public final int id;
  
  public Point position;
  public Cell cell;
  public int score;
  
  public Agent(int id) {
    this.id = id;
  }

  public void copyTo(GameState otherState, Agent agent) {
    agent.position = position;
    agent.cell = otherState.grid.get(position.x, position.y);
    agent.score = score;
  }

  public boolean inFogOfWar() {
    return cell == Cell.InvalidCell;
  }

  public boolean isFriendly(Agent agent) {
    return (id <2 && agent.id <2) || (id >=2 && agent.id >=2);
  }

  public void moveTo(Cell target) {
    pushTo(target);

  }

  public void pushTo(Cell target) {
    if (cell.agent == this) // TODO this if only for debug purpose ?
      cell.agent = null;
    this.cell = target;
    this.position = target.position;
    target.agent = this;
  }

}
