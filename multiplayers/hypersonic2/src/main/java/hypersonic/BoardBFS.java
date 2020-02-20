package hypersonic;

import java.util.ArrayList;
import java.util.List;

import hypersonic.utils.P;

public class BoardBFS {

  /** 
   * calculate the number of move are possible from a pos 
   */
  int movements(Board board, P initialPos) {
    List<P> visited = new ArrayList<>();
    List<P> toVisit = new ArrayList<>();
    int total = 1;
    
    Move dirs[] = { Move.UP, Move.RIGHT, Move.DOWN, Move.LEFT};
    toVisit.add(initialPos);
    while (!toVisit.isEmpty()) {
      P pos = toVisit.remove(0);
      visited.add(pos);
      
      for (int i=0;i<4;i++) {
        if (pos.x + dirs[i].dx == -1 || pos.x + dirs[i].dx == Board.WIDTH) continue;
        if (pos.y + dirs[i].dy == -1 || pos.y + dirs[i].dy == Board.HEIGHT) continue;
        
        P newPos = pos.move(dirs[i]);
        if ((newPos.x & 0b1) != 0 && (newPos.y & 0b1) != 0) continue;
        if (!visited.contains(newPos) && board.canMoveTo(newPos)) {
          toVisit.add(newPos);
          total++;
        }
      }
    }
    return total;
  }
}
