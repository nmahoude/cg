package cotc.game;

import cotc.utils.Coord;
import cotc.utils.Util;

public class Damage {
  private final Coord position;
  private final int health;
  private final boolean hit;

  public Damage(Coord position, int health, boolean hit) {
      this.position = position;
      this.health = health;
      this.hit = hit;
  }

  public String toViewString() {
      return Util.join(position.y, position.x, health, (hit ? 1 : 0));
  }
}
