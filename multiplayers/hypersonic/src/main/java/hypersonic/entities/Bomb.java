package hypersonic.entities;

import hypersonic.Board;
import hypersonic.utils.P;

public class Bomb extends Entity {
  public int timer;
  public int range;
  
  public Bomb(Board board, int owner, P position, int timer, int range) {
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
  public Bomb duplicate(Board board) {
    Bomb b = new Bomb(board,owner, position, timer, range);
    return b;
  }
}
