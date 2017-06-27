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

  public boolean inFogOfWar() {
    return cell == Cell.InvalidCell;
  }

  public boolean isFriendly(Agent agent) {
    return (id <2 && agent.id <2) || (id >=2 && agent.id >=2);
  }

  public void moveTo(Cell target) {
    pushTo(target);
    if (target.height == Cell.FINAL_HEIGHT - 1) {
      score++;
    }

  }

  public void pushTo(Cell target) {
    if (cell.agent == this) // TODO this if only for debug purpose ?
      cell.agent = null;
    this.cell = target;
    target.agent = this;
  }

}
