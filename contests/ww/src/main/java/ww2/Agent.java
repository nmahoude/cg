package ww2;

public class Agent {
  public final int id;
  
  public Point position = Point.unknown;
  int score = 0;
  
  Point _position;
  int _score;
  
  public Agent(int id) {
    this.id = id;
  }
  
  public void backup() {
    _position = position;
    _score = score;
  }
  public void restore() {
    score = _score;
    position = _position;
  }

  public boolean inFogOfWar() {
    return position == Point.unknown;
  }
  
}
