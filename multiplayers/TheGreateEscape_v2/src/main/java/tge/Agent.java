package tge;

import java.util.List;

public class Agent {
  public int id;
  public Point position;
  public int wallLefts;

  public List<Cell> currentPath;
  public int currentMax;
  public boolean clearPath;
  public int maxPath;

  public Agent(int id) {
    this.id = id;
  }
}
