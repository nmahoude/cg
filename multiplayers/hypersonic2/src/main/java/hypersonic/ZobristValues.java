package hypersonic;

import hypersonic.entities.Bomb;
import hypersonic.utils.P;

public class ZobristValues {
  public static final long bombValues[][] = new long[Board.WIDTH * Board.HEIGHT][Player.DEPTH+1];
  public static final long itemsValues[][] = new long[Board.WIDTH * Board.HEIGHT][Player.DEPTH+1];
  public static final long positionsValues[][] = new long[Board.WIDTH * Board.HEIGHT][Player.DEPTH+1];

  static {
    for (int depth = 0; depth < Player.DEPTH+1; depth++) {
      for (int y = 0; y < Board.HEIGHT; y++) {
        for (int x = 0; x < Board.WIDTH; x++) {
          P p = P.get(x, y);
          bombValues[p.offset][depth] = Player.rand.nextLong();
          itemsValues[p.offset][depth] = Player.rand.nextLong();
          positionsValues[p.offset][depth] = Player.rand.nextLong();
        }
      }
    }
  }

  public static long fromBomb(Bomb bomb, int depth) {
    return bombValues[bomb.position.offset][depth];
  }

  public static long fromItem(P p, int depth) {
    return itemsValues[p.offset][depth];
  }
  
  public static long fromPosition(P p, int depth) {
    return positionsValues[p.offset][depth];
  }
}
