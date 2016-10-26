package hypersonic;

import hypersonic.utils.P;

public class Bomb {
  P p = new P();
  public int timer;
  public int range;
  
  public void explode(Board board) {
    board.explode(p, range);
  }
}
