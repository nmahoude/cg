package hypersonic.entities;

import hypersonic.Board;
import hypersonic.BombCache;
import hypersonic.utils.P;

public class Bomb {

  public static final int DEFAULT_TIMER = 8;
  public static final int DEFAULT_RANGE = 3;
  
  // TODO pseudo immutable !
  public int owner;
  public int timer;
  public int range;
  public P position;
  
  public Bomb(int owner, P position, int timer, int range) {
    this.owner = owner;
    this.timer = timer;
    this.range = range;
    this.position = position;
  }
  
  public Bomb duplicate(final Board board) {
    Bomb b = BombCache.pop();
    b.owner = owner;
    b.position = position;
    b.timer = timer;
    b.range = range;
    return b;
  }
  
  @Override
  public String toString() {
    return String.format("[%d] %s (%d) <= %d =>", owner, position, timer, range);
  }
}
