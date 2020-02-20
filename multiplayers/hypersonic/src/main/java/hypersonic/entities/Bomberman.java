package hypersonic.entities;

import hypersonic.Board;
import hypersonic.utils.Cache;
import hypersonic.utils.P;

public class Bomberman extends Entity {
  public static Cache<Bomberman> cache = new Cache<>();
  static {
    for (int i=0;i<10000;i++) {
      cache.push(new Bomberman(null, i, null, i, i));
    }
  }
  
  public int bombsLeft;
  public int currentRange;

  public boolean isDead = false;
  public double points = 0;
  public int bombCount = 0;
  
  public Bomberman(final Board board, final int owner, final P position, final int bombsLeft, final int currentRange) {
    super(board, owner, EntityType.PLAYER, position);
    this.bombsLeft = bombsLeft;
    this.currentRange = currentRange;
  }

  public void move(final P p) {
    if (board.canWalkOn(p)) {
      board.walkOn(this, p);
    }
  }

  public Bomberman duplicate(final Board board) {
    Bomberman b;
    if (cache.isEmpty()) {
      b = new Bomberman(board, owner, position, bombsLeft, currentRange);
    } else {
      b = cache.pop();
      b.board = board;
      b.owner = owner;
      b.position = position;
      b.bombsLeft = bombsLeft;
      b.currentRange = currentRange;
    }
    b.points = points;
    b.isDead = isDead;
    b.bombCount = bombCount;
    return b;
  }
  
  @Override
  public String toString() {
    return "Bomberman("+owner+"): pos="+position+" bLeft:"+bombsLeft+" cRange:"+currentRange +" isDead:"+isDead;
  }
}
