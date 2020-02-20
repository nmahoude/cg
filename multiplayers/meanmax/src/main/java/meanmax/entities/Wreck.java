package meanmax.entities;

import meanmax.Game;
import meanmax.Player;

public class Wreck extends Entity {
  public int water;

  private int b_water;
  public void backup() {
    super.backup();
    b_water = water;
  }
  public void restore() { 
    super.restore();
    water = b_water;
  }
  
  public Wreck() {
    super(Game.WRECK, 0.0, 0.0, false);
  }

  public void update(int x, int y, int water, double radius) {
    this.position.x = x;
    this.position.y = y;
    this.water = water;
    this.radius = radius;
  }

  public void harvest(Player p) {
    if (isInRange(p.reaper, radius)) {
      p.score += 1;
      water -= 1;
    }
    if (water <=0) dead = true;
  }

  void readExtra(int extra, int extra2) {
    water = extra;
  }
}
