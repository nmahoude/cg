package coderoyale.units;

import coderoyale.Pos;

public class Disk {
  public Pos pos = new Pos();
  public int radius;

  public Disk(int x, int y, int radius) {
    this.radius = radius;
    pos.x = x;
    pos.y = y;
  }
}
