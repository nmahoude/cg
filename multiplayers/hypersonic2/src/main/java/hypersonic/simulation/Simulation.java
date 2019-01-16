package hypersonic.simulation;

import hypersonic.Board;
import hypersonic.BombCache;
import hypersonic.Move;
import hypersonic.entities.Bomb;
import hypersonic.utils.P;

public class Simulation {
  public Board board;
  
  public Simulation(Board board) {
    this.board = board;
  }

  public final boolean isFinished() {
    return false;
  }
  
  public final void simulate(final Move move) {
    // /!\ the first updateBombs should have been done before
    simulateMove(move);
    board.updateBombs();
  }
  
  private void simulateMove(final Move move) {
    int newX = board.me.position.x;
    int newY = board.me.position.y;
    boolean dropBomb = false;
    switch(move) {
      case DOWN_BOMB:
        dropBomb = true;
      case DOWN:
        newY+=1;
        break;
      case LEFT_BOMB:
        dropBomb = true;
      case LEFT:
        newX-=1;
        break;
      case RIGHT_BOMB:
        dropBomb = true;
      case RIGHT:
        newX+=1;
        break;
      case STAY_BOMB:
        dropBomb = true;
      case STAY:
        break;
      case UP_BOMB:
        dropBomb = true;
      case UP:
        newY-=1;
    }
    
    if (dropBomb) {
      Bomb newBomb = BombCache.pop(
          board.me.owner, 
          board.me.position, 
          board.turn + Bomb.DEFAULT_TIMER, 
          board.me.currentRange);
      board.addBomb(newBomb);

      board.me.bombsLeft-=1;
    }
    if (board.canWalkOn(P.get(newX, newY)) && (newX != board.me.position.x || newY != board.me.position.y)) {
      board.walkOn(board.me, P.get(newX, newY));
    }
  }
}
