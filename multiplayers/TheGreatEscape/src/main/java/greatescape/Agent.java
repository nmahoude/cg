package greatescape;

public class Agent {
  public Point position;
  public int wallsLeft;
  public Target goal;
  
  public Agent() {
    position = new Point();
  }
  
  public boolean isDead() {
    return position.x == -1;
  }
  public boolean isAlive() {
    return position.x != -1;
  }
}
