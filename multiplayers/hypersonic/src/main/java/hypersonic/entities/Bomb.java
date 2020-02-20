package hypersonic.entities;

import hypersonic.Board;
import hypersonic.utils.Cache;
import hypersonic.utils.P;

public class Bomb extends Entity {
  public static int DEFAULT_TIMER = 8;
  
  public static Cache<Bomb> cache = new Cache<>();
  static {
    for (int i=0;i<10000;i++) {
      cache.push(new Bomb(null, i, null, i, i));
    }
  }

  public int timer;
  public int range;
  
  private Bomb(final Board board, final int owner, final P position, final int timer, final int range) {
    super(board, owner, EntityType.BOMB, position);
    this.timer = timer;
    this.range = range;
  }
  public void explode() {
    board.explode(this);
  }
  public final void update() {
    timer--;
    if (timer == 0) {
      explode();
    }
  }
  
  public Bomb duplicate(final Board board) {
    Bomb b;
    if (cache.isEmpty()) {
      b = new Bomb(board,owner, position, timer, range);
    } else {
      b = cache.pop();
      b.board = board;
      b.owner = owner;
      b.position = position;
      b.timer = timer;
      b.range = range;
    }
    return b;
  }
  public static Bomb create(final Board board, final int owner, final P position, final int timer, final int range) {
    Bomb b;
    if (cache.isEmpty()) {
      b = new Bomb(board,owner, position, timer, range);
    } else {
      b = cache.pop();
      b.board = board;
      b.owner = owner;
      b.position = position;
      b.timer = timer;
      b.range = range;
    }
    return b;
  }
}
