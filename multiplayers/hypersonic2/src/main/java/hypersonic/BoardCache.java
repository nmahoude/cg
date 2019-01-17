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
    for (int b=0;b<board.bombsFE;b++) {
      Bomb bomb = board.bombs[b];
      if( bomb != null) {
        BombCache.push(bomb); 
      }
    }
    for (int p=0;p<board.playersFE;p++) {
      Bomberman b = board.players[p];
      BombermanCache.push(b);
    }
    BoardCache.cache.push(board);
  }
}
