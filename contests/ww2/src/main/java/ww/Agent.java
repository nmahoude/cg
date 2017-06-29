package ww;

public class Agent {
  public final int id;
  
  public Point position;
  public Cell cell;
  public int score;
  
  public Point _position;
  public Cell _cell;
  public int _score;

  public Agent(int id) {
    this.id = id;
  }

  public void copyTo(GameState otherState, Agent agent) {
    agent.position = position;
    agent.cell = otherState.grid.get(position.x, position.y);
    agent.score = score;
  }

  public void backup() {
    _position = position;
    _cell = cell;
    _score = score;
  }

  public void restore() {
    position = _position;
    cell = _cell;
    score = _score;
  }

  public boolean isModified() {
    boolean b = position == _position && cell == _cell && score == _score;
    if (!b) {
      System.err.println("Difference in agent : "+position);
      System.err.println(""+position+" vs "+_position);
      System.err.println(""+cell+" vs "+_cell);
      System.err.println(""+score+" vs "+_score);
    }
    return !b;
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
