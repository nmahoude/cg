package cotc.game;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import cotc.utils.Coord;

public class Damage {
  @SafeVarargs
  static final <T> String join(T... v) {
      return Stream.of(v).map(String::valueOf).collect(Collectors.joining(" "));
  }

  private final Coord position;
  private final int health;
  private final boolean hit;

  public Damage(Coord position, int health, boolean hit) {
      this.position = position;
      this.health = health;
      this.hit = hit;
  }

  public String toViewString() {
      return join(position.y, position.x, health, (hit ? 1 : 0));
  }
}
