package hypersonic;

import hypersonic.entities.Bomb;
import hypersonic.utils.CacheUtil;
import hypersonic.utils.P;

public class Cache {
  public static CacheUtil<Bomb> bombCache = new CacheUtil<>();
  static {
    for (int i=0;i<10000;i++) {
      bombCache.push(new Bomb(-1, null, Bomb.DEFAULT_TIMER, Bomb.DEFAULT_RANGE));
    }
  }
  
  public static Bomb popBomb() {
    if (bombCache.isEmpty()) {
      return  new Bomb(-1, null, Bomb.DEFAULT_TIMER, Bomb.DEFAULT_RANGE);
    } else {
      return bombCache.pop();
    }
  }
  public static void pushBomb(Bomb b) {
    bombCache.retrocede(b);
  }
  public static Bomb popBomb(int owner, P p, int param1, int param2) {
    Bomb bomb = popBomb();
    bomb.owner = owner;
    bomb.position = p;
    bomb.timer = param1;
    bomb.range = param2;
    return bomb;
  }

}
