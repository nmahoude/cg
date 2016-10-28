package hypersonic.entities;

import hypersonic.Board;
import hypersonic.utils.P;

public class Bomb extends Entity {
  public int timer;
  public int range;
  
  public Bomb(P position, int timer, int range) {
    super(EntityType.BOMB, position);
    this.timer = timer;
    this.range = range;
  }
  public void explode(Board board) {
    board.explode(this);
  }
  public final void update(Board board) {
    timer--;
    if (timer == 0) {
      explode(board);
    }
  }
}
