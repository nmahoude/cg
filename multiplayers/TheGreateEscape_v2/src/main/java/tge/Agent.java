package tge;

public class Agent {
  public int id;
  public Point position;
  public int wallLefts;

  public int currentDist;
  public int currentMax;

  public Agent(int id) {
    this.id = id;
  }
}
