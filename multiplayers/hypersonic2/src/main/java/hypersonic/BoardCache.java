package hypersonic;

import java.util.ArrayDeque;
import java.util.Deque;

import hypersonic.entities.Bomb;
import hypersonic.entities.Bomberman;

public class BoardCache {
  static public Deque<Board> cache = new ArrayDeque<>();
  static {
    for (int i=0;i<10000;i++) {
      cache.push(new Board());
    }
  }
  
  public static void retrocede(final Board board) {
    for (final Bomb b : board.bombs) {
      BombCache.push(b); 
    }
    for (final Bomberman b : board.players) {
      Bomberman.cache.retrocede(b);
    }
    BoardCache.cache.push(board);
  }
}
