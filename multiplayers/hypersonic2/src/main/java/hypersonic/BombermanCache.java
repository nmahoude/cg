package hypersonic;

import hypersonic.entities.Bomb;
import hypersonic.entities.Bomberman;
import hypersonic.utils.Cache;

public class BombermanCache {
  public static Cache<Bomberman> cache = new Cache<>();
  static {
    for (int i=0;i<10000;i++) {
      cache.push(new Bomberman(i, null, i, i));
    }
  }
  public static Bomberman pop() {
    if (cache.isEmpty()) {
      return new Bomberman(-1, null, 0, 0);
    } else {
      return cache.pop();
    }
  }
  public static void push(Bomberman b) {
    cache.push(b);
  }
}
