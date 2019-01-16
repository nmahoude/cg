package hypersonic;

import hypersonic.entities.Bomb;
import hypersonic.utils.Cache;
import hypersonic.utils.P;

public class BombCache {
  public static Cache<Bomb> cache = new Cache<>();
  static {
    for (int i=0;i<10000;i++) {
      cache.push(new Bomb(-1, null, Bomb.DEFAULT_TIMER, Bomb.DEFAULT_RANGE));
    }
  }
  public static Bomb pop() {
    if (cache.isEmpty()) {
      return  new Bomb(-1, null, Bomb.DEFAULT_TIMER, Bomb.DEFAULT_RANGE);
    } else {
      return cache.pop();
    }
  }
  public static void push(Bomb b) {
    cache.retrocede(b);
  }
  public static Bomb pop(int owner, P p, int param1, int param2) {
    Bomb bomb = pop();
    bomb.owner = owner;
    bomb.position = p;
    bomb.timer = param1;
    bomb.range = param2;
    return bomb;
  }

}
