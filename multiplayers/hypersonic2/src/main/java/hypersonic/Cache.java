package hypersonic;

import java.util.ArrayDeque;
import java.util.Deque;

import hypersonic.entities.Bomb;
import hypersonic.entities.Bomberman;
import hypersonic.utils.CacheUtil;
import hypersonic.utils.P;

public class Cache {
  public static CacheUtil<Bomb> bombCache = new CacheUtil<>();
  public static CacheUtil<Bomberman> bomberMencache = new CacheUtil<>();
  static public Deque<State> stateCache = new ArrayDeque<>();
  static {
    for (int i=0;i<10000;i++) {
      bombCache.push(new Bomb(-1, null, Bomb.DEFAULT_TIMER, Bomb.DEFAULT_RANGE));
    }
    for (int i=0;i<10000;i++) {
      bomberMencache.push(new Bomberman(i, null, i, i));
    }
    for (int i=0;i<10000;i++) {
      stateCache.push(new State());
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

  // Bombermen

  public static Bomberman popBomberman() {
    if (bomberMencache.isEmpty()) {
      return new Bomberman(-1, null, 0, 0);
    } else {
      return bomberMencache.pop();
    }
  }
  public static void pushBomberman(Bomberman b) {
    bomberMencache.push(b);
  }
  
  // ---- state
  
}
